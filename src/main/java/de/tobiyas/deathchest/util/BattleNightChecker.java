package de.tobiyas.deathchest.util;

import me.limebyte.battlenight.core.BattleNight;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.tobiyas.deathchest.DeathChest;

public class BattleNightChecker {

	private boolean isActive;
	private DeathChest plugin;
	
	public BattleNightChecker(){
		plugin = DeathChest.getPlugin();
		isActive = false;
	}
	
	public void checkActive() throws NoClassDefFoundError{
		Plugin bPlugin = Bukkit.getPluginManager().getPlugin("BattleNight");
		if(bPlugin == null || !bPlugin.isEnabled())
			throw new NoClassDefFoundError();
		isActive = true;
		plugin.log("Hooked plugin: BattleNight");
	}
	
	public boolean isActive(){
		return isActive;
	}
	
	public boolean checkForBattleNight(Player player){
		if(!plugin.getConfigManager().getCheckForBattleNight())
			return false;
		
		try{
			Plugin bPlugin = Bukkit.getPluginManager().getPlugin("BattleNight");
			if(bPlugin == null || !bPlugin.isEnabled())
				throw new NoClassDefFoundError();
			
			BattleNight battleNightPlugin = (BattleNight) bPlugin;
			if(isPlayingBattleNight(battleNightPlugin, player) || 
				isWatchingBattleNight(battleNightPlugin, player))
				return true;
			
			return false;
		}catch(NoClassDefFoundError e){
			plugin.log("BattleNight not found! Disable Option in Config.");
			return false;
		}catch(ClassCastException e){
			plugin.log("BattleNight not the same version as intended! Please update it!");
			return false;
		}
	}
	
	private boolean isPlayingBattleNight(BattleNight battleNight, Player player) {
		if (battleNight == null) return false;
		return battleNight.BattleUsersTeam.containsKey(player.getName());
	}

	private boolean isWatchingBattleNight(BattleNight battleNight, Player player) {
		if (battleNight == null) return false;
		return battleNight.BattleSpectators.containsKey(player.getName());
	}
}
