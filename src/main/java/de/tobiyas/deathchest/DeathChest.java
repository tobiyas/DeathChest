/*
 * DeathChest - by Tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.deathchest;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import de.tobiyas.deathchest.chestpositions.ChestContainer;
import de.tobiyas.deathchest.chestpositions.ChestPackage;
import de.tobiyas.deathchest.commands.CommandExecutor_DCHelp;
import de.tobiyas.deathchest.commands.CommandExecutor_DCPermCheck;
import de.tobiyas.deathchest.commands.CommandExecutor_DCPort;
import de.tobiyas.deathchest.commands.CommandExecutor_DCReload;
import de.tobiyas.deathchest.commands.CommandExecutor_DCRemove;
import de.tobiyas.deathchest.commands.CommandExecutor_DCVersion;
import de.tobiyas.deathchest.commands.CommandExecutor_GYPort;
import de.tobiyas.deathchest.commands.CommandExecutor_GYPos;
import de.tobiyas.deathchest.config.ConfigManager;
import de.tobiyas.deathchest.listeners.Listener_AfterPlayerDeath;
import de.tobiyas.deathchest.listeners.Listener_Entity;
import de.tobiyas.deathchest.listeners.Listener_Explosion;
import de.tobiyas.deathchest.listeners.Listener_Sign;
import de.tobiyas.deathchest.spawncontainer.SpawnContainerController;
import de.tobiyas.deathchest.util.BattleNightChecker;
import de.tobiyas.deathchest.util.Const;
import de.tobiyas.deathchest.util.PackageReloader;
import de.tobiyas.deathchest.util.protection.ProtectionManager;
import de.tobiyas.util.UtilsUsingPlugin;
import de.tobiyas.util.metrics.SendMetrics;


/**
 * @author tobiyas
 *
 */
public class DeathChest extends UtilsUsingPlugin{
	private PluginDescriptionFile description;
	
	private ConfigManager cManager;
	
	private ChestContainer cContainer;
	private static DeathChest plugin;
	private SpawnContainerController spawnSignController;
	
	private ProtectionManager protectionManager;
	private BattleNightChecker bchecker;
	
	@Override
	public void onEnable(){
		plugin = this;
		
		description = getDescription();
		
		writeVersion();
		
		if(!checkBukkitVersion()) Const.oldBukkitVersion = true;
		
		cManager = new ConfigManager();
		
		cContainer = ChestPackage.createALLPackages();
		spawnSignController = new SpawnContainerController();

		addEvents();
		addCommands();
		
		initBattleNight();
		protectionManager = new ProtectionManager();
		SendMetrics.sendMetrics(this);

		log(description.getFullName() + " fully loaded with " + getPermissionManager().getPermissionsName() + " hooked.");
	}
	
	
	private void writeVersion(){
		String versionInfo = description.getVersion();
		String split[] = versionInfo.split("\\.");
		
		Const.currentVersion = Double.parseDouble(split[0]);
		Const.currentBuildVersion = Integer.parseInt(split[1]);
		if(split.length > 2){
			Const.currentRevOfBuild = Integer.parseInt(split[2]);
		}else{
			Const.currentRevOfBuild = 1;
		}
	}
	
	/**
	 * checks if the Bukkit build-Version is higher than the one given in Const.
	 * @return version > const.oldBukkitVersion
	 */
	private boolean checkBukkitVersion(){
		String version = Bukkit.getVersion();
		int firstPos = version.indexOf("-b") + 2;
		int endPos = version.indexOf("jnks");

		try{
			String buildString = version.substring(firstPos, endPos);
			int buildVersion = Integer.parseInt(buildString);
			
			if(buildVersion < Const.leastBuild){
				log("Bukkit version is too low. Plugin will work in low version Mode.");
				return false;
			}
		}catch(Exception e){
			log("Could not recognize Bukkit Build-version. Version might be outdated.");
		}
		
		return true;
	}
	
	/**
	 * Registers Bukkit-Events
	 */
	private void addEvents(){
		Listener_Entity listenerEntity = new Listener_Entity(this);
		getServer().getPluginManager().registerEvents(listenerEntity, this);
		
		Listener_Sign listenerSign = new Listener_Sign(this);
		getServer().getPluginManager().registerEvents(listenerSign, this);
		
		Listener_Explosion listnerExplosion = new Listener_Explosion(this);
		getServer().getPluginManager().registerEvents(listnerExplosion, this);

		Listener_AfterPlayerDeath listnerAfterDeath = new Listener_AfterPlayerDeath(this);
		getServer().getPluginManager().registerEvents(listnerAfterDeath, this);
		
		
	}
	
	/**
	 * Registers Bukkit-Commands
	 */
	private void addCommands(){
		new CommandExecutor_DCReload();
		new CommandExecutor_DCVersion();
		new CommandExecutor_DCHelp();
		new CommandExecutor_DCPermCheck();
		new CommandExecutor_GYPos();
		new CommandExecutor_DCPort();
		new CommandExecutor_DCRemove();
		new CommandExecutor_GYPort();
	}
	
	private void initBattleNight(){
		bchecker = new BattleNightChecker();
		try{
			bchecker.isActive();
		}catch(NoClassDefFoundError e){
			bchecker = null;
		}
	}
	
	@Override
	public void onDisable(){
		interactSpawnContainerController().saveAllSigns();		
		log("disabled "+description.getFullName());
	}
	

	/**
	 * @return the Plugin construct
	 */
	public static DeathChest getPlugin(){
		return plugin;
	}
	
	/**
	 * @return the ChatManager
	 */
	public ConfigManager getConfigManager(){
		return cManager;
	}
	
	/**
	 * @return the ChestPackageConstruct
	 */
	public ChestContainer getChestContainer(){
		return cContainer;
	}

	/**
	 * reloads the ChestContainer from files
	 */
	public void reloadChestContainer(ChestContainer container) {
		cContainer = container;
	}
	
	/**
	 * reloads the ChestContainer from files
	 */
	public void reloadChestContainer() {
		PackageReloader.reload();
	}
	
	public SpawnContainerController interactSpawnContainerController(){
		return spawnSignController;
	}
	
	public ProtectionManager getProtectionManager(){
		return protectionManager;
	}
	
	public boolean isBattleNight(Player player){
		if(bchecker == null) return false;
		if(!bchecker.isActive()) return false;
		return bchecker.checkForBattleNight(player);
	}

}
