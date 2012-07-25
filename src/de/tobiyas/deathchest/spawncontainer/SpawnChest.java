package de.tobiyas.deathchest.spawncontainer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.permissions.PermissionNode;
import de.tobiyas.deathchest.util.Const;
import de.tobiyas.deathchest.util.PlayerInventoryModificator;
import de.tobiyas.util.config.YAMLConfigExtended;

public class SpawnChest {
	
	private Location loc;
	private int timeToDespawn;
	private boolean invalid;
	
	public SpawnChest(Location loc){
		this.loc = loc;
		timeToDespawn = 60 * DeathChest.getPlugin().getConfigManager().getspawnSignDespawnTime();
		invalid = false;
		
	}
	
	private SpawnChest(Location loc, int time){
		this.loc = loc;
		timeToDespawn = time;
		invalid = false;
	}
	
	public void saveChest(){
		File file = new File(Const.spawnChestFile);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				DeathChest.getPlugin().log("Could not create spawnChestPos.yml");
				return;
			}
		}
		
		YAMLConfigExtended config = new YAMLConfigExtended(Const.spawnChestFile).load();
		if(!config.getValidLoad())
			return;
		
		UUID uid = UUID.randomUUID();
		config.setLocation("chests." + uid.toString() + ".loc", loc);
		config.set("chests." + uid.toString() + ".despawn", timeToDespawn);
		
		config.save();
	}
	
	public static ArrayList<SpawnChest> loadChests(){
		ArrayList<SpawnChest> chests = new ArrayList<SpawnChest>();
		File file = new File(Const.spawnChestFile);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				DeathChest.getPlugin().log("Could not create spawnChestPos.yml");
				return chests;
			}
		}
		
		YAMLConfigExtended config = new YAMLConfigExtended(Const.spawnChestFile).load();
		if(!config.getValidLoad())
			return chests;
		
		for(String chest : config.getYAMLChildren("chests")){
			Location tempLoc = config.getLocation("chests." + chest + ".loc");
			int tempTime = config.getInt("chests." + chest + ".despawn");
			
			chests.add(new SpawnChest(tempLoc, tempTime));
			
		}
		
		return chests;
	}
	
	private void removeChest(){
		if(loc.getBlock().getType() == Material.CHEST)
			loc.getBlock().setType(Material.AIR);
		invalid = true;
	}
	
	
	public void tick(){
		timeToDespawn--;
		if(timeToDespawn <= 0)
			removeChest();
	}
	

	public boolean isInvalid() {
		return invalid;
	}
	
	
	
	//----------------------------------------------------------------------//
	//Static section//
	
	
	/**
	 * places a SpawnChest on the given Location
	 * 
	 * @param location
	 * @param player to copy the Inventory and check Permissions from
	 * @return the items stored
	 */
	public static LinkedList<ItemStack> placeSpawnChest(Player player){
		Location location = player.getLocation();
		LinkedList<ItemStack> emptyReturnList = new LinkedList<ItemStack>();
		
		if(!DeathChest.getPlugin().getPermissionsManager().checkPermissionsSilent(player, PermissionNode.spawnChest)) return emptyReturnList;
		
		if(!location.getBlock().getType().equals(Material.AIR)){
			location = getNextFreeBlock(location);
			if(location == null){
				player.sendMessage(ChatColor.RED + "The Block is blocked. It will not be replaced. Your stuff is dropped at your death location.");
				return null;
			}
		}
		
		if(DeathChest.getPlugin().getConfigManager().checkForWorldGuardCanBuild()){
			try{
				WorldGuardPlugin wgPlugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
				if(wgPlugin == null) throw new Exception();
				if(!wgPlugin.canBuild(player, location)) return emptyReturnList;
			}catch(Exception e){
				DeathChest.getPlugin().log("Error at check of WorldGuard. WorldGuard not Active. Deactivate WorldGuard-Options in Config!");
				DeathChest.getPlugin().getConfigManager().tempTurnOffWG();
			}
		}
		
		LinkedList<ItemStack> toRemove = new LinkedList<ItemStack>();
		
		if(DeathChest.getPlugin().getConfigManager().checkIfChestInInv()){
			if(!player.getInventory().contains(Material.CHEST)) return emptyReturnList;
			player.getInventory().removeItem(new ItemStack(Material.CHEST, 1));
			toRemove.add(new ItemStack(Material.CHEST, 1));
		}
		
		location.getBlock().setType(Material.CHEST);
		toRemove.addAll(copyInventoryToChest((Chest)location.getBlock().getState(), true, player));
		
		if(!toRemove.isEmpty()) 
			DeathChest.getPlugin().getProtectionManager().protectChest(location, player.getName());
		
		
		player.sendMessage(ChatColor.GREEN + "Your Inventory has been saved to a Chest on your Death-Position.");
		return toRemove;
	}
	
	private static Location getNextFreeBlock(Location location){
		Location tempLocation;
		for(int i = -1; i < 2; i++){
			for(int j = -1; j < 2; j++){
				for(int k = -1; k < 2; k++){
					tempLocation = location.clone();
					tempLocation.setX(tempLocation.getX() + i);
					tempLocation.setY(tempLocation.getY() + j);
					tempLocation.setZ(tempLocation.getZ() + k);
					
					if(tempLocation.getBlock().getType().equals(Material.AIR)) return tempLocation;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Copies a given Inventory of a Player to a chest
	 * 
	 * @param chest to copy to
	 * @param spawnChest if is a spawnchest
	 * @param player
	 * @return
	 */
	private static LinkedList<ItemStack> copyInventoryToChest(Chest chest, boolean spawnChest, Player player){
		PlayerInventory inventory = player.getInventory();
		
		PlayerInventoryModificator modifier = new PlayerInventoryModificator(inventory, player);
		modifier.modifyToConfig(spawnChest);
		
		HashMap<Integer, ItemStack> toDrop = modifier.getItems();
		
		return copyInventoryToChest(toDrop, chest);
	}
	
	/**
	 * copies a map of Items to a chest
	 * 
	 * @param toDrop the items to copy
	 * @param chest
	 * @return items copied
	 */
	private static LinkedList<ItemStack> copyInventoryToChest(HashMap<Integer, ItemStack> toDrop, Chest chest){
		Inventory chestInv = chest.getInventory();
		
		Block doubleChestBlock = getDoubleChest(chest.getBlock());
		boolean isDoubleChest = !(doubleChestBlock == null);
		
		LinkedList<ItemStack> toRemove = new LinkedList<ItemStack>();
		
		if(chestInv == null) return toRemove;
		
		
		for(Integer key : toDrop.keySet()){
			ItemStack item = toDrop.get(key);
			if(item == null) continue;
			if(chestInv.firstEmpty() == -1) {
				if(!isDoubleChest) break;
				isDoubleChest = false;
				
				chestInv = ((Chest)doubleChestBlock.getState()).getInventory();
				if(chestInv.firstEmpty() == -1) break;
			}
			chestInv.addItem(item);
			toRemove.add(item);
		}
		return toRemove;
	}
	
	/**
	 * gets the doubleChest of a Chest-Block
	 * if none is found, returns null
	 * 
	 * @param block
	 * @return Block the other ChestBlock
	 */
	private static Block getDoubleChest(Block block){
		if(block.getType() != Material.CHEST) return null;
		
		Block chestBlock;
		
		chestBlock = block.getRelative(BlockFace.NORTH);
		if(chestBlock.getType() == Material.CHEST) return chestBlock;
		
		chestBlock = block.getRelative(BlockFace.EAST);
		if(chestBlock.getType() == Material.CHEST) return chestBlock;
		
		chestBlock = block.getRelative(BlockFace.SOUTH);
		if(chestBlock.getType() == Material.CHEST) return chestBlock;
		
		chestBlock = block.getRelative(BlockFace.WEST);
		if(chestBlock.getType() == Material.CHEST) return chestBlock;
		
		return null;
	}

}
