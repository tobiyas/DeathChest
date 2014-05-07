package de.tobiyas.deathchest.spawncontainer;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.deathchest.DeathChest;
import de.tobiyas.deathchest.config.YamlConfigExtended;
import de.tobiyas.deathchest.util.PlayerDropModificator;

public class SpawnSign {
	
	private DeathChest plugin;
	
	private Location position;
	private Location blockPosition;
	
	private String playerName;
	private List<ItemStack> items;
	
	private static int saveVal = 0;
	
	private double exp;
	
	private String oldBlock;
	private byte oldBlockData;
	
	private String oldSignBlock;
	private byte oldSignBlockData;
	
	private int ticksToDespawn;
	private boolean toRemove;
	

	public SpawnSign(PlayerDropModificator piMod){
		Player player = piMod.getPlayer();
		plugin = DeathChest.getPlugin();
		position = player.getLocation().getBlock().getLocation().clone();
		
		if(position.getBlock().getType() != Material.AIR){
			position.setY(position.getY() + 1);
		}
		
		if(position.clone().subtract(0, 1, 0).getBlock().getType() == Material.CHEST){
			position.setY(position.getY() + 1);
		}
		
		blockPosition = position.clone();
		blockPosition.setY(blockPosition.getY() - 1);
		
		oldBlock = blockPosition.getBlock().getType().name();
		oldBlockData = blockPosition.getBlock().getData();
		
		oldSignBlock = position.getBlock().getType().name();
		oldSignBlockData = position.getBlock().getData();
		
		items = new LinkedList<ItemStack>();
		for(ItemStack item : piMod.getTransferredItems())
			items.add(item.clone());
		
		exp = piMod.getEXP() * plugin.getConfigManager().getEXPMulti();
		piMod.removeAllEXP();
		
		this.playerName = player.getName();
		ticksToDespawn = 60 * plugin.getConfigManager().getspawnSignDespawnTime();
		toRemove = false;
	}
	
	private SpawnSign(YamlConfigExtended config){
		plugin = DeathChest.getPlugin();
		config.load();
		
		int posX = config.getInt("signpos.x");
		int posY = config.getInt("signpos.y");
		int posZ = config.getInt("signpos.z");
		String worldName = config.getString("signpos.world");
		World world = Bukkit.getWorld(worldName);
		
		position = new Location(world, posX, posY, posZ);
		blockPosition = position.clone();
		blockPosition.setY(blockPosition.getY() - 1);
		
		playerName = config.getString("owner");
		exp = config.getDouble("exp", 0D);
		
		oldBlock = config.getString("oldblock");
		oldBlockData = config.getByteList("oldblockdata").get(0);
		
		oldSignBlock = config.getString("oldsignblock");
		oldSignBlockData = config.getByteList("oldsignblockdata").get(0);
		
		items = new ArrayList<ItemStack>();
		ticksToDespawn = config.getInt("despawnTime", 60 * plugin.getConfigManager().getspawnSignDespawnTime());
		
		for(String id : config.getYAMLChildren("items")){
			ItemStack stack = config.getItemStack("items." + id);
			items.add(stack);
		}
		
		toRemove = false;
	}
	
	public SpawnSign spawnSign(){
		blockPosition.getBlock().setType(Material.BEDROCK);
		position.getBlock().setType(Material.SIGN_POST);
		
		Sign sign = (Sign)position.getBlock().getState();
		sign.setLine(0, ChatColor.RED + "[GraveStone]");
		sign.setLine(1, playerName);
		
		List<String> message = RandomText.randomText();
		String part1 = message.get(0);
		String part2 = message.get(1);
		
		sign.setLine(2, ChatColor.AQUA + part1);
		sign.setLine(3, ChatColor.AQUA + part2);
		sign.update();
		
		plugin.getProtectionManager().protectSign(position, playerName);
		
		return this;
	}
	
