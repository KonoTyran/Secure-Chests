package me.HAklowner.SecureChests.Listeners;

import me.HAklowner.SecureChests.Lock;
import me.HAklowner.SecureChests.SecureChests;

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
			//String yamlNewLoc = b.getLocation().getWorld().getName() + "." + b.getLocation().getBlockX() + "_" + b.getLocation().getBlockY() + "_" + b.getLocation().getBlockZ();
			//String yamlOldLoc = chestloc.getWorld().getName() + "." + chestloc.getBlockX() + "_" + chestloc.getBlockY() + "_" + chestloc.getBlockZ();
			
			//get owner name if any
			//String lockname = plugin.getStorageConfig().getString(yamlOldLoc.concat(".owner"));
			
			Lock lock = plugin.getLockManager().getLock(chestloc);
			if(!lock.isLocked())
				return;
	
			if(lock.getOwner().equals(player.getName())) {
				plugin.sendMessage(player, "Chest lock extended.");
				if (chestChange) {
					lock.setLocation(b.getLocation());
					lock.updateLock();
				}
			} else {
				plugin.sendMessage(player, "Unable to modify chest owned by ".concat(lock.getOwner()));
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)	
	public void onBlockBreak(final BlockBreakEvent event) {

		Block b = event.getBlock();
        Player player = event.getPlayer();

        //START NEW CODE
        
        if((SecureChests.BLOCK_LIST.containsKey(b.getTypeId()) && plugin.blockStatus.get(b.getTypeId())) || (b.getLocation().add(0,1,0).getBlock().getTypeId() == 64 && plugin.blockStatus.get(64))) {//check to see if block clicked is on the watch list and is enabled.
        	
        	Location blockLoc = b.getLocation();
        	
        	
        	if(b.getTypeId() == 54) { //do double chest location corrections
                Location ccN = b.getLocation();
                Location ccE = b.getLocation();
                Location ccS = b.getLocation();
                Location ccW = b.getLocation();

                ccN = ccN.subtract(0,0,1);
                ccE = ccE.subtract(1,0,0);
                ccS = ccS.add(0,0,1);
                ccW = ccW.add(1,0,0);

                //Boolean dchest = false;
                if (ccN.getBlock().getTypeId() == 54) {
                    blockLoc = blockLoc.subtract(0, 0, 1);
                //    dchest = true;
                } else if (ccE.getBlock().getTypeId() == 54) {
                    blockLoc = blockLoc.subtract(1, 0, 0);
                //    dchest = true;
                } else if (ccS.getBlock().getTypeId() == 54) {
                //    dchest = true;
                } else if (ccW.getBlock().getTypeId() == 54) {
                //    dchest = true;
                }
        	} //END Chest location corrections.
        	
        	//Start Door Corrections
        	if (b.getLocation().add(0,1,0).getBlock().getTypeId() == 64) {
        		blockLoc = blockLoc.add(0,1,0);
        	}
        	
            else if(b.getTypeId() == 64) { //make sure block click is a DOOR
    			Door d = (Door)b.getState().getData();
    			
    			if (d.isTopHalf()) { //You clicked on the top part of the door! correct location to reflect bottom part
    				blockLoc = blockLoc.subtract(0,1,0);
    			}
            }//End Door Corrections
        	
        	String blockName = SecureChests.BLOCK_LIST.get(b.getTypeId());
        	//get name AFTER position corrections!
        	
        	Lock lock = plugin.getLockManager().getLock(blockLoc);;
        	//lock.setLocation(blockLoc);
		
        	if(lock.isLocked()) {
        		//The block has a locked status. lets now get the owner
        		String owner = lock.getOwner();
        		Integer access = lock.getAccess(player);
        		if(access == 1) { //it's yours yay!
        			lock.unlock();
        			plugin.sendMessage(player, "Removed lock on " + blockName + ".");
        			return;
        		} else if (player.hasPermission("securechests.bypass.break")) { //you have the break bypass.
        			lock.unlock();
        			plugin.sendMessage(player, "Breaking " + owner + "'s "+ blockName +".");
        			return;
        		} else {
        			plugin.sendMessage(player, "Can not break " + blockName + " owned by " + owner + ".");
        			event.setCancelled(true);
        			return;
        		}
        	}
        }
	}
}
