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

package fr.quatrevieux.araknemu.game.admin.context;

import fr.quatrevieux.araknemu.game.admin.Command;

/**
 * Configure a context by adding commands and sub-contexts
 * This class must be extended on a module
 */
abstract public class ContextConfigurator<C extends Context> implements Cloneable {
    private SimpleContext context;

    /**
     * Define the context to be configured
     */
    @SuppressWarnings("unchecked")
    final public ContextConfigurator<C> with(SimpleContext context) {
        ContextConfigurator<C> withContext =  null;

        try {
            withContext = (ContextConfigurator<C>) clone();
            withContext.context = context;
        } catch (CloneNotSupportedException e) {}

        return withContext;
    }

    /**
     * Configure the context
     */
    abstract public void configure(C context);

    /**
     * Add a new command to the context
     */
    final public void add(Command command) {
        context.add(command);
    }

    /**
     * Add a new child context
     *
     * @param name The child name
     * @param child The child
     */
    final public void add(String name, Context child) {
        context.add(name, child);
    }
}
