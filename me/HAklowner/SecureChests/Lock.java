package me.HAklowner.SecureChests;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Lock {
	public SecureChests plugin;

	private Location lockLoc;
	private String yamlLoc;
	
	public Lock(SecureChests instance) {
		plugin = instance;
	}
	
	public void setLocation(Location loc) {    	
		lockLoc = loc;
		yamlLoc = lockLoc.getWorld().getName() + "." + lockLoc.getBlockX() + "_" + lockLoc.getBlockY() + "_" + lockLoc.getBlockZ();
	}
	
	public Location getLocation() {
		return lockLoc;
	}
	
	public boolean onAccessList(String player) {
		if(plugin.getStorageConfig().getBoolean(yamlLoc+".access."+player))
			return true;
		return false;
	}
	
	public boolean onDenyList(String player) {
		if(!plugin.getStorageConfig().getBoolean(yamlLoc+".access."+player))
			return true;
		return false;
	}
	
	public boolean onGlobalList(String player) {
		if(plugin.getAListConfig().getBoolean(getOwner() + "."+ player))
			return true;
		return false;
	}
	
	public void lock(String player) {
		plugin.getStorageConfig().set(yamlLoc + ".owner", player);
		plugin.saveStorageConfig();
	}
	
	public void unlock() {
		plugin.getStorageConfig().set(yamlLoc, null);
		plugin.saveStorageConfig();
	}
	
	public boolean addToAccessList(String player) {
		if (!plugin.getStorageConfig().getBoolean(yamlLoc+".access."+player) || plugin.getStorageConfig().get(yamlLoc+".access."+player) == null) {
			plugin.getStorageConfig().set(yamlLoc + ".access."+player, true);
			plugin.saveStorageConfig();
			return true;
		}
		return false;
	}
	
	public boolean removeFromAccessList(String player) {
		if (plugin.getStorageConfig().get(yamlLoc + ".access."+player) != null) {
			plugin.getStorageConfig().set(yamlLoc + ".access."+player, null);
			plugin.saveStorageConfig();
			return true;
		}
		return false;
	}
	
	public boolean addToDenyList(String player) {
		if (!plugin.getStorageConfig().getBoolean(yamlLoc+".access."+player) || plugin.getStorageConfig().get(yamlLoc+".access."+player) == null) {
			plugin.getStorageConfig().set(yamlLoc + ".access."+player, false);
			plugin.saveStorageConfig();
			return true;
		}
		return false;
	}
	
	public Integer getAccess(String player) {
		//Return what access player has.
		if (player.equals(getOwner())) {
			return 1; //return positive you own this chest
		} else if (onAccessList(player) || (onGlobalList(player) && !onDenyList(player))) {
			return 2; //return positive your on one of the access lists
		} else if (plugin.getServer().getPlayer(player).hasPermission("securechests.bypass.open")) {
			return 3; //return positive. you have bypass ability.
		} else {
			return 0;
		}
	}
	
	public Integer getAccess(Player player) {
		return getAccess(player.getName());
	}
	
	public String getOwner() {
		return plugin.getStorageConfig().getString(yamlLoc + ".owner");
	}
	
	public Boolean isLocked() {
		String lockname = getOwner();
		if (lockname != null)
			return true;
		return false;
	}
}
