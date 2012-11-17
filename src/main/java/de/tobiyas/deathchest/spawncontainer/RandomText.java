package de.tobiyas.deathchest.spawncontainer;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import de.tobiyas.deathchest.config.YamlConfigExtended;
import de.tobiyas.deathchest.DeathChest;

public class RandomText {
	
	private static YamlConfigExtended config = new YamlConfigExtended(DeathChest.getPlugin().getDataFolder() + File.separator + "graveSentences.yml");
	private static Random rand = new Random();
	
	public static List<String> randomText(){
		config.load();
		
		Set<String> available = config.getYAMLChildren("");
		int next = rand.nextInt(available.size());
		String id = (String) available.toArray()[next];
		
		List<String> list = config.getStringList(id);
		
		return list;
	}
	
	public static void createDefaults(){
		File file = new File(DeathChest.getPlugin().getDataFolder() + File.separator + "graveSentences.yml");
		if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				DeathChest.getPlugin().log("Error at creating signTexts.");
			}
		config.load();
		
		if(config.getYAMLChildren("").size() == 0){
			LinkedList<String> list = new LinkedList<String>();
			list.add("He died like");
			list.add("he lived");
			config.set("1", list);
			
			LinkedList<String> list2 = new LinkedList<String>();
			list2.add("He wanted to");
			list2.add("hug a creeper");
			config.set("2", list2);
			
			config.save();
		}
	}
}
