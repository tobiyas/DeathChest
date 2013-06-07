package de.tobiyas.deathchest.listeners;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import de.tobiyas.deathchest.DeathChest;

public class Listener_Explosion implements Listener {

	private DeathChest plugin;
	
	
	public Listener_Explosion(DeathChest plugin){
		this.plugin = plugin;
	}
	
	
	@EventHandler(ignoreCancelled = true)
	public void onExplosion(EntityExplodeEvent explode){
		
		List<Block> blockList = new LinkedList<Block>(explode.blockList());
		for(Block block : blockList){			
			if(plugin.getChestContainer().isChestOrSignOfDC(block)){
				explode.blockList().remove(block);
			}
			
			if(block.getType().equals(Material.SIGN_POST) &&
					plugin.interactSpawnContainerController().isSpawnSign(block)){
				explode.blockList().remove(block);
			}
		}
	}
	
}
