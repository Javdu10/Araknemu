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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.turn;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.event.NextTurnInitiated;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnListChanged;
import fr.quatrevieux.araknemu.game.fight.turn.order.FighterOrderStrategy;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handle fight turns
 */
final public class FightTurnList {
    final private Fight fight;

    private List<Fighter> fighters;
    private int index;
    private Fighter current;
    private FightTurn turn;
    final private AtomicBoolean active = new AtomicBoolean(false);

    public FightTurnList(Fight fight) {
        this.fight = fight;
    }

    /**
     * Initialise the fighters order
     */
    public void init(FighterOrderStrategy orderStrategy) {
        if (fighters != null) {
            throw new IllegalStateException("FightTurnList is already initialised");
        }

        fighters = orderStrategy.compute(fight.teams());
        current = fighters.get(0); // Always init the first fighter
    }

    /**
     * Get all fighters ordered by their turn order
     */
    public List<Fighter> fighters() {
        return fighters;
    }

    /**
     * Remove a fighter from turn list
     *
     * @param fighter Fighter to remove
     */
    public void remove(Fighter fighter) {
        final int index = fighters.indexOf(fighter);

        if (index == -1) {
            throw new NoSuchElementException("Fighter " + fighter.id() + " is not found on the turn list");
        }

        fighters.remove(index);

        // The removed fighter is the current fighter or before on the list
        // so removing it will shift the list to the left relatively to the cursor (current)
        // which cause that the next fighter will be skipped
        // See: https://github.com/Arakne/Araknemu/issues/127
        if (index <= this.index) {
            // If current is negative, move cursor to the end
            if (--this.index < 0) {
                this.index += fighters.size();
            }
        }

        fight.dispatch(new TurnListChanged(this));
    }

    /**
     * Get the current turn
     */
    public Optional<FightTurn> current() {
        return Optional.ofNullable(turn);
    }

    /**
     * Get the current turn fighter
     */
    public Fighter currentFighter() {
        return current;
    }

    /**
     * Start the turn system
     */
    public void start() {
        if (active.getAndSet(true)) {
            throw new IllegalStateException("TurnList already started");
        }

        index = -1;

        next();
    }

    /**
     * Stop the turn system
     */
    public void stop() {
        if (!active.getAndSet(false)) {
            return;
        }

        if (turn != null) {
            turn.stop();
            turn = null;
        }
    }

    /**
     * Stop the current turn and start the next
     *
     * @todo test start with return false
     */
    void next() {
        turn = null;
        fight.dispatch(new NextTurnInitiated());

        while (active.get()) {
            if (++index >= fighters.size()) {
                index = 0;
            }

            if (fighters.get(index).dead()) {
                continue;
            }

            current = fighters.get(index);
            turn = new FightTurn(current, fight, fight.type().turnDuration());

            if (turn.start()) {
                break;
            }
        }
    }
}
