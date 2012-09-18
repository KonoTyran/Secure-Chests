package me.HAklowner.SecureChests.Listeners;

import org.bukkit.block.Block;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.Acrobot.ChestShop.Events.Protection.ProtectBlockEvent;
import com.Acrobot.ChestShop.Events.Protection.ProtectionCheckEvent;

import me.HAklowner.SecureChests.Lock;
import me.HAklowner.SecureChests.SecureChests;
import me.HAklowner.SecureChests.Utils.Vlevel;

public class SecureChestsChestShopListener implements Listener {

	public SecureChests plugin;

	public SecureChestsChestShopListener(SecureChests instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onProtectionCheck(final ProtectionCheckEvent event) {

		Lock lock = plugin.getLockManager().getLock(event.getBlock().getLocation());
		
		if (lock != null)	{
			if(!lock.getOwner().equals(event.getPlayer().getName())) {
				plugin.sendMessage(Vlevel.DEBUG, event.getPlayer(), "Blocking Chest Shop");
				event.setResult(Result.DENY);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockProtect(ProtectBlockEvent event) {
		if (event.isProtected()) {
			return;
		}

		Block block = event.getBlock();
		String player = event.getName();

		if (block == null || player == null) {
			return;
		}

		if (plugin.getLockManager().getLock(block.getLocation()) == null)
		{
			//Lock lock = new Lock(block.getLocation());
			//lock.lock(player);
		}
	}
}
