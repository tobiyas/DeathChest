package de.tobiyas.deathchest.chestpositions;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ChestWorlds implements ChestContainer{

	private HashMap<World, ChestContainer> deathChestConfig;
	
	@Override
	public Location getChestOfPlayer(World world, Player player) {
		ChestContainer container = deathChestConfig.get(world);
		if(container == null) return null;
		
		return container.getChestOfPlayer(world, player);
	}

	@Override
	public void createChestConfig() {
		deathChestConfig = new HashMap<World, ChestContainer>();
		
		List<World> worlds = Bukkit.getWorlds();
		
		for(World world : worlds){
			deathChestConfig.put(world, new ChestPositions(world));
		}
	}

	@Override
	public boolean checkPlayerHasChest(Player player, World world) {
		ChestContainer container = deathChestConfig.get(world);
		if(container == null) return false;
		
		return container.checkPlayerHasChest(player, world);
	}

	@Override
	public void addChestToPlayer(World world, Player player, Location location) {
		ChestContainer container = deathChestConfig.get(world);
		if(container == null) return;
		
		container.addChestToPlayer(world, player, location);		
	}

}
