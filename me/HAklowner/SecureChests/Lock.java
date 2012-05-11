package me.HAklowner.SecureChests;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;

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

	public boolean onAccessList(Clan clan) {
		if(plugin.getStorageConfig().getBoolean(yamlLoc+".caccess."+clan.getTag().toLowerCase()))
			return true;
		return false;
	}

	public boolean onDenyList(String player) {
		boolean exists = plugin.getStorageConfig().contains(yamlLoc+".access."+player);
		boolean onList = plugin.getStorageConfig().getBoolean(yamlLoc+".access."+player);
		if(!onList && exists)
			return true;
		return false;
	}

	public boolean onDenyList(Clan clan) {
		boolean exists = plugin.getStorageConfig().contains(yamlLoc+".caccess."+clan.getTag().toLowerCase());
		boolean onList = plugin.getStorageConfig().getBoolean(yamlLoc+".caccess."+clan.getTag().toLowerCase());
		if(!onList && exists)
			return true;
		return false;
	}

	public boolean onGlobalList(String player) {
		if(plugin.getAListConfig().getBoolean(getOwner() + ".players."+ player))
			return true;
		return false;
	}

	public boolean onGlobalList(Clan clan) {
		if(plugin.getAListConfig().getBoolean(getOwner() + ".clans."+ clan.getTag().toLowerCase()))
			return true;
		return false;
	}

	public boolean isPublic() {
		if(plugin.getStorageConfig().getBoolean(yamlLoc+".public"))
			return true;
		return false;
	}

	public void setPublic(boolean set) {
		plugin.getStorageConfig().set(yamlLoc+".public", set);
		plugin.saveStorageConfig();
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

	public boolean addToAccessList(Clan clan) {
		String clanName = clan.getTag().toLowerCase();
		if (!plugin.getStorageConfig().getBoolean(yamlLoc+".caccess."+clanName) || plugin.getStorageConfig().get(yamlLoc+".caccess."+clanName) == null) {
			plugin.getStorageConfig().set(yamlLoc + ".caccess."+clanName, true);
			plugin.saveStorageConfig();
			return true;
		}
		return false;
	}

	public boolean removeFromAccessList(String player) {
		if (player.toLowerCase().startsWith("c:")) {
			String clanTag = player.substring(2);
			if (plugin.getStorageConfig().get(yamlLoc + ".caccess."+clanTag) != null) {
				plugin.getStorageConfig().set(yamlLoc + ".caccess."+clanTag, null);
				plugin.saveStorageConfig();
				return true;
			}
		} else if (plugin.getStorageConfig().get(yamlLoc + ".access."+player) != null) {
			plugin.getStorageConfig().set(yamlLoc + ".access."+player, null);
			plugin.saveStorageConfig();
			return true;
		}
		return false;
	}

	public boolean removeFromAccessList(Clan clan) {
		String clanName = clan.getTag().toLowerCase();
		if (plugin.getStorageConfig().get(yamlLoc + ".caccess."+clanName) != null) {
			plugin.getStorageConfig().set(yamlLoc + ".caccess."+clanName, null);
			plugin.saveStorageConfig();
			return true;
		}
		return false;
	}

	public boolean addToDenyList(String player) {
		if (plugin.getStorageConfig().getBoolean(yamlLoc+".access."+player) || plugin.getStorageConfig().get(yamlLoc+".access."+player) == null) {
			plugin.getStorageConfig().set(yamlLoc + ".access."+player, false);
			plugin.saveStorageConfig();
			return true;
		}
		return false;
	}

	public boolean addToDenyList(Clan clan) {
		String clanName = clan.getTag().toLowerCase();
		if (plugin.getStorageConfig().getBoolean(yamlLoc+".caccess."+clanName) || plugin.getStorageConfig().get(yamlLoc+".caccess."+clanName) == null) {
			plugin.getStorageConfig().set(yamlLoc + ".caccess."+clanName, false);
			plugin.saveStorageConfig();
			return true;
		}
		return false;
	}

	public Integer getAccess(String player) {	
		//Return what access player has.
		if (player.equals(getOwner())) {
			return 1; //return positive you own this chest
		}

		if (isPublic()) {
			return 4; //return positive public chest.
		}

		if (onAccessList(player) || (onGlobalList(player) && !onDenyList(player))) {
			return 2; //return positive your on one of the access lists
		}

		if(plugin.usingSimpleClans) {
			ClanPlayer cp = plugin.simpleClans.getClanManager().getClanPlayer(player);

			if (cp != null) {
				Clan clan = cp.getClan();
				if (onAccessList(clan) || (onGlobalList(clan) && !onDenyList(clan))) {
					return 2; // access though lists.
				}
			}
		}

		if (plugin.getServer().getPlayer(player).hasPermission("securechests.bypass.open")) {
			return 3; //return positive. you have bypass ability.
		}

		return 0;
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
