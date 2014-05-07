package de.tobiyas.deathchest.chestpositions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.tobiyas.deathchest.DeathChest;

public class PackageConfig {
	
	private int maxTransferredItemsDefault;
	private DeathChest plugin;
	
	private HashMap<String, Integer> groupMap;

	/**
	 * Constructor for a package Config.
	 * package Config overrides the normal Config for the package.
	 * 
	 * @param config the YAMLConfiguration
	 * @param savePath the path to save the YAMLConfiguration to
	 */
	public PackageConfig(YamlConfiguration config, File savePath){
		plugin = DeathChest.getPlugin();
		int defaultMax = plugin.getConfigManager().getTransferLimit();
		groupMap = new HashMap<String, Integer>();
		
		maxTransferredItemsDefault = config.getInt("config.maxTransferredItems.default" ,defaultMax);
		if(maxTransferredItemsDefault == defaultMax) 
			config.set("config.maxTransferredItems.default", "");
		
		for(String group : plugin.getPermissionManager().getAllGroups()){
			int groupMax = config.getInt("config.maxTransferredItems." + group ,maxTransferredItemsDefault);
			if(groupMax == maxTransferredItemsDefault)
				config.set("config.maxTransferredItems." + group, "");
			groupMap.put(group, groupMax);
		}
			
		try {
			config.save(savePath);
		} catch (IOException e) {
		}
	}
	
	/**
	 * Returns the maximum transferlimit for a Player in this package
	 * 
	 * @param player
	 * @return the maximum transferlimit
	 */
	public int getMaxTrandferredItems(Player player){
		int playerTransferLimit;
		
		try{
			String playerGroup = plugin.getPermissionManager().getGroupOfPlayer(player);
			
			playerTransferLimit = groupMap.get(playerGroup);
		}catch(Exception e){
			playerTransferLimit = maxTransferredItemsDefault;
		}
		
		return playerTransferLimit;
	}
}
