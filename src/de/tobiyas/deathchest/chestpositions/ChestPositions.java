package de.tobiyas.deathchest.chestpositions;

import java.io.File;
import java.io.IOException;
import com.sk89q.util.yaml.YAMLNode;
import com.sk89q.util.yaml.YAMLProcessor;

import de.tobiyas.deathchest.DeathChest;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;


public class ChestPositions implements ChestContainer{

	private World world;
	
	private DeathChest plugin;

	private YAMLProcessor config;
	
	public ChestPositions(World world){
		plugin = DeathChest.getPlugin();
		this.world = world;
		String worldFolderPath = plugin.getDataFolder() + File.separator + "worlds" + File.separator;
		String path = worldFolderPath + world.getName() + "_Chests.yml";
		
		checkPath(worldFolderPath);
		File file = CheckFile(path);
		
		config = new YAMLProcessor(file, true);
		try {
			config.load();
		} catch (IOException e) {
			plugin.log("Critical Error on Chest loading of World: " + world.getName());
		}
	}
	
	private File CheckFile(String path){
		File file = new File(path);
		
		if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				plugin.log("Critical Error on File Creation: " + path);
			}
		
		return file;
	}
	
	private void checkPath(String path){
		File file = new File(path);
		
		if(!file.exists())
			file.mkdir();
	}

	@Override
	public Location getChestOfPlayer(World world, Player player) {
		if(!checkPlayerHasChest(player, world)) return null;
		
		String playerName = player.getName();
		
		YAMLNode playerConfig = config.getNode("ChestPosition." + playerName);
		if(playerConfig == null) return null;
		
		int posX = playerConfig.getInt("X", Integer.MAX_VALUE);
		int posY = playerConfig.getInt("Y", Integer.MAX_VALUE);
		int posZ = playerConfig.getInt("Z", Integer.MAX_VALUE);
		
		if(posX == Integer.MAX_VALUE || posY == Integer.MAX_VALUE || posZ == Integer.MAX_VALUE) return null;
		
		Location location = new Location(world, posX, posY, posZ);
		
		return location;
	}

	@Override
	public void createChestConfig() {
		return;
	}

	@Override
	public boolean checkPlayerHasChest(Player player, World world) {
		String playerName = player.getName();
		
		YAMLNode playerConfig = config.getNode("ChestPosition." + playerName);
		if(playerConfig == null || playerConfig.getBoolean("isActive") == false) return false;
		return true;
	}

	@Override
	public void addChestToPlayer(World world, Player player, Location location) {
		if(!this.world.getName().equals(world.getName())) return;
		
		String playerName = player.getName();
		String pathPrefix = "ChestPosition." + playerName;
		
		YAMLNode playerConfig = config.addNode(pathPrefix);
		
		playerConfig.setProperty("X", location.getBlockX());
		playerConfig.setProperty("Y", location.getBlockY());
		playerConfig.setProperty("Z", location.getBlockZ());
		playerConfig.setProperty("isActive", true);
		
		/* Not sure if above works
		config.setProperty(pathPrefix + ".X", location.getBlockX());
		config.setProperty(pathPrefix + ".Y", location.getBlockY());
		config.setProperty(pathPrefix + ".Z", location.getBlockZ());
		config.setProperty(pathPrefix + ".isActive", true);
		*/
		config.save();
	}
}
