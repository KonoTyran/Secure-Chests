package me.HAklowner.SecureChests.Listeners;

import me.HAklowner.SecureChests.Lock;
import me.HAklowner.SecureChests.SecureChests;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class SecureChestsRedstoneListener implements Listener {

	public SecureChests plugin;

	public SecureChestsRedstoneListener(SecureChests instance) {
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onRedstoneEvent(final BlockRedstoneEvent event) {
		Block b = event.getBlock();
		if (b.getType() != Material.WOOD_DOOR)
			return; //redstone event is not a door stop now.

		Lock lock = plugin.getLockManager().getLock(b.getLocation());
		//lock.setLocation(b.getLocation());
		if(lock != null)
			if (lock.isPublic())
				event.setNewCurrent(event.getOldCurrent()); //change the "current" back to what it was originally. meaning no change.
	}
}
