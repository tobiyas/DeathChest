package de.tobiyas.deathchest.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import de.tobiyas.deathchest.DeathChest;

public class PlayerInventoryModificator {

	private PlayerInventory inventory;
	private DeathChest plugin;
	private HashMap<Integer, ItemStack> map;
	private Player player;
	
	/**
	 * Sets up an Modificator for a Player inventory
	 * 
	 * @param inventory of the Player
	 * @param player
	 */
	public PlayerInventoryModificator(PlayerInventory inventory, Player player){
		this.inventory = inventory;
		this.map = new HashMap<Integer, ItemStack>();
		this.player = player;
		
		plugin = DeathChest.getPlugin();
	}
	
	/**
	 * modifies the inventory of the Player to the given configuration
	 * 
	 * @param spawnChest if the items will be stored to a spawnchest
	 */
	public void modifyToConfig(boolean spawnChest){
		int count = 0;
		for(ItemStack stack : inventory.getContents())
			if(stack != null) map.put(++count, stack);
		
		if(plugin.getConfigManager().checkTransferEquip())
			for(ItemStack equip : inventory.getArmorContents())
				if((equip != null) && (equip.getTypeId() != 0)) map.put(++count, equip);
				
		
		if(spawnChest) return;
		
		
		int limit = plugin.getChestContainer().getMaxTransferLimit(player);
		boolean random = plugin.getConfigManager().checkRandomPick();
		
		limitItemDrops(limit, random);
	}
	
	/**
	 * Returns a HashMap with a List of Items left in the Inventory
	 * Keys = some Integer to avoid deleting doubled stacks
	 * 
	 * @return the HashMap with Items
	 */
	public HashMap<Integer, ItemStack> getItems(){
		return map;
	}
	
	public ArrayList<ItemStack> getItemsAsList(){
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		for(int id : map.keySet()){
			ItemStack tempStack = map.get(id);
			if(tempStack != null) list.add(tempStack);
		}
		
		return list;
	}
	
	/**
	 * limits the items to a given limit
	 * 
	 * @param limit the limit to limit to
	 * @param random if the items should be removed randomly
	 */
	private void limitItemDrops(int limit, boolean random){
		if(map.size() <= limit) return;
		
		int toKick = map.size() - limit;
		
		for(int i = toKick; i > 0; i--){
			Math.random();
			if(random)
				removeRandomOffSet();
			else
				removeLast();
		}
	}
	
	/**
	 * removes an Item by random
	 */
	private void removeRandomOffSet(){
		if(map.size() == 0) return;
		
		int randomNr = (int) Math.floor(Math.random() * map.size());
		Set<Integer> set = map.keySet();
		
		Object[] stack = set.toArray();
		Integer item =  (Integer) stack[randomNr];
		map.remove(item);
	}
	
	/**
	 *  removes the last Item
	 */
	private void removeLast(){
		if(map.size() == 0) return;
		
		Set<Integer> set = map.keySet();
		
		Object[] stack = set.toArray();
		Integer item =  (Integer) stack[0];
		map.remove(item);
	}
}
