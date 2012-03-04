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
			
			Location ccN = b.getLocation();
			Location ccE = b.getLocation();
			Location ccS = b.getLocation();
			Location ccW = b.getLocation();
			
			ccN = ccN.subtract(0,0,1);
			ccE = ccE.subtract(1,0,0);
			ccS = ccS.add(0,0,1);
			ccW = ccW.add(1,0,0);
			
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
		
else if(b.getTypeId() == 64) { //make sure block click is a DOOR
      
      Player player = event.getPlayer();
      
      Location chestloc = b.getLocation();
      
      Location ccN = b.getLocation();
      Location ccE = b.getLocation();
      Location ccS = b.getLocation();
      Location ccW = b.getLocation();
      
      ccN = ccN.subtract(0,0,1);
      ccE = ccE.subtract(1,0,0);
      ccS = ccS.add(0,0,1);
      ccW = ccW.add(1,0,0);
      
      Boolean chestChange = false;
      if (ccN.getBlock().getTypeId() == 64) {
        chestloc = chestloc.subtract(0,0,1);
      } else if (ccE.getBlock().getTypeId() == 64) {
        chestloc = chestloc.subtract(1, 0, 0);
      } else if (ccS.getBlock().getTypeId() == 64) {
        chestloc = chestloc.add(0, 0, 1);
        chestChange = true;
      } else if (ccW.getBlock().getTypeId() == 64) {
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
        player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Door lock extended.");
        if (chestChange) {
          plugin.getStorageConfig().set(yamlOldLoc, null);
          plugin.getStorageConfig().set(yamlNewLoc+".owner", lockname);
        }
        
        plugin.saveStorageConfig();
      } else {
        player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Unable to modify door owned by: ".concat(lockname));
        event.setCancelled(true);
      }
      
    }
		
	}
	
	@EventHandler(priority = EventPriority.LOW)	
	public void onBlockBreak(final BlockBreakEvent event) {

    Block b=event.getBlock();
		if(b.getTypeId() == 54) { //make sure block click is a CHEST.
			
			Player player = event.getPlayer();
			
			Location chestloc = b.getLocation();
			
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
			
			//create the YAML string location
			String yamlloc = chestloc.getWorld().getName() + "." + chestloc.getBlockX() + "_" + chestloc.getBlockY() + "_" + chestloc.getBlockZ();
			
			//get owner name if any
			String lockname = plugin.getStorageConfig().getString(yamlloc.concat(".owner"));
			if(lockname == null) 
				return;
	
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
		
		else if(b.getTypeId() == 61) { //make sure block click is a FURNACE
		  boolean furnaceconf = plugin.getConfig().getBoolean("Furnace"); // Check config allowed locking furances.
      if (furnaceconf == true) {
      Player player = event.getPlayer();
      
      Location chestloc = b.getLocation();
      
      Location ccN = b.getLocation();
      Location ccE = b.getLocation();
      Location ccS = b.getLocation();
      Location ccW = b.getLocation();
      
      ccN = ccN.subtract(0,0,1);
      ccE = ccE.subtract(1,0,0);
      ccS = ccS.add(0,0,1);
      ccW = ccW.add(1,0,0);
      
      if (ccN.getBlock().getTypeId() == 61) {
        chestloc = chestloc.subtract(0, 0, 1);
      } else if (ccE.getBlock().getTypeId() == 61) {
        chestloc = chestloc.subtract(1, 0, 0);
      }
      
      //create the YAML string location
      String yamlloc = chestloc.getWorld().getName() + "." + chestloc.getBlockX() + "_" + chestloc.getBlockY() + "_" + chestloc.getBlockZ();
      
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
      
    } else {
      // Do Nothing
    }
		}
		
		else if(b.getTypeId() == 64) { //make sure block click is a DOOR
      boolean doorconf = plugin.getConfig().getBoolean("Door"); // Check config allowed locking doors
      if (doorconf == true) {
      Player player = event.getPlayer();
      
      Location chestloc = b.getLocation();
      
      Location ccN = b.getLocation();
      Location ccE = b.getLocation();
      Location ccS = b.getLocation();
      Location ccW = b.getLocation();
      
      ccN = ccN.subtract(0,0,1);
      ccE = ccE.subtract(1,0,0);
      ccS = ccS.add(0,0,1);
      ccW = ccW.add(1,0,0);
      
      if (ccN.getBlock().getTypeId() == 61) {
        chestloc = chestloc.subtract(0, 0, 1);
      } else if (ccE.getBlock().getTypeId() == 61) {
        chestloc = chestloc.subtract(1, 0, 0);
      }
      
      //create the YAML string location
      String yamlloc = chestloc.getWorld().getName() + "." + chestloc.getBlockX() + "_" + chestloc.getBlockY() + "_" + chestloc.getBlockZ();
      
      //get owner name if any
      String lockname = plugin.getStorageConfig().getString(yamlloc.concat(".owner"));
      if(lockname == null) 
        return;
  
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
      
    } else {
      // Do Nothing
    }
    }
		
	}
}
