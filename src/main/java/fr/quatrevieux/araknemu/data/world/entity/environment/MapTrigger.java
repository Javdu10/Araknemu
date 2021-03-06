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

package fr.quatrevieux.araknemu.data.world.entity.environment;

/**
 * Map cell triggers
 * Perform an action when player arrive on the cell
 */
final public class MapTrigger {
    final private int map;
    final private int cell;
    final private int action;
    final private String arguments;
    final private String conditions;

    public MapTrigger(int map, int cell, int action, String arguments, String conditions) {
        this.map = map;
        this.cell = cell;
        this.action = action;
        this.arguments = arguments;
        this.conditions = conditions;
    }

    public int map() {
        return map;
    }

    public int cell() {
        return cell;
    }

    public int action() {
        return action;
    }

    public String arguments() {
        return arguments;
    }

    public String conditions() {
        return conditions;
    }
}
