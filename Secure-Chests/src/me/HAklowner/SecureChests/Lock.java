package me.HAklowner.SecureChests;

import java.util.Map;

import me.HAklowner.SecureChests.Utils.Atype;
import me.HAklowner.SecureChests.Utils.Vlevel;
import net.milkbowl.vault.permission.Permission;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.material.Door;

public class Lock {
	public SecureChests plugin;


	//New Vairables
	private String owner;
	private Map<String, Boolean> playerAccess;
	private Map<String, Boolean> clanAccess;
	private Map<String, Boolean> groupAccess;
	private Boolean isPublic;
	private Location lockLoc;
	private int id;
	private Boolean resourcelock;
	private Boolean withdraw;
	private Boolean deposit;

	private static final BlockFace[] BLOCK_FACES = {BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};

	private Location getCorrectedLoc(Location loc) {
		Block b = loc.getBlock();
		Location blockl = loc;
		
		if(b.getType() == Material.CHEST)
		{
			for (BlockFace face : BLOCK_FACES)
			{
				Block reletive = b.getRelative(face);
				if (reletive.getType() == Material.CHEST)
				{
					if (face.equals(BlockFace.NORTH) || face.equals(BlockFace.EAST))
					{
						blockl = reletive.getLocation();
					}

				}
			}
		}

		//Start Door Corrections
		
		else if(b.getType() == Material.WOODEN_DOOR) { //make sure block click is a DOOR

			Door d = (Door)b.getState().getData();

			if (d.isTopHalf()) { //You clicked on the top part of the door! correct location to reflect bottom part
				blockl = blockl.subtract(0,1,0);
			}
		}//End Door Corrections
		return blockl;
	}

	public Lock(Location loc) {
		Location temp = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		plugin = SecureChests.getInstance();
		lockLoc = getCorrectedLoc(temp);
	}

	public boolean isResouseLocked() {
		return resourcelock;
	}

	public boolean getWithdrawState() {
		return withdraw;
	}

