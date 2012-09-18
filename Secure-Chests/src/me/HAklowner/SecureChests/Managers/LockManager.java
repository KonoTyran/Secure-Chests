package me.HAklowner.SecureChests.Managers;

import java.io.File;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.HAklowner.SecureChests.Lock;
import me.HAklowner.SecureChests.SecureChests;
import me.HAklowner.SecureChests.Helper.Store;
import me.HAklowner.SecureChests.Storage.DBCore;
import me.HAklowner.SecureChests.Storage.SQLite;
import me.HAklowner.SecureChests.Utils.Atype;
import me.HAklowner.SecureChests.Utils.Verblevel;
import me.HAklowner.SecureChests.Utils.Vlevel;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class LockManager {

	private SecureChests plugin;
	private Logger logger;
	private DBCore core;
	Store store = new Store(50);

	public LockManager() {
		plugin = SecureChests.getInstance();
		logger = SecureChests.getLog();
		initalizeDB();
		updateDatabase();
	}

	private void initalizeDB() {
		core = new SQLite(plugin.getDataFolder().getPath());

		if(core.checkConnection()) {
			logger.info("[SecureChests] SQLite database connection successful.");
			if(!core.tableExists("SC_Locks")) {

				SecureChests.log("[SecureChests] Creating Table: SC_Locks");

				String query = "" +
						"CREATE TABLE IF NOT EXISTS `SC_Locks` (" +
						"	`id` INTEGER PRIMARY KEY," +
						"	`World` varchar(30)," +
						"	`Owner` varchar(30)," +
						"	`PosX` int(11)," +
						"	`PosY` int(11)," +
						"	`PosZ` int(11)," +
						"	`Public` tinyint(1) DEFAULT '0'," +
						"	`Withdraw` tinyint(1) DEFAULT '1'," +
						"	`Deposit` tinyint(1) DEFAULT '1'," +
						"	`Resource Lock` tinyint(1) DEFAULT '0'" +
						")";
				core.execute(query);
			}

			if(!core.tableExists("SC_Access")) {

				SecureChests.log("[SecureChests] Creating Table: SC_Access");

				String query = "" +
						"CREATE TABLE IF NOT EXISTS `SC_Access` (" +
						" `id` INTEGER PRIMARY KEY," +
						" `Lock ID` int(11)," +
						" `Type` varchar(10)," +
						" `Name` varchar(30)," +
						" `Access` tinyint(1)" +
						")";
				core.execute(query);
			}

			if(!core.tableExists("SC_Global")) {
				SecureChests.log("[SecureChests] Creating Table: SC_Global");

				String query = "" +
						"CREATE TABLE IF NOT EXISTS `SC_Global` (" +
						"  `id` INTEGER PRIMARY KEY," +
						"  `Player` varchar(30)," +
						"  `Type` varchar(10)," +
						"  `Name` varchar(30)" +
						")";
				core.execute(query);
			}

			if (!core.tableExists("SC_Notice")) {
				SecureChests.log("[SecureChests] Creating Table: SC_Notice");
				String query = "" +
						"CREATE TABLE IF NOT EXISTS `SC_Notice` (" +
						"	`id` INTEGER PRIMARY KEY," +
						"	`Player` varchar(30)," +
						"	`Own` tinyint(1) DEFAULT '0'," +
						"	`Other` tinyint(1) DEFAULT '1'," +
						"	`Deny` tinyint(1) DEFAULT '1'," +
						"	`Debug` tinyint(1) DEFAULT '0'," +
						"	`Override` tinyint(1) DEFAULT '1'" +
						")";
				core.execute(query);
			}
		} else {
			logger.info("[SecureChests] SQLite database connection failed. :(");
		}
	}

	public void saveVerbLevel(Verblevel vl, String name) {
		String query = "SELECT * FROM `SC_Notice` WHERE `Player` = '" + name + "'";
		ResultSet res = core.select(query);
		try {
			if (res != null){
				if(res.next()) {
					int id = res.getInt("id");
					int own = 0,other = 0,debug = 0,deny = 0,override = 0;
					if(vl.getOwn())
						own = 1;
					if(vl.getOther())
						other = 1;
					if(vl.getDebug())
						debug = 1;
					if(vl.getDeny())
						deny = 1;
					if(vl.getOverride())
						override = 1;
					query = "UPDATE `SC_Notice` SET `Player` = '"+name+"', `Own` = '"+own+"', `Other` = '"+other+"', `Debug` = '"+debug+"', `Deny` = '"+deny+"', `Override` = '"+override+"' WHERE id = '"+id+"' ";
				} else {
					int own = 0,other = 0,debug = 0,deny = 0,override = 0;
					if(vl.getOwn())
						own = 1;
					if(vl.getOther())
						other = 1;
					if(vl.getDebug())
						debug = 1;
					if(vl.getDeny())
						deny = 1;
					if(vl.getOverride())
						override = 1;
					query = "INSERT INTO `SC_Notice` (`Player`,`Own`,`Other`,`Debug`,`Deny`,`Override`) VALUES ('"+name+"','"+own+"','"+other+"','"+debug+"','"+deny+"','"+override+"')";
				}
			}
		} catch (SQLException e) {
			e.getMessage();
		}
		core.execute(query);
	}

	public Verblevel getVerbLevel(String name) {
		String query = "SELECT * FROM `SC_Notice` WHERE `Player` = '" + name + "'";
		ResultSet res = core.select(query);
		Verblevel vl = new Verblevel();
		try {
			if (res != null) {
				if (res.next()) {
					vl.setOwn(res.getBoolean("Own"));
					vl.setDebug(res.getBoolean("Debug"));
					vl.setOther(res.getBoolean("Other"));
					vl.setOverride(res.getBoolean("Override"));
					vl.setDeny(res.getBoolean("Deny"));
				} else {
					vl.setOwn(plugin.getConfig().getBoolean("Notification.Own"));
					vl.setDebug(false);
					vl.setOther(plugin.getConfig().getBoolean("Notification.Other"));
					vl.setOverride(plugin.getConfig().getBoolean("Notification.Override"));
					vl.setDeny(plugin.getConfig().getBoolean("Notification.Deny"));
				}
			}
			else {
				vl.setOwn(plugin.getConfig().getBoolean("Notification.Own"));
				vl.setDebug(false);
				vl.setOther(plugin.getConfig().getBoolean("Notification.Other"));
				vl.setOverride(plugin.getConfig().getBoolean("Notification.Override"));
				vl.setDeny(plugin.getConfig().getBoolean("Notification.Deny"));
			}
		} catch (SQLException e) {
			logger.warning("[SecureChests] " + e.toString());
		}
		return vl;
	}
	
	public Lock getLock(Location loc) {
		return getLock(loc, true);
	}

	public Lock getLock(Location loc , boolean check) {
		if (loc.getWorld() == null)
			return null;
		
		Lock lock = new Lock(loc);
		
		if(!check)
			lock.setLocation(loc);
		else
			loc=lock.getLocation(); //get the corrected location from the lock.
		
		if(store.containsKey(loc)) {
			return store.get(loc);
		}


		String query = "SELECT * FROM `SC_Locks` WHERE" +
				" `World` = '" + loc.getWorld().getName() + "' AND " +
				" `PosX` = " + loc.getBlockX() + " AND " +
				" `PosY` = " + loc.getBlockY() + " AND " +
				" `PosZ` = " + loc.getBlockZ() + ";";
		ResultSet res = core.select(query);

		try {
			if(res.next()) {

				int lockID = res.getInt("id");

				lock.setID(lockID);
				lock.setOwner(res.getString("Owner"));
				lock.setPublic(res.getBoolean("Public"));
				lock.setDeposit(res.getBoolean("Deposit"));
				lock.setWithdraw(res.getBoolean("Withdraw"));
				lock.setResouseLock(res.getBoolean("Resource Lock"));

				//Get Local access list.
				String accessQuery = "SELECT * FROM `SC_Access` WHERE" +
						"`Lock ID` = " + lockID;
				ResultSet aRes = core.select(accessQuery);

				Map<String, Boolean> playerAccessList = new HashMap<String, Boolean>();
				Map<String, Boolean> clanAccessList = new HashMap<String, Boolean>();
				Map<String, Boolean> groupAccessList = new HashMap<String, Boolean>();

				while(aRes.next()) {
					String type = aRes.getString("Type");
					String name = aRes.getString("Name");
					Boolean ac = aRes.getBoolean("Access");
					if (type.equals("player"))
						playerAccessList.put(name, ac);
					if (type.equals("clan"))
						clanAccessList.put(name, ac);
					if (type.equals("group"))
						groupAccessList.put(name, ac);
				}

				lock.setPlayerAccessList(playerAccessList);
				lock.setClanAccessList(clanAccessList);
				lock.setGroupAccessList(groupAccessList);

			} else { //no lock at current location. don't execute any more queries.
				return null;
			}
		} catch (Exception ex) {
			for (StackTraceElement el : ex.getStackTrace()) {
				System.out.print(el.toString());
			}
		}
		//logger.log(Level.INFO, "putting lock in memory");
		store.put(loc, lock);
		return lock;
	}
	
	public Map<Atype, List<String>> getGlobalAccessList(String player) {
		Map<Atype, List<String>> list = new HashMap<Atype, List<String>>();
		try {
			String query = "SELECT * FROM `SC_Global` WHERE `Player` = '"+player+"'";
			ResultSet result = core.select(query);
			List<String> clans = new ArrayList<String>();
			List<String> players = new ArrayList<String>();
			List<String> groups = new ArrayList<String>();
			while(result.next()) {
				String type = result.getString("Type");
				if (type.equals("clan"))
				{
					clans.add(result.getString("Name"));
				}
				else if (type.equals("player"))
				{
					logger.info("added player to list");
					players.add(result.getString("Name"));
				}
				else if (type.equals("group"))
				{
					groups.add(result.getString("Name"));
				}
			}
			list.put(Atype.Clan, clans);
			list.put(Atype.Player, players);
			list.put(Atype.Group, groups);
		} catch (SQLException e1) {
			return list;
		}
		return list;
	}

	public Lock newLock(Lock lock) {
		String query = "INSERT INTO `SC_Locks` (`World`, `owner`, `PosX`, `PosY`, `PosZ`, `Public`) VALUES ('"+lock.getLocation().getWorld().getName()+"', '"+lock.getOwner()+"', '"+lock.getLocation().getBlockX()+"', '"+lock.getLocation().getBlockY()+"', '"+lock.getLocation().getBlockZ()+"', '0')";
		//logger.info(query);
		core.execute(query);
		Lock newlock = getLock(lock.getLocation());
		store.put(newlock);
		return newlock;
	}

	public void updateLock(Lock lock) {
		int pub = 0;
		if (lock.isPublic())
			pub = 1;
		String query = "UPDATE `SC_Locks` SET `owner` = '"+lock.getOwner()+"', `Public` = '"+pub+"', `PosX` = '"+lock.getLocation().getBlockX()+"', `PosY` = '"+lock.getLocation().getBlockY()+"', `PosZ` = '"+lock.getLocation().getBlockZ()+"' WHERE `id` =" + lock.getID();
		core.execute(query);
		store.clear();
		store.put(lock);
	}
	
	public void purgestore() {
		store.clear();
	}

	public void addToAcessList(Lock lock, String name, String type, Boolean access) {
		int na = 0; //false
		if (access)
			na = 1;
		String query = "INSERT INTO `SC_Access` (`Lock ID`,`Type`,`Name`,`Access`) VALUES ('"+lock.getID()+"','"+type+"','"+name+"','"+na+"')";
		core.execute(query);
		store.put(lock);
	}

	public void removeFromAccessList(Lock lock, String name, String type) {
		String query = "DELETE FROM `SC_Access` WHERE `Lock ID` = "+lock.getID()+" AND `Name` = '"+name+"' AND `type` = '"+type+"'";
		core.execute(query);
		store.put(lock);
	}

	public void removeLock(Lock lock) {
		String query = "DELETE FROM `SC_Access` WHERE `Lock ID` = " + lock.getID();
		core.execute(query);
		query = "DELETE FROM `SC_Locks` WHERE `id` = " + lock.getID();
		core.execute(query);
		store.remove(lock);
	}

	public Boolean playerOnGlobalList(String owner, String user) {
		String query = "SELECT `Name` FROM `SC_Global` WHERE `Player` = '"+owner+"' AND `Type` = 'player'";
		ResultSet result = core.select(query);

		try {
			while(result.next()) {
				if(user.equals(result.getString("Name")))
					return true;
			}
		} catch (SQLException e) {
			//ohh no access list empty!
			return false;
		}
		return false;
	}

	public Boolean clanOnGlobalList(String owner, String clantag) {
		String query = "SELECT `Name` FROM `SC_Global` WHERE `Player` = '"+owner+"' AND `Type` = 'clan'";
		ResultSet result = core.select(query);

		try {
			while(result.next()) {
				if(clantag.equals(result.getString("Name")))
					return true;
			}
		} catch (SQLException e) {
			//ohh no access list empty!
			return false;
		}
		return false;
	}
	
	public Boolean groupOnGlobalList(String owner, String group) {
		String query = "SELECT `Name` FROM `SC_Global` WHERE `Player` = '"+owner+"' AND `Type` = 'group'";
		ResultSet result = core.select(query);

		try {
			while(result.next()) {
				if(group.equals(result.getString("Name")))
					return true;
			}
		} catch (SQLException e) {
			//ohh no access list empty!
			return false;
		}
		return false;
	}

	public void addToGlobalList(String owner, String name, String type) {
		String query = "INSERT INTO `SC_Global` (`Player`,`Type`,`Name`) VALUES ('"+owner+"','"+type+"','"+name+"')";
		core.execute(query);
	}

	public void removeFromGlobalList(String owner, String name, String type) {
		String query = "DELETE FROM `SC_Global` WHERE `Player` = '"+owner+"' AND `Type` = '"+type+"' AND `Name` = '"+name+"'";
		core.execute(query);
	}


	private boolean purgeConsole = false;
	private Player purgePlayer;
	private void purgeMessage(String msg) {
		if (!purgeConsole) { //send to player if player started command
			plugin.sendMessage(Vlevel.COMMAND, purgePlayer, msg);
		}
		//send to console regardless of who started it.
		SecureChests.log("[" + plugin.getDescription().getName() + "] "+msg);
	}

	public void purgeGhostEntry(Player player) {
		purgeConsole = false;
		purgePlayer = player;
		purgeGhostEntry();
	}

	public void purgeGhostEntry(boolean fromconsole) {
		if (fromconsole) { 
			purgeConsole = true;
			purgeGhostEntry();
		}
	}

	private void purgeGhostEntry() {
		purgeMessage("Starting Ghost Purge");

		//limit to 100 locks loaded at a time.
		int pass = 0;
		boolean keepgoing = true;
		int total = 0;
		while (keepgoing) {
			try {
				String query = "SELECT * FROM `SC_Locks` ORDER BY `id` LIMIT 100 OFFSET "+ ((pass*100)-total);
				ResultSet result = core.select(query);
				int count = 0;
				while(result.next()) {
					count++;
					List<World> worlds = plugin.getServer().getWorlds();
					String worldstr = result.getString("world");
					if(worlds.contains(plugin.getServer().getWorld(worldstr))) {
						Location loc = new Location(plugin.getServer().getWorld(worldstr), result.getDouble("PosX"), result.getDouble("PosY"), result.getDouble("PosZ"));
						if (!plugin.isBlockEnabled(loc.getBlock().getTypeId())) {
							//ohh no its a ghost entry! squash it!
							String delquery = "DELETE FROM `SC_Locks` WHERE `id` = " + result.getInt("id");
							String delalist = "DELETE FROM `SC_Access` WHERE `lock ID` = " + result.getInt("id");
							core.execute(delalist);
							core.execute(delquery);
							total++;
						}
					} else {
						purgeMessage("Removing world: " +worldstr);
						String delquery = "DELETE FROM `SC_Locks` WHERE `world` = '" + worldstr + "'";
						core.execute(delquery);
						total++;
					}
				}
				if (count == 0) { //no rows returned we reached the end!
					keepgoing = false;
				}
			} catch (SQLException e) {
				purgeMessage("there has been an error while attempting to purge ghost entries. :(");
				keepgoing = false;
			}
			pass++;
			purgeMessage("purged " + total + " ghost locks so far!");
		}
		purgeMessage("Purge Complete " +total+ " Ghost locks purged");
		store.clear();
	}

	public void purgePlayer(String purgename, Player player) {
		plugin.sendMessage(Vlevel.COMMAND, player, "Starting Purge of player " + purgename);

		plugin.sendMessage(Vlevel.COMMAND, player, "Purging Access Lists.");
		try {
			String query = "SELECT * FROM `SC_Locks` WHERE `Owner` = '"+purgename+"'";
			ResultSet result = core.select(query);
			int count = 0;
			while(result.next()) {
				count++;
				core.execute("DELETE FROM `SC_Access` WHERE `lock ID` = " + result.getInt("id"));
			}
			plugin.sendMessage(Vlevel.COMMAND, player, "Purging locks.");
			core.execute("DELETE FROM `SC_Locks` WHERE `Owner` = '" + purgename + "'");
			plugin.sendMessage(Vlevel.COMMAND, player, "Purging global list.");
			core.execute("DELETE FROM `SC_Global` WHERE `Player` = '" + purgename + "'");
			plugin.sendMessage(Vlevel.COMMAND, player, "Purging Notify settings");
			core.execute("DELETE FROM `SC_Notice` WHERE `Player` = '" + purgename + "'");
			plugin.sendMessage(Vlevel.COMMAND, player, "Purge Complete " +count+ " locks purged");
			
			store.clear();
		} catch (SQLException e) {
			purgeMessage("there has been an error while attempting to purge player "+ purgename +". :(");
		}

	}

	public void updateFromFlatFile() {
		logger.info("[SecureChests] Starting Upgrade (note server will hang while upgrade is taking place");
		File storageConfigFile = new File("plugins/SecureChests", "storage.yml");
		FileConfiguration storageConfig =  YamlConfiguration.loadConfiguration(storageConfigFile);
		Set<String> worldList = storageConfig.getConfigurationSection("").getKeys(false);
		int total = 0;
		for(String world:worldList) {
			logger.log(Level.INFO, "[SecureChests] starting import of world "+ world);
			Set<String> locationList = storageConfig.getConfigurationSection(world).getKeys(false);
			int worldtotal = 0;
			for(String location:locationList) {
				String[] loc = location.split("_");
				String owner = storageConfig.getString(world+"."+location+".owner");
				boolean ispublic = storageConfig.getBoolean(world+"."+location+".public");
				int pub = 0;
				if (ispublic)
					pub = 1;
				total++;
				worldtotal++;
				if (worldtotal % 50 == 0) {
					logger.log(Level.INFO, "[SecureChests] "+worldtotal+"/"+locationList.size()+" Processed in world "+world);
					logger.log(Level.INFO, "[SecureChests] "+total+" across all worlds");
				}
				core.execute("INSERT INTO `SC_Locks` (`World`, `owner`, `PosX`, `PosY`, `PosZ`, `Public`) VALUES ('"+world+"', '"+owner+"', '"+loc[0]+"', '"+loc[1]+"', '"+loc[2]+"', '"+pub+"')");
				String query = "SELECT `id` FROM `SC_Locks` WHERE" +
						" `World` = '" + world + "' AND " +
						" `PosX` = " + loc[0] + " AND " +
						" `PosY` = " + loc[1] + " AND " +
						" `PosZ` = " + loc[2] + ";";
				ResultSet idres = core.select(query);
				int id = 0;
				try {
					while(idres.next()) {
						id = idres.getInt("id");
					}
				} catch (SQLException e) {
					return;
				}
				ConfigurationSection acs = storageConfig.getConfigurationSection(world+"."+location+".access");
				if (acs != null) {
					Set<String> access = acs.getKeys(false);
					for (String name : access) {
						boolean hasaccess = storageConfig.getBoolean(world+"."+location+".access."+name);
						int ac = 0;
						if (hasaccess)
							ac = 1;
						String pquery = "INSERT INTO `SC_Access` (`Lock ID`,`Type`,`Name`,`Access`) VALUES ('"+id+"','player','"+name+"','"+ac+"')";
						core.execute(pquery);
					}
				}

				ConfigurationSection ccs = storageConfig.getConfigurationSection(world+"."+location+".access");
				if(ccs != null) {
					Set<String> caccess = ccs.getKeys(false);
					for (String clantag : caccess) {
						boolean hasaccess = storageConfig.getBoolean(world+"."+location+".access."+clantag);
						int ac = 0;
						if (hasaccess)
							ac = 1;
						String pquery = "INSERT INTO `SC_Access` (`Lock ID`,`Type`,`Name`,`Access`) VALUES ('"+id+"','player','"+clantag+"','"+ac+"')";
						core.execute(pquery);
					}
				}
			}
			logger.log(Level.INFO, "[SecureChests] "+worldtotal+"/"+locationList.size()+" Processed in world "+world);
			logger.log(Level.INFO, "[SecureChests] "+total+" across all worlds");

		}	
		logger.log(Level.INFO, "[SecureChests] Upgrade Complete! Processed "+total+" entries");
	}

	public void closeConnection()
	{
		core.close();
	}

	private void updateDatabase()
	{
		String query = null;

		//From 0.6.1 to 0.6.2
		if (!core.existsColumn("SC_Locks", "Withdraw")) {
			logger.log(Level.INFO,"[SecureChests] Updating database to version post-0.6.2");
			query = "ALTER TABLE SC_Locks ADD COLUMN `Withdraw` tinyint(1) DEFAULT 1;";
			core.execute(query);
			query = "ALTER TABLE SC_Locks ADD COLUMN `Deposit` tinyint(1) DEFAULT 1;";
			core.execute(query);
			query = "ALTER TABLE SC_Locks ADD COLUMN `Resource Lock` tinyint(1) DEFAULT 0;";
			core.execute(query);

		}

	}

}
