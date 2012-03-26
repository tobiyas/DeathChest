package de.tobiyas.deathchest.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

import com.griefcraft.lwc.LWCPlugin;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.chestpositions.ChestContainer;
import de.tobiyas.deathchest.chestpositions.ChestPosition;
import de.tobiyas.deathchest.permissions.PermissionNode;

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
		if(!plugin.getPermissionsManager().CheckPermissionsSilent(player, PermissionNode.createDeathChest)){
			player.sendMessage(ChatColor.RED + "You don't have Permissions to set a DeathChest.");
			return;
		}
		
		Location location = new Location(world, signPosition.getX(), signPosition.getY() - 1, signPosition.getZ());
		
		if(!checkLWC(location, player)){
			player.sendMessage(ChatColor.RED + "You don't have LWC access to the Chest below.");
			return;
		}
		
		if(!location.getBlock().getType().equals(Material.CHEST)){
			player.sendMessage(ChatColor.RED + "No chest found underneath sign.");
			return;
		}
		
		plugin.getChestContainer().addChestToPlayer(location, player);
		
		event.setLine(0, player.getName());
		event.setLine(1, "[DeathChest]");
		event.setLine(2, world.getName());
		event.setLine(3, "");
		
		player.sendMessage(ChatColor.GREEN + "DeathChest created for world: " + world.getName());
	}
	
	private boolean checkLWC(Location location, Player player){
		if(!plugin.getConfigManager().checkDeathChestWithLWC()) return true;
		
		try{
			LWCPlugin LWC = (LWCPlugin) Bukkit.getPluginManager().getPlugin("LWC");
			return LWC.getLWC().canAccessProtection(player, location.getBlock());
			
		}catch(Exception e){
			plugin.log("LWC not Found. Disable LWC Config options for DeathChests!");
			return true;
		}
	}
	
	
	@EventHandler
	public void signBreak(BlockBreakEvent event){
		if(!(event.getBlock().getType() == Material.WALL_SIGN)) return;
		
		Location location = event.getBlock().getLocation();
		location.setY(location.getY() - 1);
		
		ChestContainer container = plugin.getChestContainer().removeFromPosition(location);
		if(container != null){
			ChestPosition position = (ChestPosition) container;
			Player player = Bukkit.getPlayer(position.getPlayerName());
			if(player != null) player.sendMessage(ChatColor.RED + "Your DeathChest on World: " 
								+ event.getBlock().getWorld().getName() 
								+ " has been destroyed, by: " 
								+ event.getPlayer().getName());
		}
	}
}
