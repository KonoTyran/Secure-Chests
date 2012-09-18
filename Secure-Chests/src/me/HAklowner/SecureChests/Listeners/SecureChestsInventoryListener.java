package me.HAklowner.SecureChests.Listeners;

import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

import me.HAklowner.SecureChests.Lock;
import me.HAklowner.SecureChests.SecureChests;
import me.HAklowner.SecureChests.Utils.Vlevel;


//Will Work on this part later. need to upgrade lock retreiveing method before i focus on this.

public class SecureChestsInventoryListener implements Listener {

	public SecureChests plugin;

	public SecureChestsInventoryListener(SecureChests instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onInventoryClick (final InventoryClickEvent event) {
		if (event.getRawSlot() == -999)
			return; //no need to continue if click was outside of inv.
		
		HumanEntity he = event.getWhoClicked();
		if(he instanceof Player) {
			
			Player player = (Player) he;
			plugin.sendMessage(Vlevel.DEBUG, player, "slottype: "+event.getSlotType().toString() + ", rawslot: " + event.getRawSlot() + ", slot: "+event.getSlot());	
			
			InventoryHolder invholder = event.getInventory().getHolder();
			
			Location blockloc = new Location(null, 0, 0, 0);
			if (invholder instanceof Chest) {
				Chest block = (Chest) invholder;
				blockloc = block.getLocation();
			}
			if (invholder instanceof Furnace) {
				plugin.isBlockEnabled(61);
				Furnace furnace = (Furnace) invholder;
				blockloc = furnace.getLocation();
			}
			
			
			if (blockloc == null)
				return;
			
			
			Lock lock = plugin.getLockManager().getLock(blockloc);
			if(lock != null) {
				int access = lock.getAccess(player);
				if (access == 0) {
					plugin.sendMessage(Vlevel.DEBUG, player, "Stopping inventory interaction");
					event.setCancelled(true);
				}
			}
			
		}
	}

		/*
	@EventHandler(priority = EventPriority.LOW)
	public void onInventoryClick (final InventoryOpenEvent event) {
		HumanEntity he = event.getPlayer();
		if(he instanceof Player) {
			Player player = (Player) he;
			if (event.getInventory().getType() == InventoryType.CHEST) {
				event.setCancelled(true);
			}
		}
	}	
		 */
	}