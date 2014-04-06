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
/**
 * 
 */
package com.andune.sak.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.IllegalPluginAccessException;

/**
 * @author andune
 *
 */
public class BukkitEventUtils {
	private static BukkitEventUtils instance = new BukkitEventUtils();
	
	// Singleton pattern
	private BukkitEventUtils() {}
	public static BukkitEventUtils instance() {
		return instance;
	}
	
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Set<Class<Event>> populateEventClasses() throws IOException, ClassNotFoundException {
    	final Set<Class<Event>> eventClasses = new HashSet<Class<Event>>(30);
    	
//    	Package[] packages = Package.getPackages();
//    	for(int i=0; i < packages.length; i++) {
//    		System.out.println("DEBUG: checking package "+packages[i]);
    		Class[] classes = JavaUtils.getClasses("org.bukkit.event");
    		if( classes.length > 0 )
//    			System.out.println("Found "+classes.length+" classes for package "+"org.bukkit.event");
    		
    		for(int j=0; j < classes.length; j++) {
    			if( classes[j] == null || classes[j].getName() == null )
    				continue;
//    			if( classes[j].getName().contains("org.bukkit.event") )
//    				System.out.println("DEBUG: checking class "+classes[j]);
    			
    			try {
					Class a = classes[j].asSubclass(Event.class);
					if( a != null ) {
//        				System.out.println("DEBUG: ADDING class "+classes[j]);
						HandlerList handlers = getEventListeners((Class<Event>) classes[j]);
						// handlers will be non-null for any class that has the method defined
						// if the method isn't defined, handlers will be null, meaning the class
						// cannot accept events, so we ignore it
						if( handlers != null )
							eventClasses.add((Class<Event>) classes[j]);
					}
    					
    			}
    			catch(ClassCastException e) {}
    		}
//    	}

    	return eventClasses;
    }
    
    public HandlerList getEventListeners(Class<? extends Event> type) {
    	Class<? extends Event> clazz = null;
        try {
        	clazz = getRegistrationClass(type);
        } catch (Exception e) {
//            throw new IllegalPluginAccessException(e.toString());
        }
        
        if( clazz == null )
        	return null;
        
        try {
            Method method = clazz.getDeclaredMethod("getHandlerList");
            method.setAccessible(true);
            return (HandlerList) method.invoke(null);
        } catch (Exception e) {
            throw new IllegalPluginAccessException(e.toString());
        }
    }

    private Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) {
        try {
            clazz.getDeclaredMethod("getHandlerList");
            return clazz;
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null
                    && !clazz.getSuperclass().equals(Event.class)
                    && Event.class.isAssignableFrom(clazz.getSuperclass())) {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            } else {
                throw new IllegalPluginAccessException("Unable to find handler list for event " + clazz.getName());
            }
        }
    }
    

}
