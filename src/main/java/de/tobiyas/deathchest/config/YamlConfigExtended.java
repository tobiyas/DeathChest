package de.tobiyas.deathchest.config;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import de.tobiyas.deathchest.DeathChest;


public class YamlConfigExtended extends YamlConfiguration {

	private String savePath;
	private DeathChest plugin;
	
	public YamlConfigExtended(String savePath){
		super();
		this.savePath = savePath;
		plugin = DeathChest.getPlugin();
	}
	
	/**
	 * Util for YAMLReader to get all child-keys as Set<String> for a given Node 
	 * 
	 * @param config the YAMLConfiguration to search through
	 * @param yamlNode the Node to get the children from
	 * @return the children as Set<String>
	 */
	public Set<String> getYAMLChildren(String yamlNode){
		try{
			ConfigurationSection tempMem = getConfigurationSection(yamlNode);
			Set<String> tempSet = tempMem.getKeys(false);
			return tempSet;
			
		}catch(Exception e){
			Set<String> empty = new LinkedHashSet<String>();
			return empty;
		}
	}
	
	public void save(){
		File file = fileCheck(savePath);
		try {
			this.save(file);
		} catch (IOException e) {
			plugin.log("saving config failed.");
		}
	}
	
	public void load(){
		File savePathFile = plugin.getDataFolder();
		if(!savePathFile.exists())
			savePathFile.mkdir();
		
		File saveFile = fileCheck(savePath);
		if(saveFile == null) return;
				
		try {
			load(saveFile);
		} catch (Exception e) {
			plugin.log("Error on loading Enables");
		}
	}
	
	public void loadDelete(){
		File savePathFile = plugin.getDataFolder();
		if(!savePathFile.exists())
			savePathFile.mkdir();

		File saveFile = new File(savePath);
		if(!saveFile.exists()) return;
			
		try {
			load(saveFile);
		} catch (Exception e) {
			plugin.log("Error on loading Enables");
		}
		
		saveFile.delete();
	}
	
	private File fileCheck(String file){		
		File fileFile = new File(file);
		if(!fileFile.exists()){
			try {
				fileFile.createNewFile();
			} catch (IOException e) {
				plugin.log("Error creating new File :" + file);
				return null;
			}
		}
		return fileFile;
	}
}

