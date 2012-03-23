package de.tobiyas.deathchest.chestpositions;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.tobiyas.deathchest.DeathChest;

public class ChestWorlds implements ChestContainer{

	private LinkedList<ChestContainer> packageContainer;
	private LinkedList<World> worldList;
	private boolean isControler;
	
	private String packageName;
	
	private YamlConfiguration packageConfig;
	private static DeathChest plugin;
	
	private static LinkedList<World> allWorlds;
	
	//constructor for package
	private ChestWorlds(String packageName, String[] worlds){
		plugin = DeathChest.getPlugin();
		this.packageName = packageName;
		isControler = false;
		packageContainer = new LinkedList<ChestContainer>();
		worldList = new LinkedList<World>();
				
		for(String worldString : worlds){
			World world = Bukkit.getWorld(worldString);
			if(world == null) continue;
			if(allWorlds.contains(world)) plugin.log("ERROR in Settings! World: " + worldString + " is in more than 1 Package.");
			allWorlds.add(world);
			worldList.add(world);
		}
		
		String packagePath = plugin.getDataFolder() + File.separator + "packages" + File.separator + packageName + ".yml";
		File configPath = checkFile(packagePath);

		packageConfig = new YamlConfiguration();
		try{
			packageConfig.load(configPath);
		}catch(Exception e){
			plugin.log("Package Loading failed: " + packageName);
			return;
		}
		
		for(String playerName : getYAMLChildren(packageConfig, packageName)){
			ChestPosition playerChest = new ChestPosition(packageConfig, packageName, playerName);
			packageContainer.add(playerChest);
		}
	}
	
	
	//constructor for creating all packages
	private ChestWorlds(YamlConfiguration packageProcessor){
		
		packageContainer = new LinkedList<ChestContainer>();
		this.packageConfig = packageProcessor;
		isControler = true;
		
		for(String packageName : getYAMLChildren(packageConfig, "worldpackages")){
			String worldString = packageConfig.getString("worldpackages." + packageName).toString();
			
			String[] worlds  = worldString.split(";");
			
			ChestContainer container = new ChestWorlds(packageName, worlds);
			packageContainer.add(container);
		}
	}
	
	
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
	
	
	//Static Constructor for creating all packages
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
		
		ChestWorlds container = new ChestWorlds(packageProcessor);
 		return container;
	}
	
	@Override
	public Location getChestOfPlayer(World world, Player player) {
		if(isControler){
			for(ChestContainer container : packageContainer){
				Location deathChestPosition = container.getChestOfPlayer(world, player);
				if(deathChestPosition != null) return deathChestPosition;
			}
			return null;
		}else{
			if(!worldList.contains(world)) return null;
			for(ChestContainer container : packageContainer){
				Location deathChestPosition = container.getChestOfPlayer(world, player);
				if(deathChestPosition != null) return deathChestPosition;
			}
			
			return null;
		}
	}


	@Override
	public boolean checkPlayerHasChest(World world, Player player) {
		return (getChestOfPlayer(world, player) != null);
	}

	@Override
	public boolean addChestToPlayer(Location location, Player player) {
		if(isControler){
			for(ChestContainer container : packageContainer){
				if(container.addChestToPlayer(location, player)) return true;
			}
		}else{
			if(!worldList.contains(location.getWorld())) return false;
			if(!checkPlayerHasChest(location.getWorld(), player)){
				ChestContainer container = new ChestPosition(packageConfig, packageName, player, location);
				packageContainer.add(container);
				plugin.log("New Creation.");
				return true;
			}else{
				changePosition(location, player);
				return true;
			}
		}
		
		return false;
	}
	
	private void changePosition(Location location, Player player){
		for(ChestContainer container : packageContainer){
			if(container.checkPlayerHasChest(location.getWorld(), player)){
				container.addChestToPlayer(location, player);
				return;
			}
		}
	}
	
	@Override
	public boolean hasWorld(World world){
		return worldList.contains(world);
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
	public boolean removeFromPosition(Location location) {
		World world = location.getWorld();
		if(!isControler)
			if(!worldList.contains(world)) 
				return false;
		
		for(ChestContainer container : packageContainer){
			if(container.removeFromPosition(location)){
				if(!isControler) packageContainer.remove(container);
				return true;
			}
		}
	return false;
	}

}
