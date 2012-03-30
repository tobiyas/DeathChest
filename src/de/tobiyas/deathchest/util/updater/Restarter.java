package de.tobiyas.deathchest.util.updater;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import de.tobiyas.deathchest.DeathChest;

public class Restarter extends Thread {

	private DeathChest plugin;
	private String pluginName;
	private boolean ended = false;
	
	public Restarter(DeathChest plugin){
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		pluginName = plugin.getDescription().getName();
		if(ended) return;
		
		plugin.log("Reloading DeathChest...");
		Bukkit.getPluginManager().disablePlugin(plugin);
		try {
			sleep(10);
			Plugin plugin = Bukkit.getPluginManager().loadPlugin(new File("plugins" + File.separator + pluginName + ".jar"));
			
			plugin.onEnable();
			
		} catch (Exception e) {
			Bukkit.getLogger().log(Level.INFO, "[" + pluginName + "]" + " Reloading failed. ");
			e.printStackTrace();
		}
		ended = true;
		this.interrupt();
	}

}
