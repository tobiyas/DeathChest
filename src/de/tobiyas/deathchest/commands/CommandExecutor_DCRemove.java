package de.tobiyas.deathchest.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.chestpositions.ChestContainer;
import de.tobiyas.deathchest.chestpositions.ChestPosition;
import de.tobiyas.deathchest.permissions.PermissionNode;

public class CommandExecutor_DCRemove implements CommandExecutor {
	
	private DeathChest plugin;
	
	public CommandExecutor_DCRemove(){
		plugin = DeathChest.getPlugin();
		try{
			plugin.getCommand("dcremove").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /dcremove.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if(!plugin.getPermissionsManager().checkPermissions(sender, PermissionNode.removeChest))
			return true;
		
		World world = null;
		String worldName = "NONE";
		String playerName = "";
		
		if(sender instanceof Player){
			Player player = (Player) sender;
			if(args.length == 0){
				world = player.getWorld();
				playerName = player.getName();
			}else if(args.length == 1){
				worldName = args[0];
				world = Bukkit.getWorld(worldName);
				playerName = player.getName();
			}else if(args.length == 2){
				worldName = args[0];
				world = Bukkit.getWorld(worldName);
				playerName = args[1];
			}else{
				sender.sendMessage(ChatColor.RED + "Wrong usage! Use: " + ChatColor.YELLOW + "/dcremove [WorldName] [PlayerName]");
				return true;
			}
		}else{
			if(args.length != 2){
				sender.sendMessage(ChatColor.RED + "Wrong usage! Use: " + ChatColor.YELLOW + "/dcremove <WorldName> <PlayerName>");
				return true;
			}
			
			worldName = args[0];
			world = Bukkit.getWorld(worldName);
			playerName = args[1];
		}
		
		if(world == null){
			sender.sendMessage(ChatColor.RED + "The World " + ChatColor.LIGHT_PURPLE + worldName + ChatColor.RED + 
								ChatColor.LIGHT_PURPLE + " was not found.");
			return true;
		}
		
		ChestContainer container = plugin.getChestContainer().getChestOfPlayer(world, playerName);
		if(container == null){
			sender.sendMessage(ChatColor.RED + "There is no DeathChest for: " + ChatColor.LIGHT_PURPLE +
								playerName + ChatColor.RED + " on World: " + ChatColor.LIGHT_PURPLE + world.getName());
			return true;
		}
		
		ChestPosition position = (ChestPosition) container;
		position.destroySelf(true, true);
		plugin.getChestContainer().removeFromPosition(position.getLocation());
		
		sender.sendMessage(ChatColor.GREEN + "Successfully destroyed the Chest of: " + ChatColor.LIGHT_PURPLE + 
							playerName + ChatColor.GREEN + " from World: " + ChatColor.LIGHT_PURPLE + world.getName());
		return true;
	}

}
