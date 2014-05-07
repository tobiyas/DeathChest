package de.tobiyas.deathchest.listeners.events;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * This event is triggers 1 Tick after a player dies.
 * 
 * @author tobiyas
 *
 */
public class AfterPlayerDeathEvent extends Event {

	private static HandlerList handlers = new HandlerList();
	
	/**
	 * The player that died
	 */
	private final Player player;
	
	/**
	 * The location the player died
	 */
	private final Location location;
	
	/**
	 * The items that the player drops
	 */
	private final  List<ItemStack> items;
	
	/**
	 * The exp the player drops
	 */
	private final int exp;
	
	
	/**
	 * Constructs a death event with the used parameters
	 * 
	 * @param player
	 * @param location
	 * @param items
	 * @param exp
	 */
	public AfterPlayerDeathEvent(Player player, Location location, List<ItemStack> items, int exp) {
		this.player = player;
		this.location = location;
		this.items = items;
		this.exp = exp;
	}


	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }


	public Player getPlayer() {
		return player;
	}


	public Location getLocation() {
		return location;
	}


	public List<ItemStack> getItems() {
		return items;
	}


	public int getExp() {
		return exp;
	}

}
