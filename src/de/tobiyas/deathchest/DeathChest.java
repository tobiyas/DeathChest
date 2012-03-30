/*
 * DeathChest - by Tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.deathchest;


import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;

import de.tobiyas.deathchest.chestpositions.ChestContainer;
import de.tobiyas.deathchest.chestpositions.ChestPackage;
import de.tobiyas.deathchest.commands.CommandExecuter_DCReload;
import de.tobiyas.deathchest.commands.CommandExecutor_DCVersion;
import de.tobiyas.deathchest.config.ConfigManager;

import de.tobiyas.deathchest.listeners.Listener_Entity;
import de.tobiyas.deathchest.listeners.Listener_Sign;
import de.tobiyas.deathchest.permissions.PermissionsManager;
import de.tobiyas.deathchest.util.Const;
import de.tobiyas.deathchest.util.updater.Restarter;
import de.tobiyas.deathchest.util.updater.Updater;


/**
 * @author tobiyas
 *
 */
public class DeathChest extends JavaPlugin{
	private Logger log;
	private PluginDescriptionFile description;
	
	private ConfigManager cManager;
	private PermissionsManager pManager;
	
	private ChestContainer cContainer;
	
	private static DeathChest plugin;

	private String prefix;
	
	private Updater updater;

	
	@Override
	public void onEnable(){
		plugin = this;
		
		log = Logger.getLogger("Minecraft");
		description = getDescription();
		prefix = "["+description.getName()+"] ";
		
		String versionInfo = description.getVersion();
		String split[] = versionInfo.split("\\.");
		
		Const.currentVersion = Double.parseDouble(split[0]);
		Const.currentBuildVersion = Integer.parseInt(split[1]);

		//log("loading "+description.getFullName());
		
		Const.oldBukkitVersion = false;
		if(!checkBukkitVersion()) Const.oldBukkitVersion = true;
		
		updater = new Updater(Const.updaterURL + "versions.html");
		
		cManager = new ConfigManager();
		pManager = new PermissionsManager();
		
		cContainer = ChestPackage.createALLPackages();

		addEvents();
		addCommands();

		log(description.getFullName() + " fully loaded.");
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
				log("Bukkit version is to low. Plugin will work in low version Mode.");
				return false;
			}
		}catch(Exception e){
			log("Could not recognize Bukkit Build-version. Version might be outdated.");
		}
		
		return true;
	}
	
	/**
	 * chesks if a new Version is available and downloads it
	 */
	private void checkPluginUpdates(){
		if(!plugin.getConfigManager().checkUpdater()) return;
		
		//Restarter restarter = new Restarter(this);
		if(!updater.checkVersion(Const.currentVersion, Const.currentBuildVersion))
			if(updater.forceDownload(Const.updaterURL + "DeathChest.jar")){
				//reloadPlugin(restarter);   //not working
			}
		
	}
	
	/**
	 * Registers Bukkit-Events
	 */
	private void addEvents(){
		Listener_Entity listenerEntity = new Listener_Entity(this);
		getServer().getPluginManager().registerEvents(listenerEntity, this);
		
		Listener_Sign listenerSign = new Listener_Sign(this);
		getServer().getPluginManager().registerEvents(listenerSign, this);
	}
	
	/**
	 * Registers Bukkit-Commands
	 */
	private void addCommands(){
		//getCommand("killself").setExecutor(new CommandExecutor_Testcommand(this));
		getCommand("dcversion").setExecutor(new CommandExecutor_DCVersion());
		getCommand("dcreload").setExecutor(new CommandExecuter_DCReload(this));
	}
	
	@Override
	public void onDisable(){
		checkPluginUpdates();
		log("disabled "+description.getFullName());
	}
	
	
	public void log(String message){
		log.info(prefix+message);
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
	 * @return the PermissionsManager
	 */
	public PermissionsManager getPermissionsManager(){
		return pManager;
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
	public void reloadChestContainer() {
		cContainer = ChestPackage.createALLPackages();
	}
	
	/**
	 * lets the Plugin restart completly //not yet implemeted
	 * @param restarter the Restarter Thread
	 * 
	 */
	@SuppressWarnings("unused") //unused till bukkit allows plugin reload in plugins
	private void reloadPlugin(Restarter restarter){
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, restarter, 1);
	}

}
