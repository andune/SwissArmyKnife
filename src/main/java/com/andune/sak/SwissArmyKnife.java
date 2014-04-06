/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2014 Andune (andune.alleria@gmail.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.andune.sak;

import com.andune.minecraft.commonlib.JarUtils;
import com.andune.sak.modules.ListenerInfo;
import com.andune.sak.util.BukkitEventUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author andune
 */
public class SwissArmyKnife extends JavaPlugin {
    private Set<Class<Event>> eventClasses = new HashSet<Class<Event>>(30);
    private JarUtils jarUtils;
    private String buildNumber = "unknown";
    private Logger log;
    private ListenerInfo listenerModule;

    @Override
    public void onEnable() {
        log = getLogger();
        jarUtils = new JarUtils(this.getDataFolder(), getFile());
        buildNumber = jarUtils.getBuild();

        try {
            eventClasses = BukkitEventUtils.instance().populateEventClasses();
        } catch (Exception e) {
            e.printStackTrace();
        }
//		dumpEventClasses();

        listenerModule = new ListenerInfo(this);

        log.info("version " + getDescription().getVersion() + ", build " + buildNumber + " is enabled");
    }

    @Override
    public void onDisable() {
        log.info("version " + getDescription().getVersion() + ", build " + buildNumber + " is disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if( listenerModule.onCommand(sender, command, label, args) )
            return true;

        return false;
    }

    public Set<Class<Event>> getEventClasses() {
        return eventClasses;
    }
}
