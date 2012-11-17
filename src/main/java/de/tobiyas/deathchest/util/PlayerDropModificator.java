package de.tobiyas.deathchest.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import de.tobiyas.deathchest.DeathChest;

public class PlayerDropModificator {

	private DeathChest plugin;
	private List<ItemStack> totalItemList;
	private List<ItemStack> itemsToTransfer;
	private List<ItemStack> itemsToDrop;
	
	private int exp;
	private Player player;
	
	/**
	 * Sets up an Modificator for a Player inventory
	 * 
	 * @param inventory of the Player
	 * @param player
	 */
	public PlayerDropModificator(Player player, List<ItemStack> drops, int exp){
		this.totalItemList = new LinkedList<ItemStack>();
		this.itemsToDrop = new LinkedList<ItemStack>();
		this.itemsToTransfer = new LinkedList<ItemStack>();
		this.player = player;
		
		this.totalItemList = drops;
		this.exp = exp;
		
		plugin = DeathChest.getPlugin();
		player.getInventory().clear();
	}
	
	/**
	 * modifies the inventory of the Player to the given configuration
	 * 
	 * @param spawnChest if the items will be stored to a spawnchest
	 */
	public void modifyForDeathChest(){
		int limit = plugin.getChestContainer().getMaxTransferLimit(player);
		boolean random = plugin.getConfigManager().checkRandomPick();
		
		limitItemDrops(limit, random);
	}
	
	/**
	 * modifies the inventory of the Player to the given configuration
	 * 
	 * 
	 */
	public void modifyForSpawnContainer(){
		itemsToTransfer.addAll(totalItemList);
	}
	
	
	/**
	 * limits the items to a given limit
	 * 
	 * @param limit the limit to limit to
	 * @param random if the items should be removed randomly
	 */
	private void limitItemDrops(int limit, boolean random){
		Collections.shuffle(totalItemList);
		int count = 0;
		for(ItemStack item : totalItemList){
			count++;
			if(count > limit)
				itemsToDrop.add(item);
			else
				itemsToTransfer.add(item);
		}
	}
		
	
	public void removeItemsTransferred(){
		for(ItemStack item : itemsToTransfer){
			totalItemList.remove(item);
		}
		itemsToTransfer.clear();
	}
	
	public void removeItemsDropped(){
		for(ItemStack item : itemsToDrop){
			totalItemList.remove(item);
		}
		itemsToDrop.clear();
	}

	
	//PUBLIC GETTER / SETTER
	
	public List<ItemStack> getTransferredItems(){
		return itemsToTransfer;
	}
	
	public List<ItemStack> getALLItemsAsList(){
		return totalItemList;
	}
	
	public List<ItemStack> getDropList(){
		return itemsToDrop;
	}
	
	public void reAddToDrop(List<ItemStack> stack){
		List<ItemStack> tempList = new LinkedList<ItemStack>();
		for(ItemStack item : stack)
			tempList.add(item.clone());
		
		for(ItemStack item : tempList)
			if(itemsToTransfer.contains(item))
				itemsToTransfer.remove(item);
			else
				itemsToDrop.remove(item);
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public boolean stilHasItems(){
		return !totalItemList.isEmpty();
	}
	
	public int getEXP(){
		return exp;
	}
	
	public boolean stilHasEXP(){
		return exp != 0;
	}
	
	public void removeEXP(int amount){
		exp -= amount;
	}
	
	public void removeAllEXP(){
		exp = 0;
	}
}
