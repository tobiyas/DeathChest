package de.tobiyas.deathchest.chestpositions;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface ChestContainer {
	
	public void createChestConfig();
	
	public Location getChestOfPlayer(World world, Player player);
	
	public boolean checkPlayerHasChest(Player player, World world);
	
	public void addChestToPlayer(World world, Player player, Location location);
	
}
