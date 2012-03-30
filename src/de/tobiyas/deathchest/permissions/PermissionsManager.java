package de.tobiyas.deathchest.permissions;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import de.tobiyas.deathchest.DeathChest;


public class PermissionsManager {
	
	private final String pluginNamePerm = "deathchest.";

	private static Permission permission = null;
	private DeathChest plugin;
	
	public PermissionsManager(){
		this.plugin = DeathChest.getPlugin();
		setupPermissions();
	}
	
	private Boolean setupPermissions(){
		try{
			RegisteredServiceProvider<Permission> permissionProvider = this.plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
			if (permissionProvider != null)
				permission = permissionProvider.getProvider();
			
			}catch(Exception e){}
		
		return (permission != null);
	}
	
	public boolean permissionSystemFound(){
		return permission != null;
	}
	
	public boolean CheckPermissions(Player player, String permissionNode){
		if(permissionNode == null) return true;
		if(!permissionSystemFound()) return true;
		boolean hasPerms = permission.has(player, pluginNamePerm + permissionNode);
		if(!hasPerms) player.sendMessage(ChatColor.RED + "You don't have Permissions.");
		return hasPerms;
	}
	
	public boolean CheckPermissionsSilent(Player player, String permissionNode){
		if(permissionNode == null) return true;
		if(!permissionSystemFound()) return true;
		return permission.has(player, pluginNamePerm + permissionNode);
	}
	
	public boolean CheckPermissions(CommandSender sender, String permissionNode){
		if(sender instanceof Player) return CheckPermissions((Player)sender, permissionNode);
		return permission.has(sender, permissionNode);
	}
	
	public boolean CheckPermissions(World world, OfflinePlayer offPlayer, String permissionNode) {
		return permission.has(world, offPlayer.getName(), permissionNode);
	}
	

	public String[] getAllPermissionGroups(){
		if(!permissionSystemFound()) return null;
		return permission.getGroups();
	}
	
	public String getGroupOfPlayer(Player player){
		return permission.getPrimaryGroup(player);
	}
	  
}
