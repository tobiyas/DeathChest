package de.tobiyas.deathchest.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.deathchest.util.Const;

public class CommandExecutor_DCVersion implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		sender.sendMessage(ChatColor.GREEN + "Current DeathChest version: " + Const.currentVersion + "_" + Const.currentBuildVersion);		
		
		return true;
	}

}
