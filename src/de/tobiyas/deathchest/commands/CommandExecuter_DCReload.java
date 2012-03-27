package de.tobiyas.deathchest.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.permissions.PermissionNode;



public class CommandExecuter_DCReload implements CommandExecutor{

	private DeathChest plugin;

	public CommandExecuter_DCReload(DeathChest plugin){
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if (command.getName().equalsIgnoreCase("dcreload")) {
			if(sender instanceof Player){
				if(!plugin.getPermissionsManager().CheckPermissions((Player)sender, PermissionNode.reloadConfig)) return true;
			}else
			 if(!plugin.getPermissionsManager().CheckPermissions(sender, PermissionNode.reloadConfig)) return true;
			
			plugin.getConfigManager().reloadConfig();
			plugin.reloadChestContainer();
			sender.sendMessage(ChatColor.GREEN + "Reloading Config done");
			
			return true;
		}
		return false;
	}
}
