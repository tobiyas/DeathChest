/*
 * DeathChest - by Tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.deathchest;


import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;
import org.bukkit.plugin.PluginDescriptionFile;

import de.tobiyas.deathchest.chestpositions.ChestContainer;
import de.tobiyas.deathchest.chestpositions.ChestWorlds;
import de.tobiyas.deathchest.commands.CommandExecutor_Testcommand;
import de.tobiyas.deathchest.config.ConfigManager;

import de.tobiyas.deathchest.listeners.Listener_Entity;
import de.tobiyas.deathchest.listeners.Listener_Sign;
import de.tobiyas.deathchest.permissions.PermissionsManager;


public class DeathChest extends JavaPlugin{
	private Logger log;
	private PluginDescriptionFile description;
	
	private ConfigManager cManager;
	private PermissionsManager pManager;
	
	private ChestContainer cContainer;
	
	private static DeathChest plugin;

	private String prefix;

	
	@Override
	public void onEnable(){
		plugin = this;
		
		log = Logger.getLogger("Minecraft");
		description = getDescription();
		prefix = "["+description.getName()+"] ";

		log("loading "+description.getFullName());
		
		cManager = new ConfigManager();
		pManager = new PermissionsManager();
		
		cContainer = new ChestWorlds();
		cContainer.createChestConfig();

		addEvents();
		addCommands();

		log(description.getFullName() + " fully loaded.");
	}
	
	private void addEvents(){
		Listener_Entity listenerEntity = new Listener_Entity(this);
		getServer().getPluginManager().registerEvents(listenerEntity, this);
		
		Listener_Sign listenerSign = new Listener_Sign(this);
		getServer().getPluginManager().registerEvents(listenerSign, this);
	}
	
	private void addCommands(){
		getCommand("killself").setExecutor(new CommandExecutor_Testcommand(this));
	}
	
	@Override
	public void onDisable(){
		log("disabled "+description.getFullName());

	}
	public void log(String message){
		log.info(prefix+message);
	}

	public static DeathChest getPlugin(){
		return plugin;
	}
	
	public ConfigManager getConfigManager(){
		return cManager;
	}
	
	public PermissionsManager getPermissionsManager(){
		return pManager;
	}
	
	public ChestContainer getChestContainer(){
		return cContainer;
	}

}
