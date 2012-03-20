/**
 * 
 */
package org.morganm.sak;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.morganm.sak.util.JarUtils;
import org.morganm.sak.util.JavaUtils;

/**
 * @author morganm
 *
 */
public class SwissArmyKnife extends JavaPlugin {
    public static final Logger log = Logger.getLogger("SwissArmyKnife");
    public static final String logPrefix = "[SwissArmyKnife]";
    
	private Set<Class<Event>> eventClasses = new HashSet<Class<Event>>(30);
	private JarUtils jarUtils;
	private int buildNumber = -1;
	
	@Override
	public void onEnable() {
    	jarUtils = new JarUtils(this, getFile(), log, logPrefix);
		buildNumber = jarUtils.getBuildNumber();
		
		try {
			populateEventClasses();
		}
		catch(Exception e) { e.printStackTrace(); }
		dumpEventClasses();
		
		log.info(logPrefix + " version "+getDescription().getVersion()+", build "+buildNumber+" is enabled");
	}
	
	@Override
	public void onDisable() {
		log.info(logPrefix + " version "+getDescription().getVersion()+", build "+buildNumber+" is disabled");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args)
	{
		if( args.length > 0 ) {
			if( args[0].equals("eventlisteners") || args[0].equals("el") ) {
				sender.sendMessage("Dumping event listeners:");
				HandlerList handlers = getEventListeners(BlockPlaceEvent.class);
				RegisteredListener[] listeners = handlers.getRegisteredListeners();
				for(int i=0; i < listeners.length; i++) {
					sender.sendMessage("Listener "+i+": Plugin \""+ listeners[i].getPlugin()
							+"\" [priority "+listeners[i].getPriority()+"]");
				}
				
				return true;
			}
			
			if( args[0].equals("allel") ) {
		    	for(Class<Event> clazz : eventClasses) {
					HandlerList handlers = getEventListeners(clazz);
					if( handlers == null )
						continue;
					RegisteredListener[] listeners = handlers.getRegisteredListeners();
					if( listeners.length == 0 ) continue;
					
		    		System.out.println(clazz);
					for(int i=0; i < listeners.length; i++) {
						sender.sendMessage("  Listener "+i+": Plugin \""+ listeners[i].getPlugin()
								+"\" [priority "+listeners[i].getPriority()+"]");
					}
		    	}
				
				return true;
			}
		}
		return false;
	}
	
    private HandlerList getEventListeners(Class<? extends Event> type) {
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
    
    public void dumpEventClasses() {
    	for(Class<Event> clazz : eventClasses) {
    		System.out.println(clazz);
    	}
    }
    
    @SuppressWarnings("unchecked")
	public void populateEventClasses() throws IOException, ClassNotFoundException {
//    	Package[] packages = Package.getPackages();
//    	for(int i=0; i < packages.length; i++) {
//    		System.out.println("DEBUG: checking package "+packages[i]);
    		Class[] classes = JavaUtils.getClasses("org.bukkit.event");
    		if( classes.length > 0 )
    			System.out.println("Found "+classes.length+" classes for package "+"org.bukkit.event");
    		
    		for(int j=0; j < classes.length; j++) {
    			if( classes[j] == null || classes[j].getName() == null )
    				continue;
    			if( classes[j].getName().contains("org.bukkit.event") )
    				System.out.println("DEBUG: checking class "+classes[j]);
    			
    			try {
					Class a = classes[j].asSubclass(Event.class);
					if( a != null ) {
        				System.out.println("DEBUG: ADDING class "+classes[j]);
    					eventClasses.add((Class<Event>) classes[j]);
					}
    					
//    				Object o = null;
//					try {
//						o = classes[j].newInstance();
//					} catch (InstantiationException e) {
//					} catch (IllegalAccessException e) {
//					}
//    				if( o instanceof Event ) {
//        				System.out.println("DEBUG: ADDING class "+classes[j]);
//    					eventClasses.add((Class<Event>) o);
//    				}
					
					
//    				Object o = classes[j].cast(Event.class);
    			}
    			catch(ClassCastException e) {}
    		}
//    	}
    }
    
    /*
    public static void find(String pckgname, Class clazz) {
        // Code from JWhich
        // ======
        // Translate the package name into an absolute path
        String name = new String(pckgname);
        if (!name.startsWith("/")) {
            name = "/" + name;
        }        
        name = name.replace('.','/');
        
        // Get a File object for the package
        URL url = Launcher.class.getResource(name);
        File directory = new File(url.getFile());
        // New code
        // ======
        if (directory.exists()) {
            // Get the list of the files contained in the package
            String [] files = directory.list();
            for (int i=0;I<files.length;i++) {
                 
                // we are only interested in .class files
                if (files[i].endsWith(".class")) {
                    // removes the .class extension
                    String classname = files[i].substring(0,files[i].length()-6);
                    try {
                        // Try to create an instance of the object
                        Object o = Class.forName(pckgname+"."+classname).newInstance();
                        if (o instanceof Command) {
                            System.out.println(classname);
                        }
                    } catch (ClassNotFoundException cnfex) {
                        System.err.println(cnfex);
                    } catch (InstantiationException iex) {
                        // We try to instantiate an interface
                        // or an object that does not have a 
                        // default constructor
                    } catch (IllegalAccessException iaex) {
                        // The class is not public
                    }
                }
            }
        }
    }
    
    public static void find(String tosubclassname) {
        try {
            Class tosubclass = Class.forName(tosubclassname);
            Package [] pcks = Package.getPackages();
            for (int i=0;I<pcks.length;i++) {
                find(pcks[i].getName(),tosubclass);
            }
        } catch (ClassNotFoundException ex) {
            System.err.println("Class "+tosubclassname+" not found!");
        }
    }
    */
}
