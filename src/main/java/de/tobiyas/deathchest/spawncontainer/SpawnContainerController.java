package de.tobiyas.deathchest.spawncontainer;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.permissions.PermissionNode;
import de.tobiyas.deathchest.util.PlayerDropModificator;

/**
 * @author Toby
 *
 */
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
	
	/**
	 * Creates a new GraveYardSign / SpawnChest with the given {@link PlayerDropModificator}
	 * 
	 * @param piMod
	 * @return the list of Items NOT saved
	 */
	public List<ItemStack> createSpawnContainer(PlayerDropModificator piMod){
		Player player = piMod.getPlayer();
		piMod.modifyForSpawnContainer();
		
		World world = piMod.getPlayer().getWorld();
		if(worldIsOnIgnore(world)){
			return piMod.getTransferredItems();
		}
		
		if(!DeathChest.getPlugin().getPermissionManager().checkPermissionsSilent(player, PermissionNode.spawnChest)){
			return (piMod.getTransferredItems());
		}
		
		Class<?> clazz = plugin.getConfigManager().getSpawnContainerUsage();
		
		if(clazz == null){
			return piMod.getTransferredItems();
		}
			
		if(clazz.equals(SpawnChest.class)){
			return spawnChest(piMod);
		}
			
		if(clazz.equals(SpawnSign.class)){
			return createSign(piMod);
		}
		
		return null;
	}
	
	private boolean worldIsOnIgnore(World world) {
		String worldName = world.getName();
		
		for(String ignoreWorld : plugin.getConfigManager().getDisableSpawnContainerInWorlds()){
			if(ignoreWorld.equals(worldName)){
				return true;
			}
		}
		
		return false;
	}

	private List<ItemStack> spawnChest(PlayerDropModificator piMod){
		Player player = piMod.getPlayer();
		List<ItemStack> notDropped = SpawnChest.placeSpawnChest(piMod);
		if(notDropped.isEmpty()){
			SpawnChest chest = new SpawnChest(player.getLocation().getBlock().getLocation());
			chests.add(chest);
		}
		
		return notDropped;
	}
	
	/**
	 * Loads all GraveYardSigns saved in the Data-Path
	 * 
	 */
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
	
	
	/**
	 * Saves all GraveYardSigns to the Data-Path
	 * 
	 */
	public void saveAllSigns(){
		for(SpawnSign sign : signs)
			sign.saveSign();
	}
	
	
	/**
	 * Creates a new GraveYardSign with the given {@link PlayerDropModificator}
	 * 
	 * @param piMod
	 * @return the List of Items NOT saved
	 */
	public List<ItemStack> createSign(PlayerDropModificator piMod){
		Player player = piMod.getPlayer();
		SpawnSign sign = new SpawnSign(piMod).spawnSign();
		signs.add(sign);
		if(plugin.getConfigManager().getEXPMulti() >= 0)
			player.setTotalExperience(0);
		return new LinkedList<ItemStack>();
	}
	
	/**
	 * A player interacts with a Sign at a certain location
	 * 
	 * @param player, the interacting Player
	 * @param location, the Location of the Block interacted with
	 * @return true, if it worked
	 */
	public boolean interactSign(Player player, Location location){
		for(SpawnSign sign : signs){
			if(sign.isAt(location)){
				if(sign.interactSign(player)){
					signs.remove(sign);
					return true;
				}
				
				return false;
			}
		}
		
		return false;
	}
	
	
	/**
	 * Places a SpawnSign with the Attributes of the {@link PlayerDropModificator}
	 * 
	 * @param piMod
	 * @return a list of Items not saved (all if it didn't work, empty if it worked)
	 */
	public static List<ItemStack> placeSpawnChestOnLocation(PlayerDropModificator piMod){
		return SpawnChest.placeSpawnChest(piMod);
	}

	
	/**
	 * Returns all GraveStones of a Player with the given Name
	 * 
	 * @param playerName
	 * @return Set<SpawnSign> of the given player (empty if he has none)
	 */
	public Set<SpawnSign> getSpawnSigns(String playerName) {
		HashSet<SpawnSign> signSet = new HashSet<SpawnSign>();
		
		for(SpawnSign sign : signs){
			if(sign.getOwner().equals(playerName))
				signSet.add(sign);
		}
		
		return signSet;
	}
	
	
	
	/**
	 * Gets the SpawnSign number X from Player Y
	 * 
	 * @param playerName
	 * @param number
	 * @return  (null if not found)
	 */
	public SpawnSign getSpawnSignNumberOf(String playerName, int number) {
		HashSet<SpawnSign> signSet = new HashSet<SpawnSign>();
		
		for(SpawnSign sign : signs){
			if(sign.getOwner().equals(playerName))
				signSet.add(sign);
		}
		
		int i = 0;
		for(SpawnSign sign : signSet)
			if(i == number)
				return sign;
			else
				i++;
		
		return null;
	}
	
	
	/**
	 * ticks all Signs for fading
	 */
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

	/**
	 * Checks if one of the Signs saved is the passed block
	 * 
	 * @param block
	 * @return
	 */
	public boolean isSpawnSign(Block block) {
		if(block.getType().equals(Material.CHEST)){
			
			Block doubleChest = getDoubleChest(block);
			for(SpawnChest chest : chests){
				if(block.equals(doubleChest)){
					return true;
				}
				
				if(block.equals(chest.getLocation().getBlock())){
					return true;
				}
			}			
			
		}
		
		if(block.getType().equals(Material.SIGN_POST)){
			for(SpawnSign sign : signs){
				if(sign.getLocation().getBlock().equals(block)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * gets the other block of a double chest.
	 * Null if the chest is no double chest.
	 * 
	 * @param block
	 * @return
	 */
	private Block getDoubleChest(Block chestLocation) {
		List<Block> toCheck = new LinkedList<Block>();
		
		toCheck.add(chestLocation.getRelative(BlockFace.EAST));
		toCheck.add(chestLocation.getRelative(BlockFace.WEST));
		toCheck.add(chestLocation.getRelative(BlockFace.NORTH));
		toCheck.add(chestLocation.getRelative(BlockFace.SOUTH));
		
		for(Block blockToCheck: toCheck){
			if(blockToCheck.getType().equals(Material.CHEST)){
				return blockToCheck;
			}
		}
		
		return null;
	}
}
