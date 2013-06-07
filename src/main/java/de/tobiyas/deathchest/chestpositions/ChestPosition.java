package de.tobiyas.deathchest.chestpositions;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.tobiyas.deathchest.DeathChest;


/**
 * @author Toby
 *
 */
public class ChestPosition implements ChestContainer{
	
	private DeathChest plugin;

	private YamlConfiguration config;
	
	private String packageName;
	private World world;
	private String player;
	private double posX;
	private double posY;
	private double posZ;
	private Location chestLocation;
	private Location signLocation;
	
	private int exp;
	
	private String savePath;
	
	/**
	 * Constructor for loading from file
	 * 
	 * @param config the YAMLConfiguration to load from
	 * @param packageName to load from
	 * @param player to load from
	 */
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
	
	/**
	 * tries to load a Player from the Config
	 * 
	 * @param player
	 * @throws IOException accures if an error accures while loading
	 */
	private void tryLoadChest(String player) throws IOException{
		this.player = player;
		String playerPrefix = packageName + "." + player + ".";
		
		posX = config.getDouble(playerPrefix + "X", Double.MAX_VALUE);
		posY = config.getDouble(playerPrefix + "Y", Double.MAX_VALUE);
		posZ = config.getDouble(playerPrefix + "Z", Double.MAX_VALUE);
		exp = config.getInt(playerPrefix + "exp", 0);
		
		String worldName = config.getString(playerPrefix + "World", "");
		
		if(posX == Double.MAX_VALUE || posY == Double.MAX_VALUE || posZ == Double.MAX_VALUE || worldName == "") throw new IOException("Position");
		
		world = Bukkit.getWorld(worldName);
		if(world == null) throw new IOException("world");
		
		chestLocation = new Location(world, posX, posY, posZ);
		signLocation = new Location(world, posX, posY + 1, posZ);
	}
	
	/**
	 * Constructor for creating a DeathChest in Game
	 * 
	 * @param config the YAMLConfiguration of the package
	 * @param packageName
	 * @param player
	 * @param location
	 */
	public ChestPosition(YamlConfiguration config, String packageName, Player player, Location location){
		this.plugin = DeathChest.getPlugin();
		this.config = config;
		this.packageName = packageName;
		this.savePath = plugin.getDataFolder() + File.separator + "packages" + File.separator + packageName + ".yml";
		
		addChestToPlayer(location,player);
	}
	
	/**
	 * Constructor for creating a DeathChest in Game
	 * 
	 * @param config the YAMLConfiguration of the package
	 * @param packageName
	 * @param player
	 * @param location
	 */
	public ChestPosition(YamlConfiguration config, String packageName, String player, Location location){
		this.plugin = DeathChest.getPlugin();
		this.config = config;
		this.packageName = packageName;
		this.savePath = plugin.getDataFolder() + File.separator + "packages" + File.separator + packageName + ".yml";
		
		addChestToPlayer(location,player);
	}
	
	
	/**
	 * Tells a DeathChest to destroy self (destroys the sign above and removes out of list)
	 * 
	 * @param lightning if it should have a lightning effect
	 * @param breakNaturaly if it should drop the sign
	 */
	public void destroySelf(boolean lightning, boolean breakNaturaly){
		if(!signLocation.getBlock().getType().equals(Material.WALL_SIGN)) return;
		if(lightning) world.strikeLightningEffect(signLocation);
		if(breakNaturaly) world.getBlockAt(signLocation).breakNaturally();
		plugin.getProtectionManager().unprotectSign(signLocation);
		
		removeFromPosition(getLocation());
	}
	
	public String getPlayerName(){
		return player;
	}
	
	public Location getLocation(){
		return chestLocation;
	}
	
	public Location getSignLocation(){
		return signLocation;
	}
	
	public int getStoredEXP(){
		int exp = this.exp;
		this.exp = 0;
		
		String playerPrefix = packageName + "." + player;
		config.set(playerPrefix + ".exp", 0);
		try {
			config.save(savePath);
		} catch (IOException e) {
			plugin.log("Saving config failed");
		}
		
		return exp;
	}
	
