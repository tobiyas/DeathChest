/*
 * http://
 *
 * powered by Kickstarter
 * DeathChest - by Tobiyas
 */

package de.tobiyas.deathchest.listeners;


import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.chesttransferring.SaveToContainer;
import de.tobiyas.deathchest.util.PlayerDropModificator;


/**
 * @author tobiyas
 *
 */
public class Listener_Entity  implements Listener{
	private DeathChest plugin;

	public Listener_Entity(DeathChest plugin){
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		if(plugin.isBattleNight(event.getEntity()))
			return;
		
		int originalDrops = event.getDrops().size();
		
		if(event.getDrops().isEmpty() && event.getDroppedExp() == 0) return;
		Player player = (Player) event.getEntity();
		
		int exp = player.getTotalExperience();
		boolean filledDC = false;
		
		PlayerDropModificator piMod = new PlayerDropModificator(player, event.getDrops(), exp);
		
		List<ItemStack> notRemoved = SaveToContainer.saveToDeathChest(piMod);
		if(notRemoved.isEmpty()) filledDC = true;
	
		piMod.reAddToDrop(notRemoved);
		piMod.removeItemsTransferred();
		
		if(!piMod.stilHasItems() && (piMod.getEXP() == 0 || plugin.getConfigManager().getEXPMulti() == 0)){
			//event.getDrops().clear();
			//event.getDrops().addAll(piMod.getALLItemsAsList());
			event.setDroppedExp(piMod.getEXP());
			return;
		}
		
		if(piMod.stilHasItems() || piMod.stilHasEXP()){
			List<ItemStack> notDropped = plugin.interactSpawnContainerController().createSpawnContainer(piMod);
			piMod.reAddToDrop(notDropped);
			piMod.removeItemsTransferred();
		}
		
		//event.getDrops().clear();
		//event.getDrops().addAll(piMod.getALLItemsAsList());
		
		
		
		if(exp != player.getTotalExperience()){
			event.setDroppedExp(0); //To secure EXP is storred
		}
			
		if(event.getDrops().size() > 0 && filledDC){
			int maxTransfer = plugin.getChestContainer().getMaxTransferLimit(player);
			if(originalDrops > maxTransfer)
				player.sendMessage(ChatColor.RED + "Only " + maxTransfer + " items could be transfered. The rest is dropped at your death location.");
			else
				player.sendMessage(ChatColor.RED + "Your total inventory did not fit in the box. The rest items were dropped at your death location.");
		}
	}
	
}
