package de.tobiyas.deathchest.config;


import de.tobiyas.deathchest.DeathChest;

public class ConfigManager {

	private DeathChest plugin;
	
	private boolean worldGuardEnable;
	private boolean chestInInv;
	
	public ConfigManager(){
		plugin = DeathChest.getPlugin();
		setupConfiguration();
		reloadConfiguration();
	}
	
	private void setupConfiguration(){
		plugin.getConfig().addDefault("plugin.worldGuardEnable", true);
		plugin.getConfig().addDefault("plugin.checkChestInInventory", true);
		
		plugin.getConfig().options().copyDefaults(true);
		plugin.saveConfig();
	}
	
	private void reloadConfiguration(){
		plugin.reloadConfig();
		
		worldGuardEnable = plugin.getConfig().getBoolean("plugin.worldGuardEnable", true);
		chestInInv = plugin.getConfig().getBoolean("plugin.checkChestInInventory", true);
		
	}
	
	public void reloadConfig(){
		reloadConfiguration();
	}
	
	public boolean checkForWorldGuardCanBuild(){
		return worldGuardEnable;
	}
	
	public boolean checkIfChestInInv(){
		return chestInInv;
	}
}
