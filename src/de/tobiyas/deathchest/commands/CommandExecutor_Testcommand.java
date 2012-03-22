/*
 * DeathChest - by Tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.deathchest.commands;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.deathchest.DeathChest;



public class CommandExecutor_Testcommand implements CommandExecutor {
	
	@SuppressWarnings("unused")
	private DeathChest plugin;

	public CommandExecutor_Testcommand(DeathChest plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {
		if (command.getName().equalsIgnoreCase("killself")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Y U NO PLAYER??!111");
				return true;
			}

			Player player = (Player) sender;
			
			player.setHealth(0);
			player.sendMessage(ChatColor.RED + "You killed yourself. Cool.");
			
			return true;
		}
		return false;
	}
}
