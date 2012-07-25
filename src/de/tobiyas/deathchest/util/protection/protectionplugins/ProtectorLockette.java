package de.tobiyas.deathchest.util.protection.protectionplugins;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.yi.acru.bukkit.Lockette.Lockette;

import com.griefcraft.lwc.LWCPlugin;
import de.tobiyas.deathchest.DeathChest;

public class ProtectorLockette implements Protector{
	
	private DeathChest plugin;
	
	public ProtectorLockette(){
		plugin = DeathChest.getPlugin();
	}

	@Override
	public boolean protectChest(Location location, String owner) {
		// TODO Maybe autoAdd to lockette. Very crapy to implement...
		return true;
	}

	@Override
	public boolean protectSign(Location location, String owner) {
		//Not possible with Lockette
		return true;
	}

	@Override
	public boolean checkProtection(Location location, Player player) {
		try{
			Plugin lockette = (LWCPlugin) Bukkit.getPluginManager().getPlugin("Lockette");
			if(lockette == null || !lockette.isEnabled()) throw new Exception();
			
			boolean isUser = Lockette.isUser(location.getBlock(), player.getName(), true);
			return isUser;
		}catch(Exception e){
			plugin.log("Lockette not Found. Disable LWC Config options for DeathChests. It will be Disabled to the next restart!");
			plugin.getConfigManager().tempTurnOffLWC();
			return true;
		}

	}

	@Override
	public boolean unprotectChest(Location location) {
		//Not that easy possible with lockette
		return true;
	}

	@Override
	public boolean unprotectSign(Location location) {
		//Not possible with lockette
		return true;
	}

}
