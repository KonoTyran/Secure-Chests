package me.HAklowner.SecureChests.Listeners;

import java.util.List;

import me.HAklowner.SecureChests.Lock;
import me.HAklowner.SecureChests.SecureChests;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class SecureChestsExplosionListener implements Listener {
	
	public SecureChests plugin;

	public SecureChestsExplosionListener(SecureChests instance) {
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.LOW)	
	public void onExplosionEvent(final EntityExplodeEvent event) {
		List<Block> blockList = event.blockList();
		int len = blockList.size();
		for(int i = 0; i < len; i++) {
			Block b = blockList.get(i);
			int bId = b.getTypeId();
			
			if (SecureChests.BLOCK_LIST.containsKey(bId)) {
				boolean block_explode = plugin.blockExplosion.get(bId);
				

				Location blockLoc = b.getLocation();
				
	        	if(bId == 54) { //do double chest location corrections
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
	        	
				Lock lock = plugin.getLockManager().getLock(blockLoc);;
				//lock.setLocation(blockLoc);
				
				if(lock.isLocked()) {
					if(block_explode) {
						blockList.remove(i);
						i--;
						len--;
					} else {
						lock.unlock();
					}
				}
			}
		}
	}
}
