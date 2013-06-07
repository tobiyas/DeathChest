package de.tobiyas.deathchest.util;

import java.util.List;

import me.limebyte.battlenight.api.battle.Battle;
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
			
			if(isPlayingBattleNight(player) || isWatchingBattleNight(player))
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
	
	private boolean isPlayingBattleNight(Player player) {
		List<Battle> battles = BattleNight.instance.getAPI().getBattleManager().getBattles();
		for(Battle battle : battles){
			if(battle.containsPlayer(player))
				return true;
		}
		
		return false;
	}

	private boolean isWatchingBattleNight(Player player) {
		List<Battle> battles = BattleNight.instance.getAPI().getBattleManager().getBattles();
		for(Battle battle : battles){
			if(battle.containsPlayer(player))
				return true;
		}
		
		return false;
	}
}
