package de.tobiyas.deathchest.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.permissions.PermissionNode;
import de.tobiyas.deathchest.spawncontainer.SpawnSign;

public class CommandExecutor_GYPort implements CommandExecutor {

	private DeathChest plugin;
	
	public CommandExecutor_GYPort(){
		plugin = DeathChest.getPlugin();
		try{
			plugin.getCommand("dcgraveport").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /dcgraveport.");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if(!(sender instanceof Player)){
			sender.sendMessage("You must be a Player to use this command.");
			return true;
		}
		
		Player player = (Player) sender;
		int number = 0;
		String playerName = player.getName();
		
		if(args.length == 2){
			if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.teleportOtherGYS))
				return true;
			playerName = args[1];
		}else{
			if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.teleportOwnGYS))
				return true;
		}
		
		if(args.length > 0){
			try{
				number = Integer.parseInt(args[0]);
			}catch(NumberFormatException exc){
				player.sendMessage(ChatColor.RED + "The number you entered could not be read! Please use a numeric.");
				return true;
			}
		}
		
		SpawnSign grave = plugin.interactSpawnContainerController().getSpawnSignNumberOf(playerName, number);
		if(grave == null){
			player.sendMessage(ChatColor.RED + "The Grave: " + ChatColor.LIGHT_PURPLE + number + ChatColor.RED + ". Of Player: " + ChatColor.LIGHT_PURPLE +
								playerName + ChatColor.RED + " does not exist!");
			return true;
		}
		
		Location graveLocation = grave.getLocation();
		player.teleport(graveLocation);
		player.sendMessage(ChatColor.GREEN + "You have been teleported successfully to Grave: " + ChatColor.RED + number + ChatColor.GREEN +
							" of Player: " + ChatColor.RED + playerName + ChatColor.GREEN + ".");
		
		return true;
	}

}
