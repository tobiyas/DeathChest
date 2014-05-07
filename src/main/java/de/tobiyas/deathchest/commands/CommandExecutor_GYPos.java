package de.tobiyas.deathchest.commands;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.permissions.PermissionNode;
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
	
	/** 
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		
		Player requester = null;
		String targetPlayer = "";
		boolean foreignViewer = false;
		
		if(!(sender instanceof Player) && args.length != 1){
			sender.sendMessage(ChatColor.RED + "You must be a Player to use this command or specify a Player.");
			return true;
		}else
			if(sender instanceof Player){
				requester = (Player) sender;
				targetPlayer = requester.getName();
			}
		
		if(!plugin.getConfigManager().getSpawnContainerUsage().equals(SpawnSign.class)){
			sender.sendMessage(ChatColor.RED + "GraveSigns are not active.");
			return true;
		}
		
		if(args.length == 1){
			if(!plugin.getPermissionManager().checkPermissionsSilent(sender, PermissionNode.viewOtherGYS)){
				sender.sendMessage(ChatColor.RED + "You don't have the Permissions to view the GraveYards of others!");
				return true;
			}
			
			targetPlayer = args[0];
			foreignViewer = true;
		}
		
		if(foreignViewer)
			sender.sendMessage(ChatColor.YELLOW + "===" + targetPlayer + "'s GRAVESTONES===");
		else
			sender.sendMessage(ChatColor.YELLOW + "===YOUR GRAVESTONES===");
		
		sendGraveYardsOf(sender, targetPlayer, foreignViewer);
		
		return true;
	}
	
	private void sendGraveYardsOf(CommandSender sender, String targetPlayer, boolean foreignViewer){
		Set<SpawnSign> signs = plugin.interactSpawnContainerController().getSpawnSigns(targetPlayer);
		
		if(signs.size() == 0){
			if(foreignViewer)
				sender.sendMessage(ChatColor.RED + targetPlayer + " has no GraveStones!");
			else
				sender.sendMessage(ChatColor.RED + "You have no GraveStones!");
			return;
		}
		
		int i = 0;
		for(SpawnSign sign : signs)
			announceGY(sign, sender, i++);
	}
	
	private void announceGY(SpawnSign sign, CommandSender sender, int number){
		Location loc = sign.getLocation();
		int locX = loc.getBlockX();
		int locY = loc.getBlockY() + 1;
		int locZ = loc.getBlockZ();
		String world = loc.getWorld().getName();
		
		sender.sendMessage(ChatColor.YELLOW + "" + number + ChatColor.GREEN + ":" + 
				" World:" + ChatColor.RED + world + ChatColor.GREEN + 
				" X:" + ChatColor.RED + locX + ChatColor.GREEN + 
				" Y:" + ChatColor.RED + locY + ChatColor.GREEN + 
				" Z:" + ChatColor.RED + locZ + ChatColor.GREEN + 
				" witter in: " + ChatColor.RED + sign.getTimeLeft());
	}
	

}
