/**
 * 
 */
package org.morganm.sak.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.IllegalPluginAccessException;

/**
 * @author morganm
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
