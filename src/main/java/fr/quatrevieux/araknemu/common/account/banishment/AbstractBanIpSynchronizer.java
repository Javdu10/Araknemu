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

package fr.quatrevieux.araknemu.common.account.banishment;

import fr.quatrevieux.araknemu.common.account.LivingAccount;
import fr.quatrevieux.araknemu.common.account.banishment.event.IpBanned;
import fr.quatrevieux.araknemu.common.account.banishment.listener.KickBannedIpSession;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.core.network.session.Session;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Base implementation for synchronize the banip table with database and between servers
 */
abstract public class AbstractBanIpSynchronizer {
    final private BanIpService<? extends LivingAccount> service;
    final private Supplier<Collection<? extends Session>> sessionsSupplier;
    final private Logger logger;
    final private Duration refreshDelay;

    final private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public AbstractBanIpSynchronizer(BanIpService<? extends LivingAccount> service, Supplier<Collection<? extends Session>> sessionsSupplier, Logger logger, Duration refreshDelay) {
        this.service = service;
        this.sessionsSupplier = sessionsSupplier;
        this.logger = logger;
        this.refreshDelay = refreshDelay;
    }

    /**
     * Start the refresh pull thread
     */
    final protected void startPulling() {
        logger.info("Loading ban ip table");
        service.load();

        executor.scheduleAtFixedRate(() -> {
            logger.info("Refresh ban ip table");

            try {
                service.refresh();
            } catch (RuntimeException e) {
                logger.error("Error during refreshing ban ip table", e);
            }
        }, refreshDelay.toMillis(), refreshDelay.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Stop the refresh pull thread
     */
    final protected void stopPulling() {
        executor.shutdownNow();
    }

    /**
     * Get the kick banned ip listener
     */
    final protected Listener<IpBanned> ipBannedListener() {
        return new KickBannedIpSession(sessionsSupplier);
    }
}
