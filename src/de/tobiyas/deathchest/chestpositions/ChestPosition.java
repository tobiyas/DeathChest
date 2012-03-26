package de.tobiyas.deathchest.chestpositions;

import java.io.File;
import java.io.IOException;

import de.tobiyas.deathchest.DeathChest;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;


public class ChestPosition implements ChestContainer{
	
	private DeathChest plugin;

	private YamlConfiguration config;
	
	private String packageName;
	private World world;
	private String player;
	private double posX;
	private double posY;
	private double posZ;
	private Location location;
	
	private String savePath;
	
	public ChestPosition(YamlConfiguration config, String packageName, String player){
		plugin = DeathChest.getPlugin();
		this.config = config;
		this.packageName = packageName;
		this.savePath = plugin.getDataFolder() + File.separator + "packages" + File.separator + packageName + ".yml";
		
		try{
			tryLoadChest(player);
		}catch(IOException e){
			plugin.log("Error reading " + player + " in package: " + packageName + ". Problem loading " +e.getMessage());
		}
	}
	
	private void tryLoadChest(String player) throws IOException{
		this.player = player;
		String playerPrefix = packageName + "." + player + ".";
		
		posX = config.getDouble(playerPrefix + "X", Double.MAX_VALUE);
		posY = config.getDouble(playerPrefix + "Y", Double.MAX_VALUE);
		posZ = config.getDouble(playerPrefix + "Z", Double.MAX_VALUE);
		
		String worldName = config.getString(playerPrefix + "World", "");
		
		if(posX == Double.MAX_VALUE || posY == Double.MAX_VALUE || posZ == Double.MAX_VALUE || worldName == "") throw new IOException("Position");
		
		world = Bukkit.getWorld(worldName);
		if(world == null) throw new IOException("world");
		
		location = new Location(world, posX, posY, posZ);
		//plugin.log("player: " + player + " world: " + world.getName() + " posX: " + posX + " posY: " + posY + " posZ: " + posZ);
	}
	
	public ChestPosition(YamlConfiguration config, String packageName, Player player, Location location){
		this.plugin = DeathChest.getPlugin();
		this.config = config;
		this.packageName = packageName;
		this.savePath = plugin.getDataFolder() + File.separator + "packages" + File.separator + packageName + ".yml";
		
		addChestToPlayer(location,player);
	}


	@Override
	public Location getChestOfPlayer(World world, Player player) {
		if(!checkPlayerHasChest(world, player)) return null;
		return location;
	}


	@Override
	public boolean checkPlayerHasChest(World world, Player player) {
		if(!player.getName().equals(this.player)) return false;
		return true;
	}

	@Override
	public boolean addChestToPlayer(Location location, Player player) {
		this.player = player.getName();
		this.world = location.getWorld();
		this.posX = location.getX();
		this.posY = location.getY();
		this.posZ = location.getZ();
		this.location = location;
		
		String playerPrefix = packageName + "." + player.getName();
		
		config.set(playerPrefix + ".World", world.getName());
		config.set(playerPrefix + ".X", posX);
		config.set(playerPrefix + ".Y", posY);
		config.set(playerPrefix + ".Z", posZ);
		
		try {
			config.save(savePath);
		} catch (IOException e) {
			plugin.log("Saving config failed");
		}
		
		return true;
	}

	@Override //not used in this struct
	public boolean hasWorld(World world) {
		return false;
	}

	@Override
	public ChestContainer removeFromPosition(Location location) {
		World world = location.getWorld();
		if(!world.equals(this.world)) return null;
		if(!location.equals(this.location)) return null;
		
		config.set(packageName + "." + player, null);
		try {
			config.save(savePath);
		} catch (IOException e) {
		}
		
		return this;
	}
	
	public String getPlayerName(){
		return player;
	}
}
