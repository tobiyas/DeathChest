package de.tobiyas.deathchest.util.protection.protectionplugins;

import java.io.FileNotFoundException;
import java.util.Map;

import me.HAklowner.SecureChests.Lock;
import me.HAklowner.SecureChests.SecureChests;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import de.tobiyas.deathchest.DeathChest;

public class ProtectorSecureChest implements Protector {

	@Override
	public boolean protectChest(Location location, String owner) {
		try{
			Plugin chestPlugin = Bukkit.getPluginManager().getPlugin("SecureChests");
			if(chestPlugin == null)
				throw new Exception();
			
			SecureChests cplugin = (SecureChests) chestPlugin;
			
			Lock lock = new Lock(location);
			lock.setOwner(owner);
			lock.setPublic(false);
			
			cplugin.getLockManager().newLock(lock);
			return true;
			
		}catch(Exception e){
			DeathChest.getPlugin().log("Secure-Chests not Found. Disable LWC Config options!");
			DeathChest.getPlugin().getConfigManager().tempTurnOffLWC();
			return false;
		}
	}

	@Override
	public boolean protectSign(Location loc, String owner) {
		//Not possible with this plugin
		return true;
	}

	@Override
	public boolean checkProtection(Location location, Player player) {
		try{
			Plugin chestPlugin = Bukkit.getPluginManager().getPlugin("SecureChests");
			if(chestPlugin == null)
				throw new FileNotFoundException();
			
			SecureChests cplugin = (SecureChests) chestPlugin;
			Lock lock = cplugin.getLockManager().getLock(location);
			if(lock == null)
				return true;
			
			if(lock.isPublic())
				return true;
			
			if(lock.getOwner().equalsIgnoreCase(player.getName()))
				return true;
			
			Map<String, Boolean> accessList = lock.getPlayerAccessList();
			if(!accessList.containsKey(player.getName()))
				return false;
			
			boolean access = accessList.get(player.getName());
			return access;
		}catch(FileNotFoundException e){
			DeathChest.getPlugin().log("Secure-Chests not Found. Disable LWC Config options!");
			DeathChest.getPlugin().getConfigManager().tempTurnOffLWC();
			return false;
		}catch(NullPointerException e){
			player.sendMessage(ChatColor.RED + "The chest is NOT protected. Protect it before.");
			return false;
		}
	}

	@Override
	public boolean unprotectChest(Location location) {
		try{
			Plugin chestPlugin = Bukkit.getPluginManager().getPlugin("SecureChests");
			if(chestPlugin == null)
				throw new Exception();
			
			SecureChests cplugin = (SecureChests) chestPlugin;
			Lock lock = cplugin.getLockManager().getLock(location);
			if(lock == null)
				return true;
			lock.unlock();
			return true;
			
		}catch(Exception e){
			DeathChest.getPlugin().log("Secure-Chests not Found. Disable LWC Config options!");
			DeathChest.getPlugin().getConfigManager().tempTurnOffLWC();
			return false;
		}
	}

	@Override
	public boolean unprotectSign(Location loc) {
		//Not possible with this plugin
		return true;
	}

}
