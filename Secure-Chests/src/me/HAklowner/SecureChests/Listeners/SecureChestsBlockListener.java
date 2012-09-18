package me.HAklowner.SecureChests.Listeners;

import me.HAklowner.SecureChests.Lock;
import me.HAklowner.SecureChests.Permission;
import me.HAklowner.SecureChests.SecureChests;
import me.HAklowner.SecureChests.Utils.Vlevel;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
	
	private static final BlockFace[] BLOCK_FACES = {BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
	
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockPlace(final BlockPlaceEvent event) {
	  
		Block b=event.getBlock();
		if(b.getType() == Material.CHEST) { //make sure its a chest
		/*if(b.getTypeId() == 54) { //make sure block click is a chest.
			
			Player player = event.getPlayer();
			
			Location chestloc = b.getLocation();
			
			//START double chest detection
			Location ccN = b.getLocation();
			Location ccE = b.getLocation();
			Location ccS = b.getLocation();
			Location ccW = b.getLocation();
			
			ccN = ccN.subtract(0,0,1);
			ccW = ccW.subtract(1,0,0);
			ccS = ccS.add(0,0,1);
			ccE = ccE.add(1,0,0);
			
			Boolean chestChange = false;
			if (ccN.getBlock().getTypeId() == 54) {
				plugin.sendMessage(Vlevel.DEBUG, player, "Chest found North");
				chestloc = ccN;
			} else if (ccE.getBlock().getTypeId() == 54) {
				plugin.sendMessage(Vlevel.DEBUG, player, "Chest found East");
				chestloc = ccE;
				chestChange = true;
			} else if (ccS.getBlock().getTypeId() == 54) {
				plugin.sendMessage(Vlevel.DEBUG, player, "Chest found South");
				chestloc = ccS;
				chestChange = true;
			} else if (ccW.getBlock().getTypeId() == 54) {
				plugin.sendMessage(Vlevel.DEBUG, player, "Chest found West");
				chestloc = ccW;
			}
			*/
			Player player = event.getPlayer();
			Location chestloc = b.getLocation();
			boolean chestChange = false;
			
			for (BlockFace face : BLOCK_FACES)
			{
				Block reletive = b.getRelative(face);
				if (reletive.getType() == Material.CHEST)
				{
					if (face.equals(BlockFace.SOUTH) || face.equals(BlockFace.WEST))
					{
						chestChange = true;
					}
					plugin.sendMessage(Vlevel.DEBUG, player, "Block found at: " + face.toString());
					chestloc = reletive.getLocation();
				}
			}
			
			
			
			plugin.sendMessage(Vlevel.DEBUG, player, "Looking for lock at loc: " + chestloc.getBlockX() + "," + chestloc.getBlockY() +","+ chestloc.getBlockZ() );
			
			Lock lock = plugin.getLockManager().getLock(chestloc, false);
			if(lock == null)
				return;
			
			plugin.sendMessage(Vlevel.DEBUG, player, "lock found at: " + chestloc.getBlockX() + "," + chestloc.getBlockY() +","+ chestloc.getBlockZ());
	
			if(lock.getOwner().equals(player.getName())) {
				plugin.sendMessage(Vlevel.COMMAND, player, "Chest lock extended.");
				if (chestChange) {
					lock.setLocation(b.getLocation());
					lock.updateLock();
				}
			} else {
				plugin.sendMessage(Vlevel.OTHER, player, "Unable to modify chest owned by ".concat(lock.getOwner()));
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)	
	public void onBlockBreak(final BlockBreakEvent event) {

		Block b = event.getBlock();
        Player player = event.getPlayer();

        //START NEW CODE
        
        if(plugin.isBlockEnabled(b.getTypeId()) || b.getLocation().add(0,1,0).getBlock().getTypeId() == 64) {//check to see if lock is enabled for said block. and check for one block higher incase its a door.
        	
        	Location blockLoc = b.getLocation();
        	
        	plugin.sendMessage(Vlevel.DEBUG, player, "--Staring block break checks--");
        	plugin.sendMessage(Vlevel.DEBUG, player, "Original ceck: " + blockLoc.getBlockX() + "," + blockLoc.getBlockY() +","+ blockLoc.getBlockZ() );
			
        	//Start Door Corrections
        	if (b.getLocation().add(0,1,0).getBlock().getTypeId() == 64) {
        		blockLoc = blockLoc.add(0,1,0);
            }//End Door Corrections
        	
        	String blockName = plugin.getBlockName(b.getTypeId());
        	//get name AFTER position corrections!
        	
        	plugin.sendMessage(Vlevel.DEBUG, player, "second check: " + blockLoc.getBlockX() + "," + blockLoc.getBlockY() +","+ blockLoc.getBlockZ() );

        	Lock lock = plugin.getLockManager().getLock(blockLoc);
        	//lock.setLocation(blockLoc);
		
        	if(lock != null) {
        		//The block has a locked status. lets now get the owner
        		String owner = lock.getOwner();
        		Integer access = lock.getAccess(player);
        		if(access == 1) { //it's yours yay!
        			lock.unlock();
        			plugin.sendMessage(Vlevel.COMMAND, player, "Removed lock on " + blockName + ".");
        			return;
        		} else if (Permission.has(player, Permission.BYPASS_BREAK)) { //you have the break bypass.
        			lock.unlock();
        			plugin.sendMessage(Vlevel.COMMAND, player, "Breaking " + owner + "'s "+ blockName +".");
        			return;
        		} else {
        			plugin.sendMessage(Vlevel.DENY, player, "Can not break " + blockName + " owned by " + owner + ".");
        			event.setCancelled(true);
        			return;
        		}
        	}
        }
	}
}
