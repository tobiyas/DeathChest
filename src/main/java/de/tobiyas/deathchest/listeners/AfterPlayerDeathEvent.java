package de.tobiyas.deathchest.listeners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class AfterPlayerDeathEvent extends Event {

	private static HandlerList handlers = new HandlerList();
	
	private final Player player;
	private final Location location;
	private final  List<ItemStack> items;
	private final int exp;
	
	
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
