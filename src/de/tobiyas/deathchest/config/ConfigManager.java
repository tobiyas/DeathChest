package de.tobiyas.deathchest.config;


import de.tobiyas.deathchest.DeathChest;

public class ConfigManager {

	private DeathChest plugin;
	
	private boolean worldGuardEnable;
	private boolean chestInInv;
	private boolean createSpawnChestWithLWC;
	private boolean checkDeathChestWithLWC;
	private boolean useLightningForDeathChestSign;
	private boolean autoTurnOffDepends;
	
	private int transferLimit;
	private boolean randomPick;
	private boolean transferEquip;
	
	private boolean checkUpdater;
	
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
		plugin.getConfig().addDefault("plugin.DeathChest.useLightningBreakDCSign", false);
		
		plugin.getConfig().addDefault("plugin.Transfer.limitItems", 100);
		plugin.getConfig().addDefault("plugin.Transfer.randomPick", true);
		plugin.getConfig().addDefault("plugin.Transfer.transferEquipped", true);
		
		plugin.getConfig().addDefault("plugin.General.autoTurnOffSoftDependsOptionsOnError", true);
		plugin.getConfig().addDefault("plugin.General.autoUpdate", false);
		
		plugin.getConfig().options().copyDefaults(true);
		plugin.saveConfig();
	}
	
	private void reloadConfiguration(){
		plugin.reloadConfig();
		
		worldGuardEnable = plugin.getConfig().getBoolean("plugin.SpawnChest.worldGuardEnable", true);
		chestInInv = plugin.getConfig().getBoolean("plugin.SpawnChest.checkChestInInventory", true);
		createSpawnChestWithLWC = plugin.getConfig().getBoolean("plugin.SpawnChest.protectWithLWC", true);
		
		checkDeathChestWithLWC = plugin.getConfig().getBoolean("plugin.DeathChest.checkCreationWithLWC", false);
		useLightningForDeathChestSign = plugin.getConfig().getBoolean("plugin.DeathChest.useLightningBreakDCSign", false);
		
		transferLimit = plugin.getConfig().getInt("plugin.Transfer.limitItems", 100);
		randomPick = plugin.getConfig().getBoolean("plugin.Transfer.randomPick", true);
		transferEquip = plugin.getConfig().getBoolean("plugin.Transfer.transferEquipped", true);
		
		autoTurnOffDepends = plugin.getConfig().getBoolean("plugin.General.autoTurnOffSoftDependsOptionsOnError", false);
		checkUpdater = plugin.getConfig().getBoolean("plugin.General.autoUpdate", false);
		
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

	public boolean useLightningForDeathChest() {
		return useLightningForDeathChestSign;
	}
	
	public void tempTurnOffLWC(){
		if(!autoTurnOffDepends) return;
		
		plugin.getConfig().set("plugin.SpawnChest.protectWithLWC", false);
		plugin.getConfig().set("plugin.DeathChest.checkCreationWithLWC", false);
	}
	
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
}
