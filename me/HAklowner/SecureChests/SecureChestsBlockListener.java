package me.HAklowner.SecureChests;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.Door;

public class SecureChestsBlockListener implements Listener {

 
	public SecureChests plugin;

	public SecureChestsBlockListener(SecureChests instance) {
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockPlace(final BlockPlaceEvent event) {
	  
		Block b=event.getBlock();
		if(b.getTypeId() == 54) { //make sure block click is a chest.
			
			Player player = event.getPlayer();
			
			Location chestloc = b.getLocation();
			
			//START double chest detection
			Location ccN = b.getLocation();
			Location ccE = b.getLocation();
			Location ccS = b.getLocation();
			Location ccW = b.getLocation();
			
			ccN = ccN.subtract(0,0,1);
			ccE = ccE.subtract(1,0,0);
			ccS = ccS.add(0,0,1);
			ccW = ccW.add(1,0,0);
			
			if (ccN.getBlock().getTypeId() == 54) {
				chestloc = chestloc.subtract(0, 0, 1);
			} else if (ccE.getBlock().getTypeId() == 54) {
				chestloc = chestloc.subtract(1, 0, 0);
			}
			//END double chest detection
			
			Boolean chestChange = false;
			if (ccN.getBlock().getTypeId() == 54) {
				chestloc = chestloc.subtract(0,0,1);
			} else if (ccE.getBlock().getTypeId() == 54) {
				chestloc = chestloc.subtract(1, 0, 0);
			} else if (ccS.getBlock().getTypeId() == 54) {
				chestloc = chestloc.add(0, 0, 1);
				chestChange = true;
			} else if (ccW.getBlock().getTypeId() == 54) {
				chestloc = chestloc.add(1, 0, 0);
				chestChange = true;
			}
			
			
			//create the YAML string location
			String yamlNewLoc = b.getLocation().getWorld().getName() + "." + b.getLocation().getBlockX() + "_" + b.getLocation().getBlockY() + "_" + b.getLocation().getBlockZ();
			String yamlOldLoc = chestloc.getWorld().getName() + "." + chestloc.getBlockX() + "_" + chestloc.getBlockY() + "_" + chestloc.getBlockZ();
			
			//get owner name if any
			String lockname = plugin.getStorageConfig().getString(yamlOldLoc.concat(".owner"));
			if(lockname == null)
				return;

	
			if(lockname.equals(player.getName())) {
				player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Chest lock extended.");
				if (chestChange) {
					plugin.getStorageConfig().set(yamlOldLoc, null);
					plugin.getStorageConfig().set(yamlNewLoc+".owner", lockname);
				}
				
				plugin.saveStorageConfig();
			} else {
				player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Unable to modify chest owned by: ".concat(lockname));
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)	
	public void onBlockBreak(final BlockBreakEvent event) {

		Block b=event.getBlock();
		
		// ########   make sure block click is a CHEST   #########
		if(b.getTypeId() == 54 && plugin.getConfig().getBoolean("Chest")) {
			
			Player player = event.getPlayer();
			
			Location chestloc = b.getLocation();
			
			//START double chest detection
			Location ccN = b.getLocation();
			Location ccE = b.getLocation();
			Location ccS = b.getLocation();
			Location ccW = b.getLocation();
			
			ccN = ccN.subtract(0,0,1);
			ccE = ccE.subtract(1,0,0);
			ccS = ccS.add(0,0,1);
			ccW = ccW.add(1,0,0);
			
			if (ccN.getBlock().getTypeId() == 54) {
				chestloc = chestloc.subtract(0, 0, 1);
			} else if (ccE.getBlock().getTypeId() == 54) {
				chestloc = chestloc.subtract(1, 0, 0);
			}
			
			//END double chest detection
			
			
			//create the YAML string location
			String yamlloc = chestloc.getWorld().getName() + "." + chestloc.getBlockX() + "_" + chestloc.getBlockY() + "_" + chestloc.getBlockZ();
			
			//get owner name if any
			String lockname = plugin.getStorageConfig().getString(yamlloc.concat(".owner"));
			if(lockname == null) 
				return; // no owner no need to continue
	
			if(lockname.equals(player.getName())) {
				player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Chest lock removed.");
				plugin.getStorageConfig().set(yamlloc, null);
				plugin.saveStorageConfig();
			} else if (player.hasPermission("SecureChests.bypass.break")) {
				player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Bypassing lock and removing chest owned by: ".concat(lockname));
				plugin.getStorageConfig().set(yamlloc, null);
				plugin.saveStorageConfig();
			} else {
				player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Unable to break chest owned by: ".concat(lockname));
				event.setCancelled(true);
			}
			
		}
		
		// ########   make sure block click is a FURNACE   #########
		else if(b.getTypeId() == 61 || b.getTypeId() == 62 && plugin.getConfig().getBoolean("Furnace")) { //furnace can have two states. off-61 or on-62 check for both
			Player player = event.getPlayer();
  
			Location furnaceloc = b.getLocation();

  
			//create the YAML string location
			String yamlloc = furnaceloc.getWorld().getName() + "." + furnaceloc.getBlockX() + "_" + furnaceloc.getBlockY() + "_" + furnaceloc.getBlockZ();
  
			//get owner name if any
			String lockname = plugin.getStorageConfig().getString(yamlloc.concat(".owner"));
				if(lockname == null) 
					return;
  
				if(lockname.equals(player.getName())) {
					player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Furnace lock removed.");
				plugin.getStorageConfig().set(yamlloc, null);
				plugin.saveStorageConfig();
			} else if (player.hasPermission("SecureChests.bypass.break")) {
				player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Bypassing lock and removing Furnace owned by: ".concat(lockname));
				plugin.getStorageConfig().set(yamlloc, null);
				plugin.saveStorageConfig();
			} else {
				player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Unable to break Furnace owned by: ".concat(lockname));
				event.setCancelled(true);
			}
		}
			
		
		// ########   make sure block click is a DOOR   #########
		else if(b.getTypeId() == 64 && plugin.getConfig().getBoolean("Door")) { 
			Player player = event.getPlayer();
			
			Location doorloc = b.getLocation();
			
			Door d = (Door)b.getState().getData();
			
			if (d.isTopHalf()) { //You clicked on the top part of the door! correct location to reflect bottom part
				doorloc = doorloc.subtract(0,1,0);
			}
			
			//Figure out if you clicked on the top or bottom part of the door and only use bottom part as lock reference
  
			//create the YAML string location
			String yamlloc = doorloc.getWorld().getName() + "." + doorloc.getBlockX() + "_" + doorloc.getBlockY() + "_" + doorloc.getBlockZ();
  
			//get owner name if any
			String lockname = plugin.getStorageConfig().getString(yamlloc.concat(".owner"));
			if(lockname == null) 
				return; // No owner no need to continue
  
				if(lockname.equals(player.getName())) {
					player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Door lock removed.");
				plugin.getStorageConfig().set(yamlloc, null);
				plugin.saveStorageConfig();
			} else if (player.hasPermission("SecureChests.bypass.break")) {
				player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Bypassing lock and removing Door owned by: ".concat(lockname));
				plugin.getStorageConfig().set(yamlloc, null);
				plugin.saveStorageConfig();
			} else {
				player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Unable to break Door owned by: ".concat(lockname));
				event.setCancelled(true);
			}
		}
	
	}
}
