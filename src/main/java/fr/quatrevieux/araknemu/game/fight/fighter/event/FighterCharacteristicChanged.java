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

package fr.quatrevieux.araknemu.game.fight.fighter.event;

import fr.quatrevieux.araknemu.data.constant.Characteristic;

/**
 * The fighter has changed a characteristic value
 */
final public class FighterCharacteristicChanged {
    final private Characteristic characteristic;
    final private int value;

    public FighterCharacteristicChanged(Characteristic characteristic, int value) {
        this.characteristic = characteristic;
        this.value = value;
    }

    public Characteristic characteristic() {
        return characteristic;
    }

    public int value() {
        return value;
    }
}
