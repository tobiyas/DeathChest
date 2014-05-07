package de.tobiyas.deathchest.listeners;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.chesttransferring.SaveToContainer;
import de.tobiyas.deathchest.listeners.events.AfterPlayerDeathEvent;
import de.tobiyas.deathchest.permissions.PermissionNode;
import de.tobiyas.deathchest.util.PlayerDropModificator;

public class Listener_AfterPlayerDeath implements Listener{

	private DeathChest plugin;
	
	
	public Listener_AfterPlayerDeath(DeathChest plugin) {
		this.plugin = plugin;
	}

	
	@EventHandler
	public void afterPlayerDeath(AfterPlayerDeathEvent event){
		Player player = event.getPlayer();
		List<ItemStack> items = event.getItems();
		int expToUse = event.getExp();
		Location dropLocation = event.getLocation();
		
		int originalDrops = items.size();
		
		boolean filledDC = false;
		
		boolean hasAnyPermissionForDC = checkPermissionForSave(player);
		
		PlayerDropModificator piMod = new PlayerDropModificator(player, items, expToUse);
		
		//Only check the dropping stuff when the player has Permission for
		if(hasAnyPermissionForDC){
			List<ItemStack> notRemoved = SaveToContainer.saveToDeathChest(piMod);
			if(notRemoved.isEmpty()) filledDC = true;
		
			piMod.reAddToDrop(notRemoved);
			piMod.removeItemsTransferred();
			
			if(!piMod.stilHasItems() && (piMod.getEXP() == 0 || plugin.getConfigManager().getEXPMulti() == 0)){
				expToUse = piMod.getEXP();
				return;
			}
			
			if(piMod.stilHasItems() || piMod.stilHasEXP()){
				List<ItemStack> notDropped = plugin.interactSpawnContainerController().createSpawnContainer(piMod);
				piMod.reAddToDrop(notDropped);
				piMod.removeItemsTransferred();
			}
		}
		

		List<ItemStack> itemsStillLeft = piMod.getALLItemsAsList();
		//We only need to inform the player when we actually moved something
		if(itemsStillLeft.size() > 0 && filledDC && hasAnyPermissionForDC){
			int maxTransfer = plugin.getChestContainer().getMaxTransferLimit(player);
			if(originalDrops > maxTransfer){
				player.sendMessage(ChatColor.RED + "Only " + maxTransfer + " items could be transfered. The rest is dropped at your death location.");
			}else{
				player.sendMessage(ChatColor.RED + "Your total inventory did not fit in the box. The rest items were dropped at your death location.");
			}
		}
		
		
		//Dropping items that were not stored
		World world = dropLocation.getWorld();
		if(itemsStillLeft.size() > 0){
			for(ItemStack item : itemsStillLeft){
				world.dropItem(dropLocation, item);
			}
		}
		
		
		//Dropping exp
		expToUse = piMod.getEXP();
		if(expToUse > 0){
			int expValue = 10;
			for( ; expToUse > 0; expToUse -= expValue){
				ExperienceOrb orb = world.spawn(dropLocation, ExperienceOrb.class);
				orb.setExperience(expValue);
			}
			
			if(expToUse > 0){
				ExperienceOrb orb = world.spawn(dropLocation, ExperienceOrb.class);
				orb.setExperience((int) expToUse);
			}
		}
	}


	/**
	 * Checks if the passed player has any permission to use the deathChest or a spawn chest / spawn sign
	 * 
	 * @param player
	 * @return
	 */
	private boolean checkPermissionForSave(Player player) {
		return plugin.getPermissionManager().hasAnyPermissionSilent(player, PermissionNode.anySavingFeatures);
	}
}
