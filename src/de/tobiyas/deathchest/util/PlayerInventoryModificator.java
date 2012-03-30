package de.tobiyas.deathchest.util;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import de.tobiyas.deathchest.DeathChest;

public class PlayerInventoryModificator {

	private PlayerInventory inventory;
	private DeathChest plugin;
	private HashMap<Integer, ItemStack> map;
	private World world;
	
	public PlayerInventoryModificator(PlayerInventory inventory, World world){
		this.inventory = inventory;
		this.map = new HashMap<Integer, ItemStack>();
		this.world = world;
		
		plugin = DeathChest.getPlugin();
	}
	
	public void modifyToConfig(boolean spawnChest){
		int count = 0;
		for(ItemStack stack : inventory.getContents())
			if(stack != null) map.put(++count, stack);
		
		if(plugin.getConfigManager().checkTransferEquip())
			for(ItemStack equip : inventory.getArmorContents())
				if((equip != null) && (equip.getTypeId() != 0)) map.put(++count, equip);
				
		
		if(spawnChest) return;
		
		
		int limit = plugin.getChestContainer().getMaxTransferLimit(world);
		boolean random = plugin.getConfigManager().checkRandomPick();
		
		limitItemDrops(limit, random);
	}
	
	public HashMap<Integer, ItemStack> getItems(){
		return map;
	}
	
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
	
	private void removeRandomOffSet(){
		if(map.size() == 0) return;
		
		int randomNr = (int) Math.floor(Math.random() * map.size());
		Set<Integer> set = map.keySet();
		
		Object[] stack = set.toArray();
		Integer item =  (Integer) stack[randomNr];
		map.remove(item);
	}
	
	private void removeLast(){
		if(map.size() == 0) return;
		
		Set<Integer> set = map.keySet();
		
		Object[] stack = set.toArray();
		Integer item =  (Integer) stack[0];
		map.remove(item);
	}
}
