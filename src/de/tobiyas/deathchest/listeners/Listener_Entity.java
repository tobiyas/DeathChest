/*
 * http://
 *
 * powered by Kickstarter
 * DeathChest - by Tobiyas
 */

package de.tobiyas.deathchest.listeners;


import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.chestpositions.ChestContainer;
import de.tobiyas.deathchest.chestpositions.ChestPosition;
import de.tobiyas.deathchest.permissions.PermissionNode;
import de.tobiyas.deathchest.spawncontainer.SpawnSign;
import de.tobiyas.deathchest.util.PlayerInventoryModificator;


/**
 * @author tobiyas
 *
 */
public class Listener_Entity  implements Listener{
	private DeathChest plugin;

	public Listener_Entity(DeathChest plugin){
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		if(plugin.isBattleNight(event.getEntity())) 
			return;
		
		boolean filledDeathChest = false;
		boolean filledSpawnChest = false;
		
		int originalDrops = event.getDrops().size();
		
		if(event.getDrops().isEmpty() && event.getDroppedExp() == 0) return;
		Player player = (Player) event.getEntity();
		
		int exp = player.getTotalExperience();
		
		LinkedList<ItemStack> toRemove = saveToDeathChest(player);
		filledDeathChest = !toRemove.isEmpty();
		
		if(exp != player.getTotalExperience()){
			event.setDroppedExp(0);
		}
		
		for(ItemStack item : toRemove)
			event.getDrops().remove(item);
		
		if(event.getDrops().isEmpty() && (event.getDroppedExp() == 0 || plugin.getConfigManager().getEXPMulti() == 0)) return;
		
		if(!event.getDrops().isEmpty() || player.getTotalExperience() != 0){
			toRemove = plugin.interactSpawnContainerController().createSpawnContainer(player);
			
			if(plugin.getConfigManager().getSpawnContainerUsage().equals(SpawnSign.class) && player.getTotalExperience() != exp) 
				event.setDroppedExp(0);
			
			if(toRemove != null){
				filledSpawnChest = !toRemove.isEmpty();
				
				for(ItemStack item : toRemove)
					event.getDrops().remove(item);
			}
		}
			
		if(event.getDrops().size() > 0 && filledDeathChest){
			int maxTransfer = plugin.getChestContainer().getMaxTransferLimit(player);
			if(originalDrops > maxTransfer)
				player.sendMessage(ChatColor.RED + "Only " + maxTransfer + " items could be transfered. The rest is dropped at your death location.");
			else
				player.sendMessage(ChatColor.RED + "Your total inventory did not fit in the box. The rest items were dropped at your death location.");
		}
			
		if(!filledDeathChest && !filledSpawnChest && simplePermissionUse(player))
			player.sendMessage(ChatColor.RED + "You don't have a Chest set yet. Sorry for you. :(");
	}
	
	private boolean simplePermissionUse(Player player){
		return plugin.getPermissionsManager().hasAnyPermissionSilent(player, PermissionNode.simpleUseArray);
	}
	
	/**
	 * saves the PlayerInventory to his deathChest
	 * returns an empty list if player has no Permission or no DeathChest exists for the player
	 * 
	 * @param player
	 * @return the list of Items saved
	 */
	private LinkedList<ItemStack> saveToDeathChest(Player player){
		LinkedList<ItemStack> emptyReturnList = new LinkedList<ItemStack>();
		
		if(!simplePermissionUse(player))
			return emptyReturnList;
		
		ChestContainer container = plugin.getChestContainer();
		if(!container.checkPlayerHasChest(player.getWorld(), player)){
			return emptyReturnList;
		}
		
		int maxTransferItems = plugin.getChestContainer().getMaxTransferLimit(player);	
		if(maxTransferItems <= 0) return emptyReturnList;
			
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
		
		double multiplicator = plugin.getConfigManager().getEXPMulti();
		int totalExperience = (int) (player.getTotalExperience() * multiplicator);
		chestPos.addEXP(totalExperience);
		
		if(multiplicator != 0)
			player.setTotalExperience(0);
		
		LinkedList<ItemStack> toRemove = copyInventoryToChest((Chest)chestBlock.getState(), false, player);
		player.sendMessage(ChatColor.GREEN + "Your inventory was stored in your DeathChest on world: " + chestLocation.getWorld().getName() + ".");
		return toRemove;
	}
	
	/**
	 * Copies a given Inventory of a Player to a chest
	 * 
	 * @param chest to copy to
	 * @param spawnChest if is a spawnchest
	 * @param player
	 * @return
	 */
	private LinkedList<ItemStack> copyInventoryToChest(Chest chest, boolean spawnChest, Player player){
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
	private LinkedList<ItemStack> copyInventoryToChest(HashMap<Integer, ItemStack> toDrop, Chest chest){
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
	private Block getDoubleChest(Block block){
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
