package de.tobiyas.deathchest.config;


import org.bukkit.configuration.file.FileConfiguration;

import de.tobiyas.deathchest.DeathChest;

public class ConfigManager {

	private DeathChest plugin;
	private String config_testnode;
	
	public ConfigManager(){
		plugin = DeathChest.getPlugin();
		setupConfiguration();
		reloadConfiguration();
	}
	
	private void reloadConfiguration(){
		FileConfiguration cfg = plugin.getConfig();
		
		config_testnode = cfg.getString("testnode", "test");
	}
	
	private void setupConfiguration(){
		FileConfiguration cfg = plugin.getConfig();
		
		cfg.addDefault("testnode", "test");
		
		plugin.saveConfig();
	}
	
	public String getConfig_testnode(){
		return config_testnode;
	}
}
