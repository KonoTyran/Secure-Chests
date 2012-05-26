package me.HAklowner.SecureChests;

import java.util.Map;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Lock {
	public SecureChests plugin;

	
	//New Vairables
	private String owner;
	private Map<String, Boolean> playerAccess;
	private Map<String, Boolean> clanAccess;
	private Boolean isPublic;
	private Location lockLoc;
	private int id;
	//private int id;
	
	//end new vars
	
	//private String yamlLoc;
	
	public Lock(Location loc) {
		plugin = SecureChests.getInstance();
		lockLoc = loc;
		//yamlLoc = lockLoc.getWorld().getName() + "." + lockLoc.getBlockX() + "_" + lockLoc.getBlockY() + "_" + lockLoc.getBlockZ();
	}

	public Location getLocation() {
		return lockLoc;
	}
	
	public void setLocation(Location loc) {
		lockLoc = loc;
	}
	
	public int getID() {
		return id;
	}
	
	public Map<String, Boolean> getPlayerAccessList() {
		return playerAccess;
	}
	
	public Map<String, Boolean> getClanAccessList() {
		return clanAccess;
	}

	public boolean onAccessList(String player) {
		if(playerAccess.containsKey(player))
			if(playerAccess.get(player))
				return true;
		return false;

		
		/*
		 * OLD code 
		 
		if(getStorageConfig().getBoolean(yamlLoc+".access."+player))
			return true;
		return false;
		*/
	}

	public boolean onAccessList(Clan clan) {
		String clantag = clan.getTag().toLowerCase();
		if(clanAccess.containsKey(clantag))
			if(clanAccess.get(clantag))
				return true;
		return false;
		
		/*
		if(getStorageConfig().getBoolean(yamlLoc+".caccess."+clan.getTag().toLowerCase()))
			return true;
		return false;
		*/
	}

	public boolean onDenyList(String player) {
		
		if(playerAccess.get(player) != null && !playerAccess.get(player))
			return true;
		return false;
		
		/*
		boolean exists = getStorageConfig().contains(yamlLoc+".access."+player);
		boolean onList = getStorageConfig().getBoolean(yamlLoc+".access."+player);
		if(!onList && exists)
			return true;
		return false;
		*/
	}

	public boolean onDenyList(Clan clan) {
		
		if(clanAccess.get(clan.getTag().toLowerCase()) != null && !clanAccess.get(clan.getTag().toLowerCase()))
			return true;
		return false;
		
		/*
		boolean exists = getStorageConfig().contains(yamlLoc+".caccess."+clan.getTag().toLowerCase());
		boolean onList = getStorageConfig().getBoolean(yamlLoc+".caccess."+clan.getTag().toLowerCase());
		if(!onList && exists)
			return true;
		return false;
		*/
	}

	public boolean onGlobalList(String player) {
		
		if(plugin.getLockManager().playerOnGlobalList(owner, player))
			return true;
		return false;
		
		/*
		if(getAListConfig().getBoolean(getOwner() + ".players."+ player))
			return true;
		return false;
		*/
	}

	public boolean onGlobalList(Clan clan) {
		
		if(plugin.getLockManager().clanOnGlobalList(owner, clan.getTag()))
			return true;
		return false;
		
		/*
		if(getAListConfig().getBoolean(getOwner() + ".clans."+ clan.getTag().toLowerCase()))
			return true;
		return false;
		*/
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean set) {
		isPublic = set;
		plugin.getLockManager().updateLock(this);
	}

	public void lock(String player) {
		owner = player;
		plugin.getLockManager().newLock(this);
	}
	
	public void updateLock() {
		plugin.getLockManager().updateLock(this);
	}

	public void unlock() {
		plugin.getLockManager().removeLock(this);
	}

	public boolean addToAccessList(String player) {
		if (!playerAccess.containsKey(player)) {
			playerAccess.put(player, true);
			plugin.getLockManager().addToAcessList(this, player, "player", true);
			return true;
		} else if (!playerAccess.get(player)) {
			playerAccess.put(player, true);
			plugin.getLockManager().addToAcessList(this, player, "player", true);
			return true;
		}
		return false;
		/*
		if (!getStorageConfig().getBoolean(yamlLoc+".access."+player) || getStorageConfig().get(yamlLoc+".access."+player) == null) {
			getStorageConfig().set(yamlLoc + ".access."+player, true);
			saveStorageConfig();
			return true;
		}
		return false;
		*/
	}

	public boolean addToAccessList(Clan clan) {
		String clanTag = clan.getTag().toLowerCase();
		if (!clanAccess.containsKey(clanTag)) {
			clanAccess.put(clanTag, true);
			plugin.getLockManager().addToAcessList(this, clanTag, "clan", true);
			return true;
		} else if(!clanAccess.get(clanTag)) {
			clanAccess.put(clanTag, true);
			plugin.getLockManager().addToAcessList(this, clanTag, "clan", true);
			return true;
		}
		return false;
		
		/*
		String clanName = clan.getTag().toLowerCase();
		if (!getStorageConfig().getBoolean(yamlLoc+".caccess."+clanName) || getStorageConfig().get(yamlLoc+".caccess."+clanName) == null) {
			getStorageConfig().set(yamlLoc + ".caccess."+clanName, true);
			saveStorageConfig();
			return true;
		}
		return false;
		*/
	}
	
	public boolean addToAccessList(String clanTag, Boolean isClan) {
		if (!clanAccess.containsKey(clanTag) || !clanAccess.get(clanTag)) {
			clanAccess.put(clanTag, true);
			plugin.getLockManager().addToAcessList(this, clanTag, "clan", true);
			return true;
		}
		return false;
	}

	public boolean removeFromAccessList(String player) {
		
		if (player.toLowerCase().startsWith("c:")) {
			String clanTag = player.substring(2);
			if (clanAccess.containsKey(clanTag)) {
				clanAccess.remove(clanTag);
				plugin.getLockManager().removeFromAccessList(this, clanTag, "clan");
				return true;
			}
			return false;
		} else if (playerAccess.containsKey(player)) {
			playerAccess.remove(player);
			plugin.getLockManager().removeFromAccessList(this, player, "player");
			return true;
		}
		return false;
		
		/*
		if (player.toLowerCase().startsWith("c:")) {
			String clanTag = player.substring(2);
			if (getStorageConfig().get(yamlLoc + ".caccess."+clanTag) != null) {
				getStorageConfig().set(yamlLoc + ".caccess."+clanTag, null);
				saveStorageConfig();
				return true;
			}
		} else if (getStorageConfig().get(yamlLoc + ".access."+player) != null) {
			getStorageConfig().set(yamlLoc + ".access."+player, null);
			saveStorageConfig();
			return true;
		}
		return false;
		*/
	}

	public boolean removeFromAccessList(Clan clan) {
		
		String clanTag = clan.getTag().toLowerCase();
		if (clanAccess.containsKey(clanTag)) {
			clanAccess.remove(clanTag);
			plugin.getLockManager().removeFromAccessList(this, clanTag, "clan");
			return true;
		}
		return false;
		/*
		String clanName = clan.getTag().toLowerCase();
		if (getStorageConfig().get(yamlLoc + ".caccess."+clanName) != null) {
			getStorageConfig().set(yamlLoc + ".caccess."+clanName, null);
			saveStorageConfig();
			return true;
		}
		return false;
		*/
	}

	public boolean addToDenyList(String player) {
		
		if(!playerAccess.containsKey(player) || playerAccess.get(player)) {
			playerAccess.put(player, false);
			plugin.getLockManager().addToAcessList(this, player, "player", false);
			return true;
		}
		return false;
		
		/*
		if (getStorageConfig().getBoolean(yamlLoc+".access."+player) || getStorageConfig().get(yamlLoc+".access."+player) == null) {
			getStorageConfig().set(yamlLoc + ".access."+player, false);
			saveStorageConfig();
			return true;
		}
		return false;
		*/
	}

	public boolean addToDenyList(Clan clan) {
		
		String clanTag = clan.getTag().toLowerCase();
		if(!clanAccess.containsKey(clanTag) || clanAccess.get(clanTag)) {
			clanAccess.put(clanTag, false);
			plugin.getLockManager().addToAcessList(this, clanTag, "clan", false);
			return true;
		}
		return false;
		
		/*
		String clanName = clan.getTag().toLowerCase();
		if (getStorageConfig().getBoolean(yamlLoc+".caccess."+clanName) || getStorageConfig().get(yamlLoc+".caccess."+clanName) == null) {
			getStorageConfig().set(yamlLoc + ".caccess."+clanName, false);
			saveStorageConfig();
			return true;
		}
		return false;
		*/
	}

	public Integer getAccess(String player) {	
		//Return what access player has.
		
		if (player.equals(getOwner())) {
			return 1; //return positive you own this chest
		}
		
		//check Deny list first and return no access if your on it! check AFTER owner so you don't lock yourself out! X_X
		if (onDenyList(player)) {
			return 0;
		}

		if (isPublic()) {
			return 4; //return positive public chest.
		}

		if (onAccessList(player) || (onGlobalList(player))) {
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
		return owner;
	}
	
	public void setOwner(String newOwner) {
		owner = newOwner;
	}
	
	public void setPlayerAccessList(Map<String, Boolean> playerAccessList) {
		playerAccess = playerAccessList;
	}
	
	public void setClanAccessList(Map<String, Boolean> clanAccessList) {
		clanAccess = clanAccessList;	
	}
	
	public void setID(int id) {
		this.id = id;
	}

	public Boolean isLocked() {
		String lockname = getOwner();
		if (lockname != null)
			return true;
		return false;
	}
}
