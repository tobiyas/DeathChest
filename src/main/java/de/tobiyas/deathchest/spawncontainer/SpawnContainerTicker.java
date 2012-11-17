package de.tobiyas.deathchest.spawncontainer;

import org.bukkit.Bukkit;

import de.tobiyas.deathchest.DeathChest;

public class SpawnContainerTicker implements Runnable{
	
	private DeathChest plugin;
	
	public SpawnContainerTicker(){
		plugin = DeathChest.getPlugin();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 20, 20);
	}
	
	@Override
	public void run() {
		plugin.interactSpawnContainerController().tick();
	}

}
