package de.tobiyas.deathchest.util;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.chestpositions.ChestContainer;
import de.tobiyas.deathchest.chestpositions.ChestPackage;

public class PackageReloader implements Runnable {

	private DeathChest plugin;
	
	private PackageReloader(){
		plugin = DeathChest.getPlugin();
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, 1);
	}
	
	@Override
	public void run() {
		ChestContainer container = ChestPackage.createALLPackages();
		plugin.reloadChestContainer(container);
	}
	
	public static void reload(){
		new PackageReloader();
	}

}
