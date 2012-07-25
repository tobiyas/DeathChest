package de.tobiyas.deathchest.chestpositions;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface ChestContainer {
	
	/**
	 * Gets the ChestPosition of a given Player by a given World
	 * null, if none found
	 * 
	 * @param world to search for
	 * @param player to search for
	 * @return the ChestPosition of the Player on the World
	 */
	public ChestContainer getChestOfPlayer(World world, Player player);
	
	/**
	 * Gets the ChestPosition of a given Player by a given World
	 * null, if none found
	 * 
	 * @param world to search for
	 * @param player to search for
	 * @return the ChestPosition of the Player on the World
	 */
	public ChestContainer getChestOfPlayer(World world, String player);
	
	
	/**
	 * Checks if a Player has a DeathChest in the World: world
	 * 
	 * @param world to search for
	 * @param player to search for
	 * @return if the Player has a Chest in World
	 */
	public boolean checkPlayerHasChest(World world, Player player);
	
	/**
	 * Checks if a Player has a DeathChest in the World: world
	 * 
	 * @param world to search for
	 * @param player to search for
	 * @return if the Player has a Chest in World
	 */
	public boolean checkPlayerHasChest(World world, String player);
	
	
	/**
	 * Adds a DeathChest to a Player at a specific Location
	 * 
	 * @param location of the Chest
	 * @param player the player
	 * @return if it worked
	 */
	public boolean addChestToPlayer(Location location, Player player);
	
	/**
	 * Adds a DeathChest to a Player at a specific Location
	 * 
	 * @param location of the Chest
	 * @param player the player
	 * @return if it worked
	 */
	public boolean addChestToPlayer(Location location, String player);
	
	/**
	 * checks if the package has world included
	 * 
	 * @param world the world to check
	 * @return if it is included
	 */
	public boolean hasWorld(World world);
	
	
	/**
	 * removes a ChestPosition off the list for a current Location
	 * 
	 * @param location the Location of the Chest to search for
	 * @return the ChestContainer removed
	 */
	public ChestContainer removeFromPosition(Location location);

	
	/**
	 * checks if the given world is in any list of a ChestPackage
	 * 
	 * @param world the world to check
	 * @return if it is included
	 */
	public boolean worldSupported(World world);
	
	/**
	 * gets the maximum transfer limit for a Player on his current world
	 * 0 if none should be transferred
	 * 
	 * @param player to check
	 * @return the maximum amount of to transfer items
	 */
	public int getMaxTransferLimit(Player player);	
}
