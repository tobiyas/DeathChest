/*
 * DeathChest - by Tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.deathchest.listeners;


import java.util.LinkedList;

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

import de.tobiyas.deathchest.DeathChest;

import de.tobiyas.deathchest.chestpositions.ChestContainer;


public class Listener_Entity  implements Listener{
	private DeathChest plugin;

	public Listener_Entity(DeathChest plugin){
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		Player player = (Player) event.getEntity();
		if(!plugin.getPermissionsManager().CheckPermissionsSilent(player, "saveafterdeath")) return;

		ChestContainer container = plugin.getChestContainer();
		if(!container.checkPlayerHasChest(player, player.getWorld())){
			player.sendMessage(ChatColor.RED + "You don't have a Chest set. Sorry for you. :(");
			return;
		}
			
		PlayerInventory inv = player.getInventory();
		Location chestLocation = container.getChestOfPlayer(player.getWorld(), player);					
		Block chestBlock = player.getWorld().getBlockAt(chestLocation);
	
		if(chestBlock.getType() != Material.CHEST){
			player.sendMessage(ChatColor.RED + "No chest found at Position: " + 
					"X: " + chestLocation.getBlockX() + 
					"Y: " + chestLocation.getBlockY() + 
					"Z: " + chestLocation.getBlockZ());
			return;
		}
		
		LinkedList<ItemStack> toRemove = copyInventoryToChest(inv, (Chest)chestBlock.getState());
		event.getDrops().removeAll(toRemove);
		
		player.sendMessage(ChatColor.GREEN + "Your inventory was stored in your DeathChest on world: " + player.getWorld().getName() + ".");
		if(event.getDrops().size() > 0) player.sendMessage("Your total inventory did not fit in the box. The rest items were dropped at your death location.");
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


}
