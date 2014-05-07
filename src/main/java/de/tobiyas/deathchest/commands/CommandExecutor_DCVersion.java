package de.tobiyas.deathchest.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.util.Const;

public class CommandExecutor_DCVersion implements CommandExecutor{
	
	private DeathChest plugin;
	
	public CommandExecutor_DCVersion(){
		plugin = DeathChest.getPlugin();
		try{
			plugin.getCommand("dcversion").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /dcversion.");
		}
		
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		
		
		sender.sendMessage(ChatColor.YELLOW + "Current DeathChest version: " + Const.currentVersion + "_" + Const.currentBuildVersion 
				+ " Rev: " + Const.currentRevOfBuild);
		sender.sendMessage(ChatColor.YELLOW + "Working with Permission-System: " + plugin.getPermissionManager().getPermissionsName());
		
		return true;
	}

}
