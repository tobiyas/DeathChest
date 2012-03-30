package de.tobiyas.deathchest.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

import com.griefcraft.lwc.LWCPlugin;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.chestpositions.ChestContainer;
import de.tobiyas.deathchest.chestpositions.ChestPosition;
import de.tobiyas.deathchest.permissions.PermissionNode;

public class Listener_Sign implements Listener {

	private DeathChest plugin;
	
	public Listener_Sign(DeathChest plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event){
		Player player = event.getPlayer();
		String[] lines = event.getLines();
		Block signPosition = event.getBlock();
		World world = player.getWorld();
		
		if(!checkForDeathSign(lines)) return;
		
		if(!plugin.getChestContainer().worldSupported(world)){
			player.sendMessage(ChatColor.RED + "World: " + world.getName() + " not Supported for DeathChest.");
			return;
		}
		
		if(!plugin.getPermissionsManager().CheckPermissionsSilent(player, PermissionNode.createDeathChest)){
			player.sendMessage(ChatColor.RED + "You don't have Permissions to set a DeathChest.");
			return;
		}
		
		Location location = new Location(world, signPosition.getX(), signPosition.getY() - 1, signPosition.getZ());
		
		if(!checkLWC(location, player)){
			player.sendMessage(ChatColor.RED + "You don't have LWC access to the Chest below.");
			return;
		}
		
		if(!location.getBlock().getType().equals(Material.CHEST)){
			player.sendMessage(ChatColor.RED + "No chest found underneath sign.");
			return;
		}
		
		ChestContainer container = plugin.getChestContainer().getChestOfPlayer(world, player);
		if(container !=  null){
			ChestPosition chestContainer = (ChestPosition) container;
			boolean useLightning = plugin.getConfigManager().useLightningForDeathChest();
			chestContainer.destroySelf(useLightning, true); //config add Option
		}
		
		plugin.getChestContainer().addChestToPlayer(location, player);
		
		event.setLine(0, player.getName());
		event.setLine(1, "[DeathChest]");
		event.setLine(2, world.getName());
		event.setLine(3, "");
		
		player.sendMessage(ChatColor.GREEN + "DeathChest created for world: " + world.getName());
	}
	
	private boolean checkForDeathSign(String[] lines){
		for(String line : lines){
			line = line.toLowerCase();
			if(line.contains("deathchest")) return true;
		}
		
		return false;
	}
	
	private boolean checkLWC(Location location, Player player){
		if(!plugin.getConfigManager().checkDeathChestWithLWC()) return true;
		
		try{
			LWCPlugin LWC = (LWCPlugin) Bukkit.getPluginManager().getPlugin("LWC");
			if(LWC == null) throw new Exception();
			
			return LWC.getLWC().canAccessProtection(player, location.getBlock());
			
		}catch(Exception e){
			plugin.log("LWC not Found. Disable LWC Config options for DeathChests. It will be Disabled for now!");
			plugin.getConfigManager().tempTurnOffLWC();
			return true;
		}
	}
	
	
	@EventHandler
	public void signBreak(BlockBreakEvent event){
		
		Location location = null;
		Block block = event.getBlock();
		
		if(block.getType() == Material.WALL_SIGN){
			location = block.getLocation();
			location.setY(location.getY() - 1);
		}
		
		if(block.getType() == Material.CHEST){
			location = block.getLocation();
		}
		
		if(location == null) return;

		
		ChestContainer container = plugin.getChestContainer().removeFromPosition(location);
		if(container != null){
			ChestPosition position = (ChestPosition) container;
			
			boolean useLightning = plugin.getConfigManager().useLightningForDeathChest();
			position.destroySelf(useLightning, block.getType() == Material.CHEST);
			
			Player player = Bukkit.getPlayer(position.getPlayerName());
			if(player != null) player.sendMessage(ChatColor.RED + "Your DeathChest on World: " 
								+ event.getBlock().getWorld().getName() 
								+ " has been destroyed, by: " 
								+ event.getPlayer().getName());
		}
	}
	
	@EventHandler
	public void signBreak(BlockPhysicsEvent event){

		Block block = event.getBlock();
		
		if(!(block.getType() == Material.WALL_SIGN)) return;
			
		org.bukkit.material.Sign signTemp = (org.bukkit.material.Sign)block.getState().getData(); //gets the face of the Block
		if(!(block.getRelative(signTemp.getAttachedFace()).getType() == Material.AIR)) return;
		
		Sign sign = (Sign) block.getState();
		
		if(!sign.getLine(1).equals("[DeathChest]")) return;
		
		Location location = event.getBlock().getLocation();
		location.setY(location.getY() - 1);
		
		ChestContainer container = plugin.getChestContainer().removeFromPosition(location);
		if(container != null){
			ChestPosition position = (ChestPosition) container;
			
			boolean useLightning = plugin.getConfigManager().useLightningForDeathChest();
			position.destroySelf(useLightning, false);
			
			Player player = Bukkit.getPlayer(position.getPlayerName());
			if(player != null) player.sendMessage(ChatColor.RED + "Your DeathChest on World: " 
								+ event.getBlock().getWorld().getName() 
								+ " has been destroyed.");
		}
	}
}
