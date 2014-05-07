package de.tobiyas.deathchest.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.chestpositions.ChestContainer;
import de.tobiyas.deathchest.chestpositions.ChestPosition;
import de.tobiyas.deathchest.permissions.PermissionNode;

public class CommandExecutor_DCPort implements CommandExecutor {
	
	private DeathChest plugin;
	
	public CommandExecutor_DCPort(){
		plugin = DeathChest.getPlugin();
		try{
			plugin.getCommand("dcport").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /dcport");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		
		if(!(sender instanceof Player)){
			sender.sendMessage("You must be a Player to use this command.");
			return true;
		}
		
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.portToDeathChest))
			return true;
		
		Player player = (Player) sender;
		ChestContainer container = plugin.getChestContainer().getChestOfPlayer(player.getWorld(), player.getName());
		
		if(container == null){
			player.sendMessage(ChatColor.RED + "You don't have a DeathChest for this World.");
			return true;
		}
		
		ChestPosition position = (ChestPosition) container;
		Location loc = position.getSignLocation();
		Location toPortTo = getNextFree(loc);
		
		if(toPortTo != null){
			player.teleport(toPortTo);
			player.sendMessage(ChatColor.GREEN + "You have been portet to your DeathChest");
		}else
			player.sendMessage(ChatColor.RED + "Could not find a free location around your DeathChest.");
		
		return true;
	}
	
	private Location getNextFree(Location location){
		Location newLocation;
		
		for(int i = -2; i <= 2; i++){
			for(int j = -2; j <= 2; j++){
				for(int k = -1; k <= 2; k++){
					newLocation = location.clone();
					newLocation = newLocation.add(j, i, k);
					
					if(newLocation.getBlock().getType() == Material.AIR &&
						newLocation.getBlock().getRelative(BlockFace.UP).getType() == Material.AIR){
						return newLocation.add(0.5, 0, 0.5);
					}
				}
			}
		}

		return null;
	}

}
