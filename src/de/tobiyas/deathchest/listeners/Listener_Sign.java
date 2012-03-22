package de.tobiyas.deathchest.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

import de.tobiyas.deathchest.DeathChest;

public class Listener_Sign implements Listener {

	private DeathChest plugin;
	
	public Listener_Sign(DeathChest plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event){
		Player player = event.getPlayer();
		String[] lines = event.getLines();
		Block signPosition = event.getBlock();
		World world = player.getWorld();
		
		if(!(lines[1].toLowerCase().equals("[deathchest]"))) return;
		if(!plugin.getPermissionsManager().CheckPermissions(player, "createdeathchest")) return;
		
		Location location = new Location(world, signPosition.getX(), signPosition.getY() - 1, signPosition.getZ());
		
		if(!location.getBlock().getType().equals(Material.CHEST)){
			player.sendMessage(ChatColor.RED + "No chest found underneath sign.");
			return;
		}
		
		plugin.getChestContainer().addChestToPlayer(world, player, location);
		
		event.setLine(0, player.getName());
		event.setLine(1, "[DeathChest]");
		event.setLine(2, world.getName());
		event.setLine(3, "");
		
		player.sendMessage(ChatColor.GREEN + "DeathChest created for world: " + world.getName());
	}
}
