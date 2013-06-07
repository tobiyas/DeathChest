package de.tobiyas.deathchest.config;


import java.util.LinkedList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.spawncontainer.SpawnChest;
import de.tobiyas.deathchest.spawncontainer.SpawnSign;

public class ConfigManager {

	private DeathChest plugin;
	
	private boolean worldGuardEnable;
	private boolean chestInInv;
	private boolean createSpawnChestWithLWC;
	private boolean checkDeathChestWithLWC;
	private boolean useLightningForDeathChestSign;
	private boolean autoTurnOffDepends;
	private boolean remindUpdate;
	private boolean checkForBattleNight;
	private int spawnSignDespawnTime;
	private boolean useOrbs;
	
	private int transferLimit;
	private boolean randomPick;
	private boolean transferEquip;
	
	private boolean checkUpdater;
	
	private double expMulti;

	private boolean lwcSignProtect;

	private String useSpawnContainer;
	
	private boolean useSecureChestInsteadOfLWC;
	private boolean useLocketteInsteadOfLWC;
	
	private List<String> disableSpawnContainerInWorlds;
	
	
	/**
	 * Constructor creates a new Config and reloads it from the config file 
	 */
	public ConfigManager(){
		plugin = DeathChest.getPlugin();
		setupConfiguration();
		reloadConfiguration();
	}
	
	
	/**
	 * adds default values to config and saves them if set yet
	 */
	private void setupConfiguration(){
		FileConfiguration config = plugin.getConfig();
		config.addDefault("plugin.SpawnChest.worldGuardEnable", true);
		config.addDefault("plugin.SpawnChest.checkChestInInventory", true);
		config.addDefault("plugin.SpawnChest.protectWithLWC", true);
		
		config.addDefault("plugin.DeathChest.checkCreationWithLWC", false);
		config.addDefault("plugin.DeathChest.useLightningBreakDCSign", false);
		
		config.addDefault("plugin.Transfer.limitItems", 100);
		config.addDefault("plugin.Transfer.randomPick", true);
		config.addDefault("plugin.Transfer.transferEquipped", true);
		
		config.addDefault("plugin.General.autoTurnOffSoftDependsOptionsOnError", true);
		config.addDefault("plugin.General.autoUpdate", false);
		config.addDefault("plugin.General.remindUpdate", true);
		config.addDefault("plugin.General.useSecureChestInsteadOfLWC", false);
		config.addDefault("plugin.General.useLocketteInsteadOfLWC", false);
		config.addDefault("plugin.General.checkForBattleNight", false);
		
		config.addDefault("plugin.General.useDeathSpawnType", "sign");
		config.addDefault("plugin.General.useEXPOrbs", true);

		List<String> tempList = new LinkedList<String>();
		tempList.add("demoWorldToReplace");
		config.addDefault("plugin.SpawnContainer.disableSpawnContainerInWorlds", tempList);
		
		config.addDefault("plugin.SpawnSign.EXPValue", 0.75D);
		config.addDefault("plugin.SpawnSign.LWCSignProtect", true);
		config.addDefault("plugin.SpawnSign.despawnTimeInMin", 4320);
		
		config.options().copyDefaults(true);
		plugin.saveConfig();
	}
	
	
	/**
	 * reloads all variables from the Config file 
	 */
	private void reloadConfiguration(){
		plugin.reloadConfig();
		FileConfiguration config = plugin.getConfig();
		
		worldGuardEnable = config.getBoolean("plugin.SpawnChest.worldGuardEnable", true);
		chestInInv = config.getBoolean("plugin.SpawnChest.checkChestInInventory", true);
		createSpawnChestWithLWC = config.getBoolean("plugin.SpawnChest.protectWithLWC", true);
		
		checkDeathChestWithLWC = config.getBoolean("plugin.DeathChest.checkCreationWithLWC", false);
		useLightningForDeathChestSign = config.getBoolean("plugin.DeathChest.useLightningBreakDCSign", false);
		
		transferLimit = config.getInt("plugin.Transfer.limitItems", 100);
		randomPick = config.getBoolean("plugin.Transfer.randomPick", true);
		transferEquip = config.getBoolean("plugin.Transfer.transferEquipped", true);
		
		autoTurnOffDepends = config.getBoolean("plugin.General.autoTurnOffSoftDependsOptionsOnError", false);
		checkUpdater = config.getBoolean("plugin.General.autoUpdate", false);
		remindUpdate = config.getBoolean("plugin.General.remindUpdate", true);
		useSpawnContainer = config.getString("plugin.General.useDeathSpawnType", "sign");
		checkForBattleNight = config.getBoolean("plugin.General.checkForBattleNight", false);
		useOrbs = config.getBoolean("plugin.General.useEXPOrbs", true);
		
		expMulti = config.getDouble("plugin.SpawnSign.EXPValue", 0.75);
		lwcSignProtect = config.getBoolean("plugin.SpawnSign.LWCSignProtect", true);
		spawnSignDespawnTime = config.getInt("plugin.SpawnSign.despawnTimeInMin", 4320);
		
		useSecureChestInsteadOfLWC = config.getBoolean("plugin.General.useSecureChestInsteadOfLWC", false);
		useLocketteInsteadOfLWC = config.getBoolean("plugin.General.useLocketteInsteadOfLWC", false);
		
		disableSpawnContainerInWorlds = config.getStringList("plugin.SpawnContainer.disableSpawnContainerInWorlds");
	}
	
	
	/**
	 * reloads the Configuration
	 */
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

