package de.tobiyas.deathchest.config;


import de.tobiyas.deathchest.DeathChest;

public class ConfigManager {

	private DeathChest plugin;
	
	private boolean worldGuardEnable;
	private boolean chestInInv;
	private boolean createSpawnChestWithLWC;
	private boolean checkDeathChestWithLWC;
	
	public ConfigManager(){
		plugin = DeathChest.getPlugin();
		setupConfiguration();
		reloadConfiguration();
	}
	
	private void setupConfiguration(){
		plugin.getConfig().addDefault("plugin.SpawnChest.worldGuardEnable", true);
		plugin.getConfig().addDefault("plugin.SpawnChest.checkChestInInventory", true);
		plugin.getConfig().addDefault("plugin.SpawnChest.protectWithLWC", true);
		plugin.getConfig().addDefault("plugin.DeathChest.checkCreationWithLWC", false);
		
		plugin.getConfig().options().copyDefaults(true);
		plugin.saveConfig();
	}
	
	private void reloadConfiguration(){
		plugin.reloadConfig();
		
		worldGuardEnable = plugin.getConfig().getBoolean("plugin.SpawnChest.worldGuardEnable", true);
		chestInInv = plugin.getConfig().getBoolean("plugin.SpawnChest.checkChestInInventory", true);
		createSpawnChestWithLWC = plugin.getConfig().getBoolean("plugin.SpawnChest.protectWithLWC", true);
		checkDeathChestWithLWC = plugin.getConfig().getBoolean("plugin.DeathChest.checkCreationWithLWC", false);
		
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
	
	public boolean checkSpawnChestLWC(){
		return createSpawnChestWithLWC;
	}
	
	public boolean checkDeathChestWithLWC(){
		return checkDeathChestWithLWC;
	}
}
