package de.tobiyas.deathchest.chestpositions;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface ChestContainer {
	
	public ChestContainer getChestOfPlayer(World world, Player player);
	
	public boolean checkPlayerHasChest(World world, Player player);
	
	public boolean addChestToPlayer(Location location, Player player);
	
	public boolean hasWorld(World world);
	
	public ChestContainer removeFromPosition(Location location);

	public boolean worldSupported(World world);
	
}