	public boolean useLightningForDeathChest() {
		return useLightningForDeathChestSign;
	}
	
	/**
	 * Turns off temporary all LWC options
	 */
	public void tempTurnOffLWC(){
		if(!autoTurnOffDepends) return;
		
		this.lwcSignProtect = false;
		this.createSpawnChestWithLWC = false;
		this.checkDeathChestWithLWC = false;
		
		plugin.getConfig().set("plugin.SpawnChest.protectWithLWC", false);
		plugin.getConfig().set("plugin.DeathChest.checkCreationWithLWC", false);
		plugin.getConfig().set("plugin.SpawnSign.LWCSignProtect", false);
	}
	
	/**
	 * Turns off temporary all WorldGuard options
	 */
	public void tempTurnOffWG(){
		if(!autoTurnOffDepends) return;
		
		plugin.getConfig().getBoolean("plugin.SpawnChest.worldGuardEnable", false);
	}
	
	public boolean checkUpdater(){
		return checkUpdater;
	}
	
	public int getTransferLimit(){
		return transferLimit;
	}
	
	public boolean checkRandomPick(){
		return randomPick;
	}
	
	public boolean checkTransferEquip(){
		return transferEquip;
	}
	
	public boolean checkRemindUpdate(){
		return remindUpdate;
	}
	
	public double getEXPMulti(){
		return expMulti;
	}
	
	public boolean getLWCSignProtect(){
		return lwcSignProtect;
	}
	
	public Class<?> getSpawnContainerUsage(){
		if(useSpawnContainer.equalsIgnoreCase("sign"))
			return SpawnSign.class;
		
		if(useSpawnContainer.equalsIgnoreCase("chest"))
			return SpawnChest.class;
		
		if(useSpawnContainer.equalsIgnoreCase("none"))
			return null;
		
		return SpawnSign.class;
	}
	
	public boolean getUseSecureChestInsteadOfLWC(){
		return useSecureChestInsteadOfLWC;
	}
	
	public boolean getUseLocketteInsteadOfLWC(){
		return useLocketteInsteadOfLWC;
	}
	
	public boolean getCheckForBattleNight(){
		return checkForBattleNight;
	}
	
	public int getspawnSignDespawnTime(){
		return spawnSignDespawnTime;
	}
	
	public boolean getUseOrbs(){
		return useOrbs;
	}


	public List<String> getDisableSpawnContainerInWorlds() {
		return disableSpawnContainerInWorlds;
	}
}
