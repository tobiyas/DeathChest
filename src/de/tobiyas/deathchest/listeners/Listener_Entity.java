/*
 * DeathChest - by Tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.deathchest.listeners;


import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import de.tobiyas.deathchest.DeathChest;

import de.tobiyas.deathchest.chestpositions.ChestContainer;
import de.tobiyas.deathchest.permissions.PermissionNode;


public class Listener_Entity  implements Listener{
	private DeathChest plugin;

	public Listener_Entity(DeathChest plugin){
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		boolean filledDeathChest = false;
		boolean filledSpawnChest = false;
		
		if(event.getDrops().isEmpty()) return;
		Player player = (Player) event.getEntity();
		Location location = player.getLocation();
		
		LinkedList<ItemStack> toRemove = saveToDeathChest(player);
		filledDeathChest = !toRemove.isEmpty();
		event.getDrops().removeAll(toRemove);
		
		if(event.getDrops().isEmpty()) return;
		
		if(!filledDeathChest){
			toRemove = placeSpawnChestOnLocation(location, player);
			filledSpawnChest = !toRemove.isEmpty();
			event.getDrops().removeAll(toRemove);
		}
			
		if(event.getDrops().size() > 0 && filledDeathChest)
			player.sendMessage(ChatColor.RED + "Your total inventory did not fit in the box. The rest items were dropped at your death location.");
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
		Location chestLocation = container.getChestOfPlayer(player.getWorld(), player);					
		Block chestBlock = chestLocation.getBlock();
	
		if(chestBlock.getType() != Material.CHEST){
			player.sendMessage(ChatColor.RED + "No chest found at Position: " + 
					"X: " + chestLocation.getBlockX() + 
					"Y: " + chestLocation.getBlockY() + 
					"Z: " + chestLocation.getBlockZ());
			return emptyReturnList;
		}
		
		LinkedList<ItemStack> toRemove = copyInventoryToChest(inv, (Chest)chestBlock.getState());
		player.sendMessage(ChatColor.GREEN + "Your inventory was stored in your DeathChest on world: " + chestLocation.getWorld().getName() + ".");
		return toRemove;
	}
	
	private LinkedList<ItemStack> copyInventoryToChest(PlayerInventory inventory, Chest chest){
		Inventory chestInv = chest.getInventory();
		Inventory playerInv = inventory;
		
		LinkedList<ItemStack> toRemove = new LinkedList<ItemStack>();
		
		if(chestInv == null) return toRemove;
		
		DoubleChestInventory dChestInv = null;
		
		if(chest.getInventory() instanceof DoubleChestInventory){
			dChestInv = (CraftInventoryDoubleChest) chestInv;
			chestInv = (CraftInventory) dChestInv.getLeftSide();
		}
		
		for(ItemStack item : playerInv){
			if(chestInv.firstEmpty() == -1 && dChestInv == null) return toRemove;
			if(chestInv.firstEmpty() == -1 ) {
				chestInv = (CraftInventory) dChestInv.getRightSide();
				dChestInv = null;
				
				if(chestInv.firstEmpty() == -1) return toRemove;
			}
			if(item == null) continue;
			chestInv.addItem(item);
			toRemove.add(item);
		}
		return toRemove;
	}
	
	private LinkedList<ItemStack> placeSpawnChestOnLocation(Location location, Player player){
		LinkedList<ItemStack> emptyReturnList = new LinkedList<ItemStack>();
		
		if(!plugin.getPermissionsManager().CheckPermissionsSilent(player, PermissionNode.spawnChest)) return emptyReturnList;
		if(plugin.getConfigManager().checkForWorldGuardCanBuild()){
			try{
				WorldGuardPlugin wgPlugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
				if(!wgPlugin.canBuild(player, location)) return emptyReturnList;
			}catch(Exception e){
				plugin.log("Error at check of WorldGuard. WorldGuard not Active.");
			}
		}
		
		if(plugin.getConfigManager().checkIfChestInInv()){
			if(!player.getInventory().contains(Material.CHEST)) return emptyReturnList;
			player.getInventory().removeItem(new ItemStack(Material.CHEST, 1));
		}
		
		location.getBlock().setType(Material.CHEST);
		LinkedList<ItemStack> toRemove = copyInventoryToChest(player.getInventory(), (Chest)location.getBlock().getState());
		
		if(!toRemove.isEmpty()) protectWithLWC(location, player);
		
		
		player.sendMessage(ChatColor.GREEN + "Your Inventory has been saved to a Chest on your Death-Position.");
		return toRemove;
	}
	
	private void protectWithLWC(Location location, Player player){
		if(plugin.getConfigManager().checkSpawnChestLWC()){
			try{
				LWCPlugin LWC = (LWCPlugin) Bukkit.getPluginManager().getPlugin("LWC");
				
				String world = player.getWorld().getName();
				int blockID = location.getBlock().getTypeId();
				int x = location.getBlockX();
				int y = location.getBlockY();
				int z = location.getBlockZ();
				
				Protection protection = LWC.getLWC().getPhysicalDatabase().registerProtection(blockID, Protection.Type.PRIVATE, world, player.getName(), "", x, y, z);
				protection.save();
				
			}catch(Exception e){
				plugin.log("LWC not Found. Disable LWC Config options for SpawnChests!");
			}
		}
	}


}
