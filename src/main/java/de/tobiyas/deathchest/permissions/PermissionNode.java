package de.tobiyas.deathchest.permissions;

public class PermissionNode {
	
	private final static String pluginPre = "deathchest.";

	public static final String createDeathChest = pluginPre + "signchest.create.own";
	public static final String otherChestCreate = pluginPre + "signchest.create.other";
	public static final String saveToDeathChest = pluginPre + "signchest.saveto";
	public static final String simpleUse = pluginPre + "signchest.use";
	
	public static final String portToDeathChest = pluginPre + "signchest.port";
	
	public static final String spawnChest = pluginPre + "spawnchest.spawn";
	public static final String reloadConfig = pluginPre + "commands.reload";
	public static final String removeChest = pluginPre + "command.remove";
	
	public static final String viewOtherGYS = pluginPre + "gy.view.other";
	public static final String teleportOwnGYS = pluginPre + "gy.port.own";
	public static final String teleportOtherGYS = pluginPre + "gy.port.other";
	
	
	public static final String[] anyGYPort = new String[]{teleportOwnGYS, teleportOtherGYS};
	public static final String[] simpleUseArray = new String[]{simpleUse, saveToDeathChest};
	
	public static final String[] anySavingFeatures = new String[]{simpleUse, saveToDeathChest, spawnChest};
}
