package de.tobiyas.deathchest.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.permissions.PermissionNode;



public class CommandExecutor_DCReload implements CommandExecutor{

	private DeathChest plugin;

	public CommandExecutor_DCReload(){
		plugin = DeathChest.getPlugin();
		try{
			plugin.getCommand("dcreload").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /dcreload.");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if (command.getName().equalsIgnoreCase("dcreload")) {
			if(sender instanceof Player){
				if(!plugin.getPermissionsManager().checkPermissions(sender, PermissionNode.reloadConfig)) return true;
			}else
			 if(!plugin.getPermissionsManager().checkPermissions(sender, PermissionNode.reloadConfig)) return true;
			
			plugin.getConfigManager().reloadConfig();
			plugin.reloadChestContainer();
			sender.sendMessage(ChatColor.GREEN + "Reloading Config done");
			
			return true;
		}
		return false;
	}
}
