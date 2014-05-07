package de.tobiyas.deathchest.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.permissions.PermissionNode;

public class CommandExecutor_DCHelp implements CommandExecutor {
	
	private DeathChest plugin;
	
	public CommandExecutor_DCHelp(){
		plugin = DeathChest.getPlugin();
		try{
			plugin.getCommand("dchelp").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /dchelp.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		
		sender.sendMessage(ChatColor.YELLOW + "======Help: DeathChest======");
		sender.sendMessage(ChatColor.BLUE + "Commands:");
		sender.sendMessage(ChatColor.RED + "/dcversion" + ChatColor.YELLOW + " Displays the Version of DC.");
		sender.sendMessage(ChatColor.RED + "/dcpermcheck" + ChatColor.YELLOW + " checks which Permissions you got.");
		if(plugin.getPermissionManager().checkPermissionsSilent(sender, PermissionNode.reloadConfig))
			sender.sendMessage(ChatColor.RED + "/dcreload" + ChatColor.YELLOW + " Reloads the Config of DC.");
		if(plugin.getPermissionManager().checkPermissionsSilent(sender, PermissionNode.portToDeathChest))
			sender.sendMessage(ChatColor.RED + "/dcport" + ChatColor.YELLOW + " teleports you to your DeathChest.");
		if(plugin.getPermissionManager().checkPermissionsSilent(sender, PermissionNode.removeChest))
			sender.sendMessage(ChatColor.RED + "/dcremove [WorldName] [PlayerName] " + ChatColor.YELLOW + "removes DeathChest of Player from World.");
		sender.sendMessage(ChatColor.RED + "/dcgravelist [PlayerName]" + ChatColor.YELLOW + " lists the Position of all your/PlayerName's GraveYard signs.");
		if(plugin.getPermissionManager().hasAnyPermissions(sender, PermissionNode.anyGYPort))
			sender.sendMessage(ChatColor.RED + "/dcgraveport [Number] [Playername] " + ChatColor.YELLOW + "teleports you to the grave of the player with the given number.");
		return true;
	}

}
