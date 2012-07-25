package de.tobiyas.deathchest.commands;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.spawncontainer.SpawnSign;

public class CommandExecutor_GYPos implements CommandExecutor{

	private DeathChest plugin;
	
	public CommandExecutor_GYPos(){
		plugin = DeathChest.getPlugin();
		try{
			plugin.getCommand("dcgravelist").setExecutor(this);
		}catch(Exception e){
			plugin.log("Could not register command: /dcgravelist");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "You must be a Player to use this command.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if(!plugin.getConfigManager().getSpawnContainerUsage().equals(SpawnSign.class)){
			player.sendMessage(ChatColor.RED + "GraveSigns are not active.");
			return true;
		}
		
		Set<SpawnSign> signs = plugin.interactSpawnContainerController().getSpawnSigns(player);
			
		player.sendMessage(ChatColor.YELLOW + "===YOUR GRAVESTONES===");
		
		if(signs.size() == 0)
			player.sendMessage(ChatColor.RED + "You have no GraveStones!");
		
		int i = 0;
		for(SpawnSign sign : signs){
			i++;
			Location loc = sign.getLocation();
			int locX = loc.getBlockX();
			int locY = loc.getBlockY() + 1;
			int locZ = loc.getBlockZ();
			String world = loc.getWorld().getName();
			
			player.sendMessage(ChatColor.YELLOW + "" + i + ChatColor.GREEN + ":" + 
					" World:" + ChatColor.RED + world + ChatColor.GREEN + 
					" X:" + ChatColor.RED + locX + ChatColor.GREEN + 
					" Y:" + ChatColor.RED + locY + ChatColor.GREEN + 
					" Z:" + ChatColor.RED + locZ + ChatColor.GREEN + 
					" witter in: " + ChatColor.RED + sign.getTimeLeft());
		}
		
		return true;
	}
	

}
