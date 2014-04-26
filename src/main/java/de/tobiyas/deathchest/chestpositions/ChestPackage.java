package de.tobiyas.deathchest.chestpositions;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.tobiyas.deathchest.DeathChest;

public class ChestPackage implements ChestContainer{

	private LinkedList<ChestContainer> packageContainer;
	private LinkedList<String> worldList;
	private boolean isControler;
	private PackageConfig pConfig;
	
	private String packageName;
	
	private YamlConfiguration packageParser;
	
	private static DeathChest plugin;
	private static LinkedList<String> allWorlds;
	
	
	/**
	 * Constructor to load all ChestPositions in a Package
	 * 
	 * @param packageName
	 * @param worlds
	 */
	private ChestPackage(String packageName, String[] worlds){
		plugin = DeathChest.getPlugin();
		this.packageName = packageName;
		isControler = false;
		packageContainer = new LinkedList<ChestContainer>();
		worldList = new LinkedList<String>();
				
		for(String worldString : worlds){
			if(allWorlds.contains(worldString)) plugin.log("ERROR in Settings! World: " + worldString + " is in more than 1 Package.");
			allWorlds.add(worldString);
			worldList.add(worldString);
		}
		
		String packagePath = plugin.getDataFolder() + File.separator + "packages" + File.separator + packageName + ".yml";
		File configPath = checkFile(packagePath);

		packageParser = new YamlConfiguration();
		try{
			packageParser.load(configPath);
		}catch(Exception e){
			plugin.log("Package Loading failed: " + packageName);
			return;
		}
		
		for(String playerName : getYAMLChildren(packageParser, packageName)){
			ChestPosition playerChest = new ChestPosition(packageParser, packageName, playerName);
			packageContainer.add(playerChest);
		}
		
		pConfig = new PackageConfig(packageParser, configPath);
		
	}
	
	
	
	
	/**
	 * creates all packages given through the packageProcessor
	 * 
	 * @param packageProcessor the config for the package information
	 */
	private ChestPackage(YamlConfiguration packageProcessor){
		
		packageContainer = new LinkedList<ChestContainer>();
		this.packageParser = packageProcessor;
		isControler = true;
		
		for(String packageName : getYAMLChildren(packageParser, "worldpackages")){
			String worldString = packageParser.getString("worldpackages." + packageName).toString();
			
			String[] worlds  = worldString.split(";");
			
			ChestContainer container = new ChestPackage(packageName, worlds);
			packageContainer.add(container);
		}
	}
	
	
	/**
	 * Util for YAMLReader to get all child-keys as Set<String> for a given Node 
	 * 
	 * @param config the YAMLConfiguration to search through
	 * @param yamlNode the Node to get the children from
	 * @return the children as Set<String>
	 */
	private Set<String> getYAMLChildren(YamlConfiguration config, String yamlNode){
		try{
			ConfigurationSection tempMem = config.getConfigurationSection(yamlNode);
			Set<String> tempSet = tempMem.getKeys(false);
			return tempSet;
			
		}catch(Exception e){
			Set<String> empty = new LinkedHashSet<String>();
			return empty;
		}
	}
	
	/**
	 * Factory to construct all Packages
	 * Returns the Package Construct
	 * 
	 * @return the ChestContainer containing all packages
	 */
	public static ChestContainer createALLPackages(){
		InitStructure.checkAndInitPaths();
		plugin = DeathChest.getPlugin();
		allWorlds = new LinkedList<World>();
		
		String pathPackages = plugin.getDataFolder() + File.separator + "packages.yml";
		File filePackages = checkFile(pathPackages);
		
		YamlConfiguration packageProcessor = new YamlConfiguration();
		
		try {
			packageProcessor.load(filePackages);
		} catch (Exception e) {
			plugin.log("Loading failed.");
			return null;
		}
		
		checkPath(plugin.getDataFolder()+ File.separator + "packages" + File.separator);
		
		ChestPackage container = new ChestPackage(packageProcessor);
 		return container;
	}
	
	private static File checkFile(String path){
		File file = new File(path);
		
		if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				plugin.log("Critical Error on File Creation: " + path);
			}
		
		return file;
	}
	
	private static void checkPath(String path){
		File file = new File(path);
		
		if(!file.exists())
			file.mkdir();
	}
	
	@Override
	public ChestContainer getChestOfPlayer(World world, Player player) {
		return getChestOfPlayer(world, player.getName());
	}


	@Override
	public boolean checkPlayerHasChest(World world, Player player) {
		return (getChestOfPlayer(world, player) != null);
	}

	@Override
	public boolean addChestToPlayer(Location location, Player player) {
		return addChestToPlayer(location, player.getName());
	}
	
	private void changePosition(Location location, String player){
		for(ChestContainer container : packageContainer){
			if(container.checkPlayerHasChest(location.getWorld(), player)){
				container.addChestToPlayer(location, player);
				return;
			}
		}
	}
	
	@Override
	public boolean hasWorld(World world){
		return worldList.contains(world.getName());
	}


	@Override
	public ChestContainer removeFromPosition(Location location) {
		World world = location.getWorld();
		if(!isControler)
			if(!worldList.contains(world.getName())) 
				return null;
		
		for(ChestContainer container : packageContainer){
			ChestContainer possibleContainer = container.removeFromPosition(location);
			if(possibleContainer != null){
				if(!isControler) {
					packageContainer.remove(container);
					return container;
				}
				return possibleContainer;
			}
		}
	return null;
	}


	@Override
	public boolean worldSupported(World world) {
		if(isControler){
			for(ChestContainer container : packageContainer){
				if(container.worldSupported(world)) return true;
			}
		}else{
			if(worldList.contains(world.getName())) return true;
		}
		return false;
	}


	@Override
	public int getMaxTransferLimit(Player player) {
		World world = player.getWorld();
		if(isControler){
			for(ChestContainer container : packageContainer){
				int containerMaxLimit = container.getMaxTransferLimit(player);
				if(containerMaxLimit > 0) return containerMaxLimit;
			}
		}else 
			if(worldList.contains(world.getName())) return pConfig.getMaxTrandferredItems(player);
			
		return -1;
	}




	@Override
	public ChestContainer getChestOfPlayer(World world, String player) {
		if(isControler){
			for(ChestContainer container : packageContainer){
				ChestContainer deathChestPosition = container.getChestOfPlayer(world, player);
				if(deathChestPosition != null) return deathChestPosition;
			}
			return null;
		}else{
			if(!worldList.contains(world.getName())) return null;
			for(ChestContainer container : packageContainer){
				ChestContainer deathChestPosition = container.getChestOfPlayer(world, player);
				if(deathChestPosition != null) return deathChestPosition;
			}
			
			return null;
		}
	}




	@Override
	public boolean addChestToPlayer(Location location, String player) {
		if(isControler){
			for(ChestContainer container : packageContainer){
				if(container.addChestToPlayer(location, player)) return true;
			}
		}else{
			if(!worldList.contains(location.getWorld().getName())) return false;
			if(!checkPlayerHasChest(location.getWorld(), player)){
				ChestContainer container = new ChestPosition(packageParser, packageName, player, location);
				packageContainer.add(container);
				return true;
			}else{
				changePosition(location, player);
				return true;
			}
		}
		
		return false;
	}




	@Override
	public boolean checkPlayerHasChest(World world, String player) {
		return (getChestOfPlayer(world, player) != null);
	}



	@Override
	public boolean isChestOrSignOfDC(Block block) {
		for(ChestContainer container : packageContainer){
			if(container.isChestOrSignOfDC(block)) return true;
		}
		
		return false;
	}

}
