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
			plugin.getCommand("dc").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /dc.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		
		sender.sendMessage(ChatColor.YELLOW + "======Help: DeathChest======");
		sender.sendMessage(ChatColor.BLUE + "Commands:");
		sender.sendMessage(ChatColor.RED + "/dcversion" + ChatColor.YELLOW + " Displays the Version of DC.");
		sender.sendMessage(ChatColor.RED + "/dcpermcheck" + ChatColor.YELLOW + " checks which Permissions you got.");
		if(plugin.getPermissionsManager().checkPermissionsSilent(sender, PermissionNode.reloadConfig))
			sender.sendMessage(ChatColor.RED + "/dcreload" + ChatColor.YELLOW + " Reloads the Config of DC.");
		if(plugin.getPermissionsManager().checkPermissionsSilent(sender, PermissionNode.portToDeathChest))
			sender.sendMessage(ChatColor.RED + "/dcport" + ChatColor.YELLOW + " teleports you to your DeathChest.");
		sender.sendMessage(ChatColor.RED + "/dcgravelist" + ChatColor.YELLOW + " lists the Position of all your GraveYard signs.");
		return true;
	}

}
