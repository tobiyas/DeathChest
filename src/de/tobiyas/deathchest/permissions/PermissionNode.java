package de.tobiyas.deathchest.permissions;

public class PermissionNode {
	
	private final static String pluginPre = "deathchest.";

	public static String createDeathChest = pluginPre + "signchest.create.own";
	public static String otherChestCreate = pluginPre + "signchest.create.other";
	public static String saveToDeathChest = pluginPre + "signchest.saveto";
	public static String simpleUse = pluginPre + "signchest.use";
	
	public static String portToDeathChest = pluginPre + "signchest.port";
	
	public static String spawnChest = pluginPre + "spawnchest.spawn";
	public static String reloadConfig = pluginPre + "commands.reload";
	
	
	public static String[] simpleUseArray = new String[]{simpleUse, saveToDeathChest};
}
