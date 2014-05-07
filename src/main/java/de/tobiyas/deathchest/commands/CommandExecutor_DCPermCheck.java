package de.tobiyas.deathchest.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.chestpositions.ChestContainer;
import de.tobiyas.deathchest.chestpositions.ChestPosition;
import de.tobiyas.deathchest.permissions.PermissionNode;

public class CommandExecutor_DCPermCheck implements CommandExecutor{

	private DeathChest plugin;
	
	public CommandExecutor_DCPermCheck(){
		plugin = DeathChest.getPlugin();
		try{
			plugin.getCommand("dcpermcheck").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /dcpermcheck");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("You must be a Player to use this command.");
			return true;
		}
		
		Player player = (Player) sender;
		
		
		
		player.sendMessage(ChatColor.YELLOW + "====DEATH CHEST PERMISSION CHECK====");
		player.sendMessage(ChatColor.BLUE + "PERMISSION                       " + ChatColor.GREEN + "Value");
		
		
		String[] buildPerms = new String[]
				{PermissionNode.createDeathChest, 
				PermissionNode.simpleUse};
		boolean build = plugin.getPermissionManager().hasAnyPermissionSilent(sender, buildPerms);
		player.sendMessage(ChatColor.BLUE + "Build DeathChest:                " + parseBool(build) + build);
		
		boolean saveDC = plugin.getPermissionManager().checkPermissionsSilent(player, PermissionNode.saveToDeathChest) ||
				plugin.getPermissionManager().checkPermissionsSilent(player, PermissionNode.simpleUse);
		player.sendMessage(ChatColor.BLUE + "Save to DeathChest:            " + parseBool(saveDC) + saveDC);
		
		boolean saveSC = plugin.getPermissionManager().checkPermissionsSilent(player, PermissionNode.spawnChest);
		player.sendMessage(ChatColor.BLUE + "Spawn SpawnChest on Death:  " + parseBool(saveSC) + saveSC);
		
		boolean reloadConfig = plugin.getPermissionManager().checkPermissionsSilent(player, PermissionNode.reloadConfig);
		player.sendMessage(ChatColor.BLUE + "Reload Config:                    " + parseBool(reloadConfig) + reloadConfig);
		
		player.sendMessage(ChatColor.YELLOW + "====World-Settings: \"" + player.getWorld().getName() + "\"===");
		
		boolean buildWorld = plugin.getChestContainer().worldSupported(player.getWorld()) && build;
		player.sendMessage(ChatColor.BLUE + "Build DeathChest here:         " + parseBool(buildWorld) + buildWorld);
		
		int limit = plugin.getChestContainer().getMaxTransferLimit(player);
		if(!saveSC) limit = 0;
		player.sendMessage(ChatColor.BLUE + "Transfer Limit:                   " + parseBool(limit != 0) + parseInt(limit));
		
		ChestContainer container = plugin.getChestContainer().getChestOfPlayer(player.getWorld(), player.getName());
		if(container == null)
			return true;
		
		ChestPosition pos = (ChestPosition) container;
		
		Location chestLoctaion = pos.getLocation();
		String x = ChatColor.RED + "" +  chestLoctaion.getBlockX() + ChatColor.BLUE;
		String y = ChatColor.RED + "" +  chestLoctaion.getBlockY() + ChatColor.BLUE;
		String z = ChatColor.RED + "" +  chestLoctaion.getBlockZ() + ChatColor.BLUE;
		
		player.sendMessage(ChatColor.BLUE + "DeathChest for this world:  X:" + x + " Y:" + y + " Z:" + z);
		
		return true;
	}
	
	private ChatColor parseBool(boolean bool){
		if(bool)
			return ChatColor.GREEN;
		else
			return ChatColor.RED;
	}
	
	private String parseInt(int Int){
		if(Int == 0) return "NONE";
		return String.valueOf(Int);
	}
}
