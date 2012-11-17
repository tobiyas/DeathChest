package de.tobiyas.deathchest.util;

import java.io.File;

import de.tobiyas.deathchest.DeathChest;

public class Const {

	public final static String updateURL = "";
	public final static int leastBuild = 1987;
	
	public static double currentVersion;
	public static int currentBuildVersion;
	public static final String updaterURL = "http://0x002a.de/Minecraft/Plugins/DeathChest/";
	
	public static String spawnChestFile = DeathChest.getPlugin().getDataFolder() + File.separator + "spawnChestPos.yml";
	
	public static boolean oldBukkitVersion = false;
}
