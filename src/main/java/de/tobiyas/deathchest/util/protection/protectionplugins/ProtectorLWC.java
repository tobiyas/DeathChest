package de.tobiyas.deathchest.util.protection.protectionplugins;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;

import de.tobiyas.deathchest.DeathChest;

public class ProtectorLWC implements Protector {
	
	private DeathChest plugin;
	
	public ProtectorLWC(){
		plugin = DeathChest.getPlugin();
	}

	@Override
	public boolean protectChest(Location location, String owner) {
		return protectBlock(location, owner);
	}
	
	private boolean protectBlock(Location location, String owner){
		try{
			LWCPlugin LWC = (LWCPlugin) Bukkit.getPluginManager().getPlugin("LWC");
			if(LWC == null) throw new Exception();
			
			String world = location.getWorld().getName();
			int blockID = location.getBlock().getTypeId();
			int x = location.getBlockX();
			int y = location.getBlockY();
			int z = location.getBlockZ();
			
			Protection protection = LWC.getLWC().getPhysicalDatabase().registerProtection(blockID, Protection.Type.PRIVATE, world, owner, "", x, y, z);
			protection.save();
			return true;
		}catch(Exception e){
			DeathChest.getPlugin().log("LWC not Found. Disable LWC Config options!");
			DeathChest.getPlugin().getConfigManager().tempTurnOffLWC();
			return false;
		}
	}

	@Override
	public boolean protectSign(Location location, String owner) {
		return protectBlock(location, owner);
	}

	@Override
	public boolean checkProtection(Location location, Player player) {		
		try{
			LWCPlugin LWC = (LWCPlugin) Bukkit.getPluginManager().getPlugin("LWC");
			if(LWC == null) throw new Exception();
			
			return LWC.getLWC().canAccessProtection(player, location.getBlock());
			
		}catch(Exception e){
			plugin.log("LWC not Found. Disable LWC Config options for DeathChests. It will be Disabled for now!");
			plugin.getConfigManager().tempTurnOffLWC();
			return true;
		}
	}

	@Override
	public boolean unprotectChest(Location location) {
		try{
			Protection protection = LWC.getInstance().findProtection(location.getBlock());
			if(protection == null) return false;
			protection.remove();
			return true;
		}catch(NoClassDefFoundError e){
			return false;
		}
	}

	@Override
	public boolean unprotectSign(Location location) {
		return unprotectChest(location);
	}

}
