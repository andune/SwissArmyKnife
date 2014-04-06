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
package com.andune.sak.modules;

import com.andune.sak.SwissArmyKnife;
import com.andune.sak.util.BukkitEventUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;

/**
 * @author andune
 */
public class ListenerInfo {
    private final SwissArmyKnife sakPlugin;

    public ListenerInfo(SwissArmyKnife sakPlugin) {
        this.sakPlugin = sakPlugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("eld")) {
            eldump(args, sender);
            return true;
        }
        else if (label.equalsIgnoreCase("ev")) {
            event(args, sender);
            return true;
        }
        else if (label.equalsIgnoreCase("pl")) {
            event(args, sender);
            return true;
        }
        else {
            return false;
        }
    }

    public void eldump(String[] args, CommandSender sender) {
        for (Class<Event> clazz : sakPlugin.getEventClasses()) {
            HandlerList handlers = BukkitEventUtils.instance().getEventListeners(clazz);
//				if( handlers == null )
//					continue;
            RegisteredListener[] listeners = handlers.getRegisteredListeners();
            if (listeners.length == 0) continue;

            System.out.println(clazz);
            for (int i = 0; i < listeners.length; i++) {
                sender.sendMessage("  Listener " + i + ": Plugin \"" + listeners[i].getPlugin()
                        + "\" [priority " + listeners[i].getPriority() + "]");
            }
        }
    }

    public void event(String[] args, CommandSender sender) {
        if( args.length < 1 ) {
            sender.sendMessage("Invalid argument, event class name required");
            return;
        }

        final String arg0 = args[0];
        for (Class<Event> clazz : sakPlugin.getEventClasses()) {
            if (!clazz.getName().contains(arg0))
                continue;

            HandlerList handlers = BukkitEventUtils.instance().getEventListeners(clazz);
            RegisteredListener[] listeners = handlers.getRegisteredListeners();
            if (listeners.length == 0) continue;

            System.out.println(clazz);
            for (int i = 0; i < listeners.length; i++) {
                sender.sendMessage("  Listener " + i + ": Plugin \"" + listeners[i].getPlugin()
                        + "\" [priority " + listeners[i].getPriority() + "]");
            }
        }
    }

    public void plugin(String[] args, CommandSender sender) {
        if( args.length < 1 ) {
            sender.sendMessage("Invalid argument, plugin name required");
            return;
        }

        final String arg0 = args[0];
        for (Class<Event> clazz : sakPlugin.getEventClasses()) {
            HandlerList handlers = BukkitEventUtils.instance().getEventListeners(clazz);
            RegisteredListener[] listeners = handlers.getRegisteredListeners();
            if (listeners.length == 0) continue;

            String title = clazz.toString() + ":";
            for (int i = 0; i < listeners.length; i++) {
                if (!listeners[i].getPlugin().getName().contains(arg0))
                    continue;
                if (title != null) {
                    sender.sendMessage(title);
                    title = null;
                }
                sender.sendMessage("  Listener " + i + ": Plugin \"" + listeners[i].getPlugin()
                        + "\" [priority " + listeners[i].getPriority() + "]");
            }
        }
    }

}
