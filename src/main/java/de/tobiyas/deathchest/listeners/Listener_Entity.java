/*
 * http://
 *
 * powered by Kickstarter
 * DeathChest - by Tobiyas
 */

package de.tobiyas.deathchest.listeners;


import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.listeners.events.AfterPlayerDeathEvent;


/**
 * @author tobiyas
 */
public class Listener_Entity  implements Listener{
	private DeathChest plugin;

	public Listener_Entity(DeathChest plugin){
		this.plugin = plugin;
	}
	

	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event){
		if(plugin.isBattleNight(event.getEntity())){
			return;
		}
		
		if(event.getDrops().isEmpty() && event.getDroppedExp() == 0) return;
		Player player = (Player) event.getEntity();
		
		boolean useOwnExpCalculation = plugin.getConfigManager().getEXPMulti() > 0;
		
		int expToUse = useOwnExpCalculation ? player.getTotalExperience() : event.getDroppedExp();
		
		//clone drop table
		List<ItemStack> items = new LinkedList<ItemStack>();
		for(ItemStack item : event.getDrops()){
			items.add(item.clone());
		}
		
		AfterDeathEventFire task = new AfterDeathEventFire(player, player.getLocation(), items, expToUse);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, task, 2);
		
		event.setDroppedExp(0);
		event.getDrops().clear();
	}
	
	private class AfterDeathEventFire implements Runnable{

		private final Player player;
		private final Location location;
		private final List<ItemStack> items;
		private final int exp;
		
		
		private AfterDeathEventFire(Player player, Location location, List<ItemStack> items, int exp){
			this.player = player;
			this.location = location;
			this.items = items;
			this.exp = exp;
		}
		
		
		@Override
		public void run() {
			Event event = new AfterPlayerDeathEvent(player, location, items, exp);
			Bukkit.getPluginManager().callEvent(event);
		}
		
	}
	
}
