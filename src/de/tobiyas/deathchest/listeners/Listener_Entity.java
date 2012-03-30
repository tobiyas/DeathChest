/*
 * DeathChest - by Tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.deathchest.listeners;


import java.util.HashMap;
import java.util.LinkedList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.chestpositions.ChestContainer;
import de.tobiyas.deathchest.chestpositions.ChestPosition;
import de.tobiyas.deathchest.permissions.PermissionNode;
import de.tobiyas.deathchest.util.PlayerInventoryModificator;


public class Listener_Entity  implements Listener{
	private DeathChest plugin;

	public Listener_Entity(DeathChest plugin){
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		boolean filledDeathChest = false;
		boolean filledSpawnChest = false;
		
		int originalDrops = event.getDrops().size();
		
		if(event.getDrops().isEmpty()) return;
		Player player = (Player) event.getEntity();
		Location location = player.getLocation();
		World world = location.getWorld();
		
		LinkedList<ItemStack> toRemove = saveToDeathChest(player);
		filledDeathChest = !toRemove.isEmpty();
		
		for(ItemStack item : toRemove)
			event.getDrops().remove(item);
		
		if(event.getDrops().isEmpty()) return;
		
		if(!filledDeathChest){
			toRemove = placeSpawnChestOnLocation(location, player);
			filledSpawnChest = !toRemove.isEmpty();
			
			for(ItemStack item : toRemove)
				event.getDrops().remove(item);
		}
			
		if(event.getDrops().size() > 0 && filledDeathChest){
			int maxTransfer = plugin.getChestContainer().getMaxTransferLimit(world);
			if(originalDrops > maxTransfer)
				player.sendMessage(ChatColor.RED + "Only " + maxTransfer + " items could be transfered. The rest is dropped at your death location.");
			else
				player.sendMessage(ChatColor.RED + "Your total inventory did not fit in the box. The rest items were dropped at your death location.");
		}
			
		if(!filledDeathChest && !filledSpawnChest && plugin.getPermissionsManager().CheckPermissionsSilent(player, PermissionNode.saveToDeathChest))
			player.sendMessage(ChatColor.RED + "You don't have a Chest set yet. Sorry for you. :(");
	}
		
	
	private LinkedList<ItemStack> saveToDeathChest(Player player){
		LinkedList<ItemStack> emptyReturnList = new LinkedList<ItemStack>();
		
		if(!plugin.getPermissionsManager().CheckPermissionsSilent(player, PermissionNode.saveToDeathChest)) return emptyReturnList;

		ChestContainer container = plugin.getChestContainer();
		if(!container.checkPlayerHasChest(player.getWorld(), player)){
			return emptyReturnList;
		}
			
		PlayerInventory inv = player.getInventory();
		ChestContainer chestContainer = container.getChestOfPlayer(player.getWorld(), player);
		
		ChestPosition chestPos = (ChestPosition) chestContainer;
		Location chestLocation = chestPos.getLocation();
		
		Block chestBlock = chestLocation.getBlock();
	
		if(chestBlock.getType() != Material.CHEST){
			player.sendMessage(ChatColor.RED + "No chest found at Position: " + 
					"X: " + chestLocation.getBlockX() + 
					"Y: " + chestLocation.getBlockY() + 
					"Z: " + chestLocation.getBlockZ());
			return emptyReturnList;
		}
		
		LinkedList<ItemStack> toRemove = copyInventoryToChest(inv, (Chest)chestBlock.getState(), false, player.getWorld());
		player.sendMessage(ChatColor.GREEN + "Your inventory was stored in your DeathChest on world: " + chestLocation.getWorld().getName() + ".");
		return toRemove;
	}
	
	private LinkedList<ItemStack> copyInventoryToChest(PlayerInventory inventory, Chest chest, boolean spawnChest, World world){
		PlayerInventoryModificator modifier = new PlayerInventoryModificator(inventory, world);
		modifier.modifyToConfig(spawnChest);
		
		HashMap<Integer, ItemStack> toDrop = modifier.getItems();
		
		return copyInventoryToChestOld(toDrop, chest);
	}
	
	private LinkedList<ItemStack> copyInventoryToChestOld(HashMap<Integer, ItemStack> toDrop, Chest chest){
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
	
	private Block getDoubleChest(Block block){
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
	
	private LinkedList<ItemStack> placeSpawnChestOnLocation(Location location, Player player){
		LinkedList<ItemStack> emptyReturnList = new LinkedList<ItemStack>();
		
		if(!plugin.getPermissionsManager().CheckPermissionsSilent(player, PermissionNode.spawnChest)) return emptyReturnList;
		if(plugin.getConfigManager().checkForWorldGuardCanBuild()){
			try{
				WorldGuardPlugin wgPlugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
				if(wgPlugin == null) throw new Exception();
				if(!wgPlugin.canBuild(player, location)) return emptyReturnList;
			}catch(Exception e){
				plugin.log("Error at check of WorldGuard. WorldGuard not Active. Deactivate WorldGuard-Options in Config!");
				plugin.getConfigManager().tempTurnOffWG();
			}
		}
		
		LinkedList<ItemStack> toRemove = new LinkedList<ItemStack>();
		
		if(plugin.getConfigManager().checkIfChestInInv()){
			if(!player.getInventory().contains(Material.CHEST)) return emptyReturnList;
			player.getInventory().removeItem(new ItemStack(Material.CHEST, 1));
			toRemove.add(new ItemStack(Material.CHEST, 1));
		}
		
		location.getBlock().setType(Material.CHEST);
		toRemove.addAll(copyInventoryToChest(player.getInventory(), (Chest)location.getBlock().getState(), true, player.getWorld()));
		
		if(!toRemove.isEmpty()) protectWithLWC(location, player);
		
		
		player.sendMessage(ChatColor.GREEN + "Your Inventory has been saved to a Chest on your Death-Position.");
		return toRemove;
	}
	
	private void protectWithLWC(Location location, Player player){
		if(plugin.getConfigManager().checkSpawnChestLWC()){
			try{
				LWCPlugin LWC = (LWCPlugin) Bukkit.getPluginManager().getPlugin("LWC");
				if(LWC == null) throw new Exception();
				
				String world = player.getWorld().getName();
				int blockID = location.getBlock().getTypeId();
				int x = location.getBlockX();
				int y = location.getBlockY();
				int z = location.getBlockZ();
				
				Protection protection = LWC.getLWC().getPhysicalDatabase().registerProtection(blockID, Protection.Type.PRIVATE, world, player.getName(), "", x, y, z);
				protection.save();
				
			}catch(Exception e){
				plugin.log("LWC not Found. Disable LWC Config options for SpawnChests!");
				plugin.getConfigManager().tempTurnOffLWC();
			}
		}
	}


}
