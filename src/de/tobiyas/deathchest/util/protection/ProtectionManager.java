package de.tobiyas.deathchest.util.protection;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.util.protection.protectionplugins.Protector;
import de.tobiyas.deathchest.util.protection.protectionplugins.ProtectorLWC;
import de.tobiyas.deathchest.util.protection.protectionplugins.ProtectorLockette;
import de.tobiyas.deathchest.util.protection.protectionplugins.ProtectorSecureChests;

public class ProtectionManager {

	private Protector protector;
	private DeathChest plugin;
	
	public ProtectionManager(){
		plugin = DeathChest.getPlugin();
		reloadManager();
	}
	
	public void reloadManager(){
		if(plugin.getConfigManager().getUseSecureChestInsteadOfLWC()){
			protector = new ProtectorSecureChests();
			return;
		}
		
		if(plugin.getConfigManager().getUseLocketteInsteadOfLWC()){
			protector = new ProtectorLockette();
			return;
		}
		
		protector = new ProtectorLWC();
	}
	
	public boolean protectChest(Location location, String owner){
		if(!plugin.getConfigManager().checkSpawnChestLWC()) return true;
		return protector.protectChest(location, owner);
	}
	
	public boolean protectSign(Location location, String owner){
		if(!plugin.getConfigManager().getLWCSignProtect()) return true;
		return protector.protectSign(location, owner);
	}
	
	public boolean checkProtection(Location location, Player player){
		if(!plugin.getConfigManager().checkDeathChestWithLWC()) return true;
		return protector.checkProtection(location, player);
	}
	
	public boolean unprotectChest(Location location){
		return protector.unprotectChest(location);
	}
	
	public boolean unprotectSign(Location location){
		return protector.unprotectSign(location);
	}
}
