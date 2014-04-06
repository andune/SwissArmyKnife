/**
 * 
 */
package com.andune.sak.modules;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;
import com.andune.sak.SwissArmyKnife;
import com.andune.sak.util.BukkitEventUtils;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;

/**
 * @author andune
 *
 */
@ComponentInformation(friendlyName = "ListenerInfo", desc = "ListenerInfo contains commands that allow "+
        "admins to gather information about event listeners")
public class ListenerInfo extends BukkitComponent {
    @Override
    public void enable() {
        registerCommands(Commands.class);
    }

    @Override
    public void reload() {
        super.reload();
    }

    public class Commands {
        @Command(aliases = {"eld"},
//                usage = "", flags = "do",
                desc = "Dump event listeners",
                min = 0, max = 0)
        @CommandPermissions({"sak.admin"})
        public void eldump(CommandContext args, CommandSender sender) throws CommandException {
	    	for(Class<Event> clazz : SwissArmyKnife.instance().getEventClasses()) {
				HandlerList handlers = BukkitEventUtils.instance().getEventListeners(clazz);
//				if( handlers == null )
//					continue;
				RegisteredListener[] listeners = handlers.getRegisteredListeners();
				if( listeners.length == 0 ) continue;
				
	    		System.out.println(clazz);
				for(int i=0; i < listeners.length; i++) {
					sender.sendMessage("  Listener "+i+": Plugin \""+ listeners[i].getPlugin()
							+"\" [priority "+listeners[i].getPriority()+"]");
				}
	    	}
        }
        
        @Command(aliases = {"ev"},
        		desc = "Show listeners for a specific event",
        		min = 1, max = 1)
        @CommandPermissions({"sak.admin"})
        public void event(CommandContext args, CommandSender sender) throws CommandException {
        	final String arg0 = args.getString(0);
        	for(Class<Event> clazz : SwissArmyKnife.instance().getEventClasses()) {
        		if( !clazz.getName().contains(arg0) )
        			continue;
        		
        		HandlerList handlers = BukkitEventUtils.instance().getEventListeners(clazz);
        		RegisteredListener[] listeners = handlers.getRegisteredListeners();
        		if( listeners.length == 0 ) continue;

        		System.out.println(clazz);
        		for(int i=0; i < listeners.length; i++) {
        			sender.sendMessage("  Listener "+i+": Plugin \""+ listeners[i].getPlugin()
        					+"\" [priority "+listeners[i].getPriority()+"]");
        		}
        	}
        }
        
        @Command(aliases = {"pl"},
        		desc = "Show all events a plugin is subscribed to",
        		min = 1, max = 1)
        @CommandPermissions({"sak.admin"})
        public void plugin(CommandContext args, CommandSender sender) throws CommandException {
        	final String arg0 = args.getString(0);
	    	for(Class<Event> clazz : SwissArmyKnife.instance().getEventClasses()) {
				HandlerList handlers = BukkitEventUtils.instance().getEventListeners(clazz);
				RegisteredListener[] listeners = handlers.getRegisteredListeners();
				if( listeners.length == 0 ) continue;
				
				String title = clazz.toString() + ":";
				for(int i=0; i < listeners.length; i++) {
					if( !listeners[i].getPlugin().getName().contains(arg0) )
						continue;
					if( title != null ) {
						sender.sendMessage(title);
						title = null;
					}
					sender.sendMessage("  Listener "+i+": Plugin \""+ listeners[i].getPlugin()
							+"\" [priority "+listeners[i].getPriority()+"]");
				}
	    	}
        }
    }
}
