package de.tobiyas.deathchest.spawncontainer;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.permissions.PermissionNode;

public class SpawnContainerController {

	private ArrayList<SpawnSign> signs;
	private ArrayList<SpawnChest> chests;
	private DeathChest plugin;
	
	public SpawnContainerController(){
		plugin = DeathChest.getPlugin();
		signs = new ArrayList<SpawnSign>();
		chests = new ArrayList<SpawnChest>();
		new SpawnContainerTicker();
		
		loadAllSigns();
		RandomText.createDefaults();
	}
	
	public LinkedList<ItemStack> createSpawnContainer(Player player){
		if(!DeathChest.getPlugin().getPermissionsManager().checkPermissionsSilent(player, PermissionNode.spawnChest)){
			return (new LinkedList<ItemStack>());
		}
		
		Class<?> clazz = plugin.getConfigManager().getSpawnContainerUsage();
		
		if(clazz == null)
			return new LinkedList<ItemStack>();
		
		if(clazz.equals(SpawnChest.class))
			return spawnChest(player);
		
		if(clazz.equals(SpawnSign.class))
			return createSign(player);
		
		return null;
	}
	
	private LinkedList<ItemStack> spawnChest(Player player){
		LinkedList<ItemStack> stack = SpawnChest.placeSpawnChest(player);
		if(!stack.isEmpty()){
			SpawnChest chest = new SpawnChest(player.getLocation().getBlock().getLocation());
			chests.add(chest);
		}
		
		return stack;
	}
	
	public void loadAllSigns(){
		File file = new File(plugin.getDataFolder() + File.separator + "gravestones" + File.separator);
		if(!file.exists()) return;
		
		//File filter for directories
		FileFilter fileFilter = new FileFilter() {
		public boolean accept(File file) {
		       return file.isFile();
			}
		};
		
		for(File possibleSign : file.listFiles(fileFilter)){
			SpawnSign sign = SpawnSign.loadSign(possibleSign.toString());
			if(sign != null) signs.add(sign);
		}
		
		if(signs.size() != 0)
			plugin.log("loaded " + signs.size() + " graves");
	}
	
	public void saveAllSigns(){
		for(SpawnSign sign : signs)
			sign.saveSign();
	}
	
	public LinkedList<ItemStack> createSign(Player player){
		SpawnSign sign = new SpawnSign(player).spawnSign();
		signs.add(sign);
		if(plugin.getConfigManager().getEXPMulti() >= 0)
			player.setTotalExperience(0);
		return sign.getItems();
	}
	
	public boolean interactSign(Player player, Location location){
		for(SpawnSign sign : signs){
			if(sign.isAt(location)){
				sign.interactSign(player);
				signs.remove(sign);
				return true;
			}
		}
		
		return false;
	}
	
	public static LinkedList<ItemStack> placeSpawnChestOnLocation(Player player){
		return SpawnChest.placeSpawnChest(player);
	}

	public Set<SpawnSign> getSpawnSigns(Player player) {
		HashSet<SpawnSign> signSet = new HashSet<SpawnSign>();
		
		for(SpawnSign sign : signs){
			if(sign.getOwner().equals(player.getName()))
				signSet.add(sign);
		}
		
		return signSet;
	}
	
	
	public void tick(){
		if(signs != null && signs.size() > 0){
			ArrayList<SpawnSign> tempSigns = new ArrayList<SpawnSign>();
			for(SpawnSign sign : signs){
				if(sign != null){
					sign.tick();
					if(sign.toRemove())
						tempSigns.add(sign);
				}
			}
			
			for(SpawnSign sign : tempSigns)
				signs.remove(sign);
		}
		
		if(chests != null && chests.size() > 0){
			ArrayList<SpawnChest> removeChests = new ArrayList<SpawnChest>();
			for(SpawnChest chest : chests){
				if(chest != null && !chest.isInvalid())
					chest.tick();
				else
					removeChests.add(chest);
			}
			
			for(SpawnChest remove : removeChests)
				chests.remove(remove);
		}
		
	}
}
