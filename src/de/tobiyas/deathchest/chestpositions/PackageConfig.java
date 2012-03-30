package de.tobiyas.deathchest.chestpositions;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import de.tobiyas.deathchest.DeathChest;

public class PackageConfig {
	
	private int maxTransferredItems;
	private DeathChest plugin;

	public PackageConfig(YamlConfiguration config, File savePath){
		plugin = DeathChest.getPlugin();
		int defaultMax = plugin.getConfigManager().getTransferLimit();
		
		maxTransferredItems = config.getInt("config.maxTransferredItems" ,defaultMax);
		if(maxTransferredItems == defaultMax) {
			config.set("config.maxTransferredItems", "");
			try {
				config.save(savePath);
			} catch (IOException e) {
				
			}
		}
	}
	
	public int getMaxTrandferredItems(){
		return maxTransferredItems;
	}
}
