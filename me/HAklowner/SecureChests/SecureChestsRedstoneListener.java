package me.HAklowner.SecureChests;

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
		if (b.getTypeId() != 64)
			return; //redstone event is not a door stop now.

		Lock lock = new Lock(plugin);
		lock.setLocation(b.getLocation());
		if(lock.isLocked()) 
			event.setNewCurrent(event.getOldCurrent()); //change the "current" back to what it was originally. meaning no change.
	}
}
