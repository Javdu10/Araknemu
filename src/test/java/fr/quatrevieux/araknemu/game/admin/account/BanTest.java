/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin.account;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.common.account.banishment.BanishmentService;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.account.Banishment;
import fr.quatrevieux.araknemu.data.living.repository.account.BanishmentRepository;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.out.ServerMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class BanTest extends CommandTestCase {
    private GameAccount target;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        command = new Ban(
            target = new GameAccount(
                new Account(5, "target", "", "to ban"),
                container.get(AccountService.class),
                2
            ),
            container.get(BanishmentService.class)
        );

        dataSet.use(Banishment.class);
    }

    @Test
    void banInvalidAction() {
        assertThrows(CommandException.class, () -> execute("ban"));
        assertThrows(CommandException.class, () -> execute("ban", "invalid"));
    }

    @Test
    void banListEmpty() throws SQLException, AdminException {
        execute("ban", "list");

        assertOutput("No ban entries found");
    }

    @Test
    void banList() throws SQLException, AdminException {
        dataSet.push(new Banishment(target.id(), Instant.parse("2020-03-25T15:00:00.Z"), Instant.parse("2020-03-30T15:00:00.Z"), "ban 1", 1));
        dataSet.push(new Banishment(target.id(), Instant.parse("2020-07-25T15:00:00.Z"), Instant.parse("2020-07-30T15:00:00.Z"), "ban 2", -1));
        Banishment active = dataSet.push(new Banishment(target.id(), Instant.now(), Instant.now().plus(1, ChronoUnit.HOURS), "current", -1));

        execute("ban", "list");
        assertOutput(
            "List of ban entries for to ban :",
            active.startDate().with(ChronoField.MICRO_OF_SECOND, 0) + " - " + active.endDate().with(ChronoField.MICRO_OF_SECOND, 0) + " (by system) : current <b>active</b>",
            "2020-07-25T15:00:00Z - 2020-07-30T15:00:00Z (by system) : ban 2",
            "2020-03-25T15:00:00Z - 2020-03-30T15:00:00Z (by bob) : ban 1"
        );
    }

    @Test
    void banFor() throws SQLException, AdminException {
        execute("ban", "for", "1h", "my", "ban", "message");

        assertTrue(container.get(BanishmentService.class).isBanned(target));

        Banishment banishment = container.get(BanishmentRepository.class).forAccount(target.id()).get(0);

        assertBetween(0, 1, Instant.now().getEpochSecond() - banishment.startDate().getEpochSecond());
        assertBetween(2599, 3601, banishment.endDate().getEpochSecond() - Instant.now().getEpochSecond());
        assertEquals("my ban message", banishment.cause());
        assertEquals(1, banishment.banisherId());
    }

    @Test
    void banFunctionalShouldKickBannedAccount() throws SQLException, AdminException {
        GameSession targetSession = server.createSession();
        target.attach(targetSession);

        execute("ban", "for", "1h", "my", "ban", "message");

        assertEquals(ServerMessage.kick("bob", "my ban message").toString(), ((DummyChannel) targetSession.channel()).getMessages().peek().toString());
        assertFalse(targetSession.isAlive());
    }

    @Test
    void banForInvalidArguments() {
        assertThrows(CommandException.class, () -> execute("ban", "for"));
        assertThrows(CommandException.class, () -> execute("ban", "for", "invalid"));
        assertThrows(CommandException.class, () -> execute("ban", "for", "1h"));
    }

    @Test
    void banForCannotBanSelf() {
        login();
        command = new Ban(
            session.account(),
            container.get(BanishmentService.class)
        );

        assertThrows(CommandException.class, () -> execute("ban", "for", "1h", "cause"));
    }

    @Test
    void banForCannotBanGameMaster() {
        command = new Ban(
            target = new GameAccount(
                new Account(5, "target", "", "to ban", EnumSet.allOf(Permission.class), "", ""),
                container.get(AccountService.class),
                2
            ),
            container.get(BanishmentService.class)
        );

        assertThrows(CommandException.class, () -> execute("ban", "for", "1h", "cause"));
    }

    @Test
    void unban() throws SQLException, AdminException {
        container.get(BanishmentService.class).ban(target, Duration.ofHours(1), "my cause");

        execute("ban", "unban");

        assertFalse(container.get(BanishmentService.class).isBanned(target));
        assertOutput("The account to ban has been unbaned");
    }

    @Test
    void help() {
        String help = command.help();

        assertTrue(help.contains("Ban an account"));
        assertTrue(help.contains("[context] ban [for|list|unban] [arguments]"));
    }
}
