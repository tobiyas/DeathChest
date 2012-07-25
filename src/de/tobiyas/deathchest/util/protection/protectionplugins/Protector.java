package de.tobiyas.deathchest.util.protection.protectionplugins;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Protector {
	public boolean protectChest(Location location, String owner);
	
	public boolean protectSign(Location location, String owner);
	
	public boolean checkProtection(Location location, Player player);
	
	public boolean unprotectChest(Location location);
	
	public boolean unprotectSign(Location location);
}
