package me.HAklowner.SecureChests;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

public class SecureChestsBlockListener extends BlockListener {
	
	public SecureChests plugin;

	public SecureChestsBlockListener(SecureChests instance) {
		plugin = instance;
	}
	
	public void onBlockBreak(BlockBreakEvent event) {
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
				player.sendMessage("Chest lock removed.");
				plugin.getStorageConfig().set(yamlloc, null);
				plugin.saveStorageConfig();
			} else if (player.hasPermission("SecureChests.bypass.break")) {
				player.sendMessage("Bypassing lock and removeing chest owned by: ".concat(lockname));
				plugin.getStorageConfig().set(yamlloc, null);
				plugin.saveStorageConfig();
			} else {
				player.sendMessage("Unable to break chest owned by: ".concat(lockname));
				event.setCancelled(true);
			}
			
		}
	}
}
