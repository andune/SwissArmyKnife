/**
 * 
 */
package com.andune.sak;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.andune.sak.util.BukkitEventUtils;
import com.andune.sak.util.JarUtils;
import org.bukkit.event.Event;
import org.yaml.snakeyaml.error.YAMLException;

import com.sk89q.util.yaml.YAMLFormat;
import com.sk89q.util.yaml.YAMLProcessor;
import com.zachsthings.libcomponents.InjectComponent;
import com.zachsthings.libcomponents.InjectComponentAnnotationHandler;
import com.zachsthings.libcomponents.bukkit.BasePlugin;
import com.zachsthings.libcomponents.bukkit.DefaultsFileYAMLProcessor;
import com.zachsthings.libcomponents.bukkit.YAMLNodeConfigurationNode;
import com.zachsthings.libcomponents.loader.ConfigListedComponentLoader;

/**
 * @author andune
 *
 */
public class SwissArmyKnife extends BasePlugin {
    private static final Logger log = Logger.getLogger("SwissArmyKnife");
    private static final String logPrefix = "[SwissArmyKnife]";
    
    private static SwissArmyKnife instance;
    
	private Set<Class<Event>> eventClasses = new HashSet<Class<Event>>(30);
	private JarUtils jarUtils;
	private int buildNumber = -1;
	
	public SwissArmyKnife() {
		super();
		instance = this;
	}
	
	public static SwissArmyKnife instance() { return instance; }
	
	@Override
	public void onEnable() {
    	jarUtils = new JarUtils(this, getFile(), log, logPrefix);
		buildNumber = jarUtils.getBuildNumber();
		
		try {
			eventClasses = BukkitEventUtils.instance().populateEventClasses();
		}
		catch(Exception e) { e.printStackTrace(); }
//		dumpEventClasses();
		
        super.onEnable();
		log.info(logPrefix + " version "+getDescription().getVersion()+", build "+buildNumber+" is enabled");
	}
	
	@Override
	public void onDisable() {
		log.info(logPrefix + " version "+getDescription().getVersion()+", build "+buildNumber+" is disabled");
	}
	
	public Set<Class<Event>> getEventClasses() { return eventClasses; }
	
	@Override
	public YAMLProcessor populateConfiguration() {
        final File configFile = new File(getDataFolder(), "config.yml");
        YAMLProcessor config = new YAMLProcessor(configFile, true, YAMLFormat.EXTENDED);
        try {
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
            }
            config.load();
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Error loading configuration: ", e);
        }
        return config;
	}

	@Override
	public void registerComponentLoaders() {
        // -- Component loaders
        final File configDir = new File(getDataFolder(), "config/");
        final YAMLProcessor jarComponentAliases = new DefaultsFileYAMLProcessor("components.yml", false);
        try {
            jarComponentAliases.load();
        } catch (IOException e) {
            getLogger().severe("Error loading component aliases!");
            e.printStackTrace();
        } catch (YAMLException e) {
            getLogger().severe("Error loading component aliases!");
            e.printStackTrace();
        }
        componentManager.addComponentLoader(new ConfigListedComponentLoader(getLogger(),
                new YAMLNodeConfigurationNode(config),
                new YAMLNodeConfigurationNode(jarComponentAliases), configDir));

        // -- Annotation handlers
        componentManager.registerAnnotationHandler(InjectComponent.class, new InjectComponentAnnotationHandler(componentManager));
	}
	
    public void loadConfiguration() {
        config = populateConfiguration();
    }
}
