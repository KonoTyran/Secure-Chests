package me.HAklowner.SecureChests.Listeners;

import java.util.List;

import me.HAklowner.SecureChests.Lock;
import me.HAklowner.SecureChests.SecureChests;

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
				
				Lock lock = new Lock(plugin);
				lock.setLocation(b.getLocation());
				
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
