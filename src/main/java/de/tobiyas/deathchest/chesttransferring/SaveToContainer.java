package de.tobiyas.deathchest.chesttransferring;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.chestpositions.ChestContainer;
import de.tobiyas.deathchest.chestpositions.ChestPosition;
import de.tobiyas.deathchest.permissions.PermissionNode;
import de.tobiyas.deathchest.util.PlayerDropModificator;

public class SaveToContainer {
	
	private static DeathChest plugin = DeathChest.getPlugin();

	/**
	 * saves the PlayerInventory to his deathChest
	 * returns an empty list if player has no Permission or no DeathChest exists for the player
	 * 
	 * @param player
	 * @return the list of Items saved
	 */
	public static List<ItemStack> saveToDeathChest(PlayerDropModificator piMod){
		LinkedList<ItemStack> emptyReturnList = new LinkedList<ItemStack>();
		Player player = piMod.getPlayer();
		
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
		int totalExperience = (int) (piMod.getEXP() * multiplicator);
		chestPos.addEXP(totalExperience);
		piMod.removeAllEXP();
		
		if(multiplicator != 0)
			player.setTotalExperience(0);
		
		piMod.modifyForDeathChest();
		List<ItemStack> toRemove = copyInventoryToChest(piMod.getTransferredItems(), (Chest)chestBlock.getState());		
		player.sendMessage(ChatColor.GREEN + "Your inventory was stored in your DeathChest on world: " + chestLocation.getWorld().getName() + ".");
		return toRemove;
	}
	
	
	/**
	 * copies a map of Items to a chest
	 * 
	 * @param toDrop the items to copy
	 * @param chest
	 * @return items not copied
	 */
	private static List<ItemStack> copyInventoryToChest(List<ItemStack> toDrop, Chest chest){
		Inventory chestInv = chest.getInventory();
		
		Block doubleChestBlock = getDoubleChest(chest.getBlock());
		boolean isDoubleChest = doubleChestBlock != null;
		
		LinkedList<ItemStack> notRemove = new LinkedList<ItemStack>();
		
		if(chestInv == null) return toDrop;
		
		boolean isFull = false;
		
		for(ItemStack item : toDrop){
			if(item == null) continue;
			if(chestInv.firstEmpty() == -1) {
				if(!isDoubleChest){
					isFull = true;
				}else{
					chestInv = ((Chest)doubleChestBlock.getState()).getInventory();
					if(chestInv.firstEmpty() == -1) isFull = true;
				}
			}

			if(isFull)
				notRemove.add(item);
			else
				chestInv.addItem(item);
		}
		
		return notRemove;
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
	
	private static boolean simplePermissionUse(Player player){
		return plugin.getPermissionManager().hasAnyPermissionSilent(player, PermissionNode.simpleUseArray);
	}

}