	public boolean getDepositState() {
		return deposit;
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

	public Map<String, Boolean> getGroupAccessList() {
		return groupAccess;
	}

	public boolean onAccessList(String player) {
		if(playerAccess.containsKey(player))
			if(playerAccess.get(player))
				return true;
		return false;
	}


	//new API calls for access lists.

	public boolean onAccessList(Atype Atype, String name) {
		if (Atype == me.HAklowner.SecureChests.Utils.Atype.Clan) {
			if(clanAccess.containsKey(name))
				if(clanAccess.get(name))
					return true;
		} else if (Atype == me.HAklowner.SecureChests.Utils.Atype.Group) {
			if(groupAccess.containsKey(name))
				if(groupAccess.get(name))
					return true;
		} else if (Atype == me.HAklowner.SecureChests.Utils.Atype.Player) {
			if(playerAccess.containsKey(name))
				if(playerAccess.get(name))
					return true;
		}
		return false;
	}

	public boolean onDenyList(Atype Atype, String name) {
		if (Atype == me.HAklowner.SecureChests.Utils.Atype.Clan) {
			if(clanAccess.get(name) != null && !clanAccess.get(name))
				return true;
		} else if (Atype == me.HAklowner.SecureChests.Utils.Atype.Group) {
			if(groupAccess.get(name) != null && !playerAccess.get(name))
				return true;
		} else if (Atype == me.HAklowner.SecureChests.Utils.Atype.Player) {
			if(playerAccess.get(name) != null && !playerAccess.get(name))
				return true;
		}
		return false;
	}

	public boolean addToAccessList(Atype Atype, String name) {
		if (Atype == me.HAklowner.SecureChests.Utils.Atype.Clan) {
			if (!clanAccess.containsKey(name)) {
				clanAccess.put(name, true);
				plugin.getLockManager().addToAcessList(this, name, "clan", true);
				return true;
			} else if(!clanAccess.get(name)) {
				clanAccess.put(name, true);
				plugin.getLockManager().addToAcessList(this, name, "clan", true);
				return true;
			}
		} else if (Atype == me.HAklowner.SecureChests.Utils.Atype.Group) {
			if (!groupAccess.containsKey(name)) {
				groupAccess.put(name, true);
				plugin.getLockManager().addToAcessList(this, name, "group", true);
				return true;
			} else if(!groupAccess.get(name)) {
				groupAccess.put(name, true);
				plugin.getLockManager().addToAcessList(this, name, "group", true);
				return true;
			}
		} else if (Atype == me.HAklowner.SecureChests.Utils.Atype.Player) {
			if (!playerAccess.containsKey(name)) {
				playerAccess.put(name, true);
				plugin.getLockManager().addToAcessList(this, name, "player", true);
				return true;
			} else if(!playerAccess.get(name)) {
				playerAccess.put(name, true);
				plugin.getLockManager().addToAcessList(this, name, "player", true);
				return true;
			}
		}
		return false;
	}


	public boolean removeFromAccessList(Atype Atype, String name) {
		if (Atype == me.HAklowner.SecureChests.Utils.Atype.Clan) {
			if (clanAccess.containsKey(name)) {
				clanAccess.remove(name);
				plugin.getLockManager().removeFromAccessList(this, name, "clan");
				return true;
			}
		} else if (Atype == me.HAklowner.SecureChests.Utils.Atype.Group) {
			if (groupAccess.containsKey(name)) {
				groupAccess.remove(name);
				plugin.getLockManager().removeFromAccessList(this, name, "group");
				return true;
			}
		} else if (Atype == me.HAklowner.SecureChests.Utils.Atype.Player) {
			if (playerAccess.containsKey(name)) {
				playerAccess.remove(name);
				plugin.getLockManager().removeFromAccessList(this, name, "player");
				return true;
			}
		}
		return false;
	}

	public boolean addToDenyList(Atype Atype, String name) {
		if (Atype == me.HAklowner.SecureChests.Utils.Atype.Clan) {
			if(!clanAccess.containsKey(name) || clanAccess.get(name)) {
				clanAccess.put(name, false);
				plugin.getLockManager().addToAcessList(this, name, "clan", false);
				return true;
			}
		} else if (Atype == me.HAklowner.SecureChests.Utils.Atype.Group) {
			if(!groupAccess.containsKey(name) || groupAccess.get(name)) {
				groupAccess.put(name, false);
				plugin.getLockManager().addToAcessList(this, name, "group", false);
				return true;
			}
		} else if (Atype == me.HAklowner.SecureChests.Utils.Atype.Player) {
			if(!playerAccess.containsKey(name) || playerAccess.get(name)) {
				playerAccess.put(name, false);
				plugin.getLockManager().addToAcessList(this, name, "player", false);
				return true;
			}
		}
		return false;
	}

	//end new API calls


	public boolean onAccessList(Clan clan) {
		String clantag = clan.getTag().toLowerCase();
		if(clanAccess.containsKey(clantag))
			if(clanAccess.get(clantag))
				return true;
		return false;
	}

	public boolean onDenyList(String player)
	{
		if(playerAccess.get(player) != null && !playerAccess.get(player))
			return true;
		return false;
	}

	public boolean onDenyList(Clan clan)
	{
		if(clanAccess.get(clan.getTag().toLowerCase()) != null && !clanAccess.get(clan.getTag().toLowerCase()))
			return true;
		return false;
	}

	public boolean onGlobalList(String player)
	{

		if(plugin.getLockManager().playerOnGlobalList(owner, player))
			return true;
		return false;
	}

	public boolean onGlobalList(Clan clan)
	{
		if(plugin.getLockManager().clanOnGlobalList(owner, clan.getTag()))
			return true;
		return false;
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
		Lock nl = plugin.getLockManager().newLock(this);
		id = nl.getID();
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
	}

	public boolean addToAccessList(String clanTag, Boolean isClan) {
		if (!clanAccess.containsKey(clanTag) || !clanAccess.get(clanTag)) {
			clanAccess.put(clanTag, true);
			plugin.getLockManager().addToAcessList(this, clanTag, "clan", true);
			return true;
		}
		return false;
	}

	public boolean addToDenyList(String player) {

		if(!playerAccess.containsKey(player) || playerAccess.get(player)) {
			playerAccess.put(player, false);
			plugin.getLockManager().addToAcessList(this, player, "player", false);
			return true;
		}
		return false;
	}

	public boolean addToDenyList(Clan clan) {

		String clanTag = clan.getTag().toLowerCase();
		if(!clanAccess.containsKey(clanTag) || clanAccess.get(clanTag)) {
			clanAccess.put(clanTag, false);
			plugin.getLockManager().addToAcessList(this, clanTag, "clan", false);
			return true;
		}
		return false;
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

		if (plugin.vaultEnabled())
		{
			Permission perm = plugin.getVault();
			String[] groups = perm.getPlayerGroups(lockLoc.getWorld().getName(), owner);
			Player p = plugin.getServer().getPlayer(player);
			plugin.sendMessage(Vlevel.DEBUG, p, "Vault enabled, checking Alist for groups.");
			for (String group: groups) {
				plugin.sendMessage(Vlevel.DEBUG, p, "checking for " + group);
				if (groupAccess.containsKey(group)) {
					plugin.sendMessage(Vlevel.DEBUG, p, group + " found!");
					return 2;
				}
			}
		}

		if (me.HAklowner.SecureChests.Permission.has(plugin.getServer().getPlayer(player), me.HAklowner.SecureChests.Permission.BYPASS_OPEN)) {
			return 3; //return positive. you have bypass ability.
		}

		if (plugin.vaultEnabled()) {
			Permission perm = plugin.getVault();
			String[] groups = perm.getPlayerGroups(lockLoc.getWorld().getName(), owner);
			Player p = plugin.getServer().getPlayer(player);
			plugin.sendMessage(Vlevel.DEBUG, p, "Vault enabled checking override groups now.");
			for (String group: groups) {
				plugin.sendMessage(Vlevel.DEBUG, p, "checking for " + group);
				if (me.HAklowner.SecureChests.Permission.GroupName(p, me.HAklowner.SecureChests.Permission.BYPASS_OPEN_GROUP, group)) {
					plugin.sendMessage(Vlevel.DEBUG, p, group + " found!");
					return 3;
				}
			}
		}

		return 0;
	}

	public Integer getAccess(Player player) {
		return getAccess(player.getName());
	}

	public String getOwner() {
		return owner;
	}

	public void setWithdraw(boolean state) {
		withdraw = state;
	}

	public void setDeposit(boolean state) {
		deposit = state;
	}

	public void setResouseLock(boolean state) {
		resourcelock = state;
	}

	public void setOwner(String newOwner) {
		owner = newOwner;
	}

	public void setPlayerAccessList(Map<String, Boolean> playerAccessList) {
		playerAccess = playerAccessList;
	}

	public void setGroupAccessList(Map<String, Boolean> groupAccessList) {
		groupAccess = groupAccessList;
	}

	public void setClanAccessList(Map<String, Boolean> clanAccessList) {
		clanAccess = clanAccessList;	
	}

	public void setID(int id) {
		this.id = id;
	}
}
