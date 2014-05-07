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
import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.chestpositions.ChestContainer;
import de.tobiyas.deathchest.chestpositions.ChestPosition;
import de.tobiyas.deathchest.permissions.PermissionNode;

/**
 * @author tobiyas
 *
 */
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
		
		if(event.getLine(0).equals(ChatColor.RED + "[GraveStone]")) {
			event.getPlayer().sendMessage(ChatColor.RED + "You may create no GraveStone!");
			event.setCancelled(true);
			return;
		}
		
		if(!checkForDeathSign(lines)) return;
		
		if(!plugin.getChestContainer().worldSupported(world)){
			player.sendMessage(ChatColor.RED + "World: " + world.getName() + " not Supported for DeathChest.");
			return;
		}
		
		if(!plugin.getPermissionManager().checkPermissionsSilent(player, PermissionNode.createDeathChest) && 
			!plugin.getPermissionManager().checkPermissionsSilent(player, PermissionNode.simpleUse)){
			player.sendMessage(ChatColor.RED + "You don't have Permissions to set a DeathChest.");
			return;
		}
		
		Location location = new Location(world, signPosition.getX(), signPosition.getY() - 1, signPosition.getZ());
		
		if(!plugin.getProtectionManager().checkProtection(location, player)){
			player.sendMessage(ChatColor.RED + "You don't have access to the Chest below.");
			return;
		}
		
		if(!location.getBlock().getType().equals(Material.CHEST)){
			player.sendMessage(ChatColor.RED + "No chest found underneath sign.");
			return;
		}
		
		String playerCreation = checkOtherPlayerCreation(event.getLine(1), player);
		
		ChestContainer container = plugin.getChestContainer().getChestOfPlayer(world, playerCreation);
		if(container !=  null){
			ChestPosition chestContainer = (ChestPosition) container;
			boolean useLightning = plugin.getConfigManager().useLightningForDeathChest();
			chestContainer.destroySelf(useLightning, true);
		}
		
		plugin.getChestContainer().addChestToPlayer(location, playerCreation);
		
		event.setLine(0, playerCreation);
		event.setLine(1, "[DeathChest]");
		event.setLine(2, world.getName());
		event.setLine(3, "");
		
		if(player.getName() != playerCreation)
			player.sendMessage(ChatColor.GREEN + "DeathChest created for world: " + world.getName() + " for player: " + ChatColor.RED + playerCreation);
		else
			player.sendMessage(ChatColor.GREEN + "DeathChest created for world: " + world.getName());
	}
	
	/**
	 * Checks if the String[] contain "deathchest"
	 * 
	 * @param lines the String[] to check
	 * @return if it containes "deathchest"
	 */
	private boolean checkForDeathSign(String[] lines){
		for(String line : lines){
			line = line.toLowerCase();
			if(line.contains("deathchest")) return true;
		}
		
		return false;
	}
	
	private String checkOtherPlayerCreation(String possiblePlayer, Player player){
		String orgPlayer = player.getName();
		
		if(possiblePlayer.length() == 0 || possiblePlayer.toLowerCase().contains("deathchest"))
			return orgPlayer;
		
		if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.otherChestCreate)) 
			return orgPlayer;
		
		if(Bukkit.getServer().getOfflinePlayer(possiblePlayer) == null)
			return orgPlayer;

		return possiblePlayer;
	}
	
	
	@EventHandler
	public void signBreak(BlockBreakEvent event){
		
		Location location = null;
		Block block = event.getBlock();
		
		if(block.getType() == Material.SIGN_POST){
			Sign sign = (Sign) block.getState();
			if(sign.getLine(0).equals(ChatColor.RED + "[GraveStone]"))
				if(plugin.interactSpawnContainerController().interactSign(event.getPlayer(), event.getBlock().getLocation()))
					event.setCancelled(true);
			
			return;
		}
		
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
	public void signInteract(PlayerInteractEvent event){		
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		
		Block block = event.getClickedBlock();
		Location signPosition = event.getClickedBlock().getLocation();
		Player player = event.getPlayer();
		
		if(block.getType() == Material.SIGN_POST){
			if(checkGraveSign(player, block))
				return;
		}
		
		if(block.getType() != Material.WALL_SIGN)
			return;
		
		Sign sign = (Sign) block.getState();
		
		if(!sign.getLine(1).equals("[DeathChest]"))
			return;
		
		ChestContainer container = plugin.getChestContainer().getChestOfPlayer(player.getWorld(), player);
		if(container == null){
			player.sendMessage(ChatColor.RED + "This is not Your DeathChest!");
			return;
		}
		
		ChestPosition chestPos = (ChestPosition) container;
		String playerName = chestPos.getPlayerName();
		
		if(!playerName.equals(player.getName())){
			return;
		}
		
		if(signPosition.distanceSquared(chestPos.getSignLocation()) == 0){
			giveEXP(player, chestPos.getStoredEXP());
		}else{
			player.sendMessage(ChatColor.RED + "This is not Your DeathChest!");
		}
	}
	
	private boolean checkGraveSign(Player player, Block signBlock){
		Sign sign = (Sign) signBlock.getState();		
		
		if(sign.getLine(0).equals(ChatColor.RED + "[GraveStone]"))
			if(plugin.interactSpawnContainerController().interactSign(player, signBlock.getLocation()))
				return true;
		
		return false;
	}
		
	private void giveEXP(Player player, int exp){		
		if(exp == 0){
			player.sendMessage(ChatColor.RED + "No Experience stored.");
			return;
		}
		
		for( ; exp > 0; exp -= 5){
			player.giveExp(5);
		}

		if(exp > 0){
			player.giveExp(exp);
		}
		player.sendMessage(ChatColor.GREEN + "All EXP given to you");
			
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