	public void addEXP(int amount){
		this.exp += amount;
		
		String playerPrefix = packageName + "." + player;
		config.set(playerPrefix + ".exp", exp);
		
		try {
			config.save(savePath);
		} catch (IOException e) {
			plugin.log("Saving config failed");
		}
	}


	@Override
	public ChestContainer getChestOfPlayer(World world, Player player) {
		if(!checkPlayerHasChest(world, player)) return null;
		return this;
	}


	@Override
	public boolean checkPlayerHasChest(World world, Player player) {
		if(!player.getName().equals(this.player)) return false;
		return true;
	}

	@Override
	public boolean addChestToPlayer(Location location, Player player) {
		return addChestToPlayer(location, player.getName());
	}

	@Override //not used in this struct
	public boolean hasWorld(World world) {
		return false;
	}

	@Override
	public ChestContainer removeFromPosition(Location location) {
		World world = location.getWorld();
		if(!world.equals(this.world)) return null;
		if(!location.equals(this.chestLocation)) return null;
		
		config.set(packageName + "." + player, null);
		try {
			config.save(savePath);
		} catch (IOException e) {
		}
		
		return this;
	}

	@Override //not used in this struct
	public boolean worldSupported(World world) {
		return true;
	}

	@Override //not used in this struct
	public int getMaxTransferLimit(Player player) {
		return -1;
	}

	@Override
	public ChestContainer getChestOfPlayer(World world, String player) {
		if(!checkPlayerHasChest(world, player)) return null;
		return this;
	}
	
	@Override
	public boolean checkPlayerHasChest(World world, String player) {
		if(!player.equals(this.player)) return false;
		return true;
	}

	@Override
	public boolean addChestToPlayer(Location location, String player) {
		this.player = player;
		this.world = location.getWorld();
		this.posX = location.getX();
		this.posY = location.getY();
		this.posZ = location.getZ();
		this.chestLocation = location;
		this.signLocation = new Location(world, posX, posY + 1, posZ);
		this.exp = 0;
		
		String playerPrefix = packageName + "." + player;
		
		config.set(playerPrefix + ".World", world.getName());
		config.set(playerPrefix + ".X", posX);
		config.set(playerPrefix + ".Y", posY);
		config.set(playerPrefix + ".Z", posZ);
		
		config.set(playerPrefix + ".exp", 0);
		
		try {
			config.save(savePath);
		} catch (IOException e) {
			plugin.log("Saving config failed");
		}
		
		return true;
	}

	@Override
	public boolean isChestOrSignOfDC(Block block) {
		if(block.equals(this.chestLocation.getBlock())){
			return true;
		}
		
		if(block.equals(this.signLocation.getBlock())){
			return true;
		}
		
		Block attachedSignBlock = getBlockAttachedToSign();
		if(attachedSignBlock != null && block.equals(attachedSignBlock)){
			return true;
		}
		
		Block doubleChestBlock = getDoubleChest();
		if(doubleChestBlock != null && block.equals(doubleChestBlock)){
			return true;
		}
			
		return false;
	}


	private Block getBlockAttachedToSign() {
		if(!signLocation.getBlock().getType().equals(Material.WALL_SIGN)){
			return null;
		}
		
		org.bukkit.material.Sign sign = (org.bukkit.material.Sign) signLocation.getBlock().getState().getData();
		return signLocation.getBlock().getRelative(sign.getAttachedFace());
	}

	/**
	 * gets the other block of a double chest.
	 * Null if the chest is no double chest.
	 * 
	 * @param block
	 * @return
	 */
	private Block getDoubleChest() {
		List<Block> toCheck = new LinkedList<Block>();
		
		toCheck.add(chestLocation.getBlock().getRelative(BlockFace.EAST));
		toCheck.add(chestLocation.getBlock().getRelative(BlockFace.WEST));
		toCheck.add(chestLocation.getBlock().getRelative(BlockFace.NORTH));
		toCheck.add(chestLocation.getBlock().getRelative(BlockFace.SOUTH));
		
		for(Block blockToCheck: toCheck){
			if(blockToCheck.getType().equals(Material.CHEST)){
				return blockToCheck;
			}
		}
		
		return null;
	}
	
}