	/**
	 * Saves the Sign to the dataFolder
	 */
	public void saveSign(){
		File file = new File(plugin.getDataFolder() + File.separator + "gravestones" + File.separator);
		file.mkdirs();
		
		YamlConfigExtended config = new YamlConfigExtended(file.getPath() + File.separator + "stone" + saveVal++ + ".yml");
		config.set("signpos.x", position.getBlockX());
		config.set("signpos.y", position.getBlockY());
		config.set("signpos.z", position.getBlockZ());
		config.set("signpos.world", position.getWorld().getName());
		
		config.set("owner", playerName);
		
		List<Byte> dataList = new LinkedList<Byte>();
		dataList.add(oldBlockData);
		config.set("oldblockdata", dataList);
		config.set("oldblock", oldBlock);
		
		List<Byte> dataList2 = new LinkedList<Byte>();
		dataList2.add(oldSignBlockData);
		config.set("oldsignblockdata", dataList2);
		config.set("oldsignblock", oldSignBlock);
		
		config.set("exp", exp);
		config.set("despawnTime", ticksToDespawn);
		
		config.createSection("items");
		int i = 0;
		for(ItemStack stack : items){
			config.set("items." + i, stack);
			i++;
		}
		
		config.save();
	}
	
	/**
	 * loads sign from the given Path
	 * 
	 * @param path, the file-System Path of the sign
	 * @return the Sign-Object
	 */
	public static SpawnSign loadSign(String path){
		try{
			SpawnSign sign = new SpawnSign(new YamlConfigExtended(path));
			
			File file = new File(path);
			file.delete();
			
			if(!sign.stillCorrect()){
				DeathChest.getPlugin().log("Removing Corrupted GraveStone");
				return null;
			}
			return sign;
		}catch(Exception e){
			File file = new File(path);
			file.delete();
			return null;
		}
	}
	
	private boolean stillCorrect(){
		if(position.getBlock().getType() != Material.SIGN_POST) return false;
		if(blockPosition.getBlock().getType() != Material.BEDROCK) return false;
		
		return true;
	}
	
	/**
	 * if a player interacts with a sign
	 * 
	 * @param player
	 * @return if the sign was used.
	 */
	public boolean interactSign(Player player){
		if(player != null){
			if(!(player.isOp() || player.getName().equalsIgnoreCase(this.playerName))) return false;
		}
		

		for(ItemStack stack : items){
			position.getWorld().dropItemNaturally(position, stack);
		}
	
		boolean useOrbs = plugin.getConfigManager().getUseOrbs();
		if(useOrbs || player == null){
			for( ; exp > 0; exp -= 5){
				ExperienceOrb orb = position.getWorld().spawn(position, ExperienceOrb.class);
				orb.setExperience(5);
			}
			
			if(exp > 0){
				ExperienceOrb orb = position.getWorld().spawn(position, ExperienceOrb.class);
				orb.setExperience((int) exp);
			}
		}else{
			for( ; exp > 0; exp -= 5){
				player.giveExp(5);
			}
			
			if(exp > 0){
				player.giveExp((int) exp);
			}
		}
		
		position.getBlock().setType(Material.getMaterial(oldSignBlock));
		position.getBlock().setData(oldSignBlockData);
		
		blockPosition.getBlock().setType(Material.getMaterial(oldBlock));
		blockPosition.getBlock().setData(oldBlockData);
		
		plugin.getProtectionManager().unprotectSign(position);
		return true;
	}

	public boolean isAt(Location location) {
		return position.equals(location);
	}
	
	public List<ItemStack> getItems(){
		List<ItemStack> stacks = new LinkedList<ItemStack>();
		
		for(ItemStack stack : items){
			stacks.add(stack);
		}
		
		return stacks;
	}
	
	public String getOwner(){
		return playerName;
	}
	
	public Location getLocation(){
		return position;
	}

	public void tick() {
		ticksToDespawn--;
		if(ticksToDespawn <= 0){
			notifyPlayer();
			interactSign(null);
			toRemove = true;
		}
		
	}
	
	private void notifyPlayer(){
		Player owner = Bukkit.getPlayer(playerName);
		if(owner == null)
			return;
		
		owner.sendMessage(ChatColor.RED + "Your grave stone in " + ChatColor.AQUA +
							position.getWorld().getName() + ChatColor.RED + " has wittered and has been removed.");
	}
	
	public boolean toRemove(){
		return toRemove;
	}

	public String getTimeLeft() {
		String time = "";
		if(ticksToDespawn > 3600){
			int hours = (int) Math.ceil(ticksToDespawn / 3600);
			time += ChatColor.RED + "" + hours + ChatColor.GREEN + "h ";
		}
		
		int minutes = ticksToDespawn % 3600;
		minutes = (int) Math.ceil(minutes / 60);
		if(minutes != 0){
			time += ChatColor.RED + "" + minutes + ChatColor.GREEN + "min ";
		}
		
		int secs = ticksToDespawn % 60;
		time += ChatColor.RED + "" + secs + ChatColor.GREEN + "secs";
		
		return time;
	}
}
