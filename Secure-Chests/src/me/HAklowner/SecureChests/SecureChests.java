package me.HAklowner.SecureChests;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.HAklowner.SecureChests.Commands.LockCommand;
import me.HAklowner.SecureChests.Commands.SCCommand;
import me.HAklowner.SecureChests.Commands.UnLockCommand;
import me.HAklowner.SecureChests.Config.Config;
import me.HAklowner.SecureChests.Config.Language;
import me.HAklowner.SecureChests.Listeners.SecureChestsBlockListener;
import me.HAklowner.SecureChests.Listeners.SecureChestsChestShopListener;
import me.HAklowner.SecureChests.Listeners.SecureChestsExplosionListener;
//import me.HAklowner.SecureChests.Listeners.SecureChestsInventoryListener;
import me.HAklowner.SecureChests.Listeners.SecureChestsPlayerListener;
import me.HAklowner.SecureChests.Listeners.SecureChestsRedstoneListener;
import me.HAklowner.SecureChests.Managers.LockManager;
import me.HAklowner.SecureChests.Utils.Atype;
import me.HAklowner.SecureChests.Utils.MetricsSC;
import me.HAklowner.SecureChests.Utils.Verblevel;
import me.HAklowner.SecureChests.Utils.Vlevel;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans2;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.Acrobot.ChestShop.ChestShop;

public class SecureChests extends JavaPlugin {

	private static SecureChests instance;

	//ClassListeners
	private final SecureChestsPlayerListener playerListener = new SecureChestsPlayerListener(this);
	private final SecureChestsBlockListener blockListener = new SecureChestsBlockListener(this);
	private final SecureChestsRedstoneListener redstoneListener = new SecureChestsRedstoneListener(this);
	private final SecureChestsExplosionListener explosionListener = new SecureChestsExplosionListener(this);
	private final SecureChestsChestShopListener chestShopListener = new SecureChestsChestShopListener(this);
	//private final SecureChestsInventoryListener inventoryListener = new SecureChestsInventoryListener(this);

	//Define the logger
	static final Logger logger = Logger.getLogger("SecureChests");


	//simpleClan vars.
	public SimpleClans simpleClans;
	public boolean usingSimpleClans;

	private Boolean usingVault = false;
	private static net.milkbowl.vault.permission.Permission permission = null;
	
	private ChestShop chestshop;

	
	 public static File dataFolder = new File("plugins/SecureChests");

	//block list stuffs
	private Map<Integer, String> BLOCK_LIST = createBlockListMap(); //make block list name
	private Map<Integer, String> blockCustom = new HashMap<Integer, String>();
	private Map<Integer, String> BLOCK_CONFIG = createBlockConfigMap(); //make block perm list
	private Map<Integer, Boolean> blockStatus = new HashMap<Integer, Boolean>();
	private Map<Integer, Boolean> blockExplosion = new HashMap<Integer, Boolean>();

	//managers
	private LockManager LockManager;



	private static Map<Integer, String> createBlockListMap() {
		Map<Integer, String> result = new HashMap<Integer, String>();
		result.put(23, "dispenser");
		result.put(54 , "chest");
		result.put(61, "furnace");
		result.put(62, "furnace");
		result.put(64, "door");
		result.put(96, "trapdoor");
		result.put(107, "gate");
		result.put(117, "potion stand");
		result.put(84, "jukebox");
		return result;
	}

	private static Map<Integer, String> createBlockConfigMap() {
		Map<Integer, String> result = new HashMap<Integer, String>();
		result.put(23, "Dispenser");
		result.put(54, "Chest");
		result.put(61, "Furnace");
		result.put(62, "Furnace");
		result.put(64, "Door");
		result.put(96, "Trapdoor");
		result.put(107, "Gate");
		result.put(117, "Potion");
		result.put(84, "Jukebox");
		return result;	
	}


	public Map<Player, Integer> scCmd = new HashMap<Player, Integer>();
	public Map<Player, String> scAList = new HashMap<Player, String>();	
	public Map<Player, Atype> scAtype = new HashMap<Player, Atype>();
	public Map<Player, Verblevel> scVlevel = new HashMap<Player, Verblevel>();

	public boolean isBlockEnabled(int id) {
		return blockStatus.containsKey(id);
	}

	public String getBlockName(int id) {
		if (BLOCK_LIST.containsKey(id))
			return BLOCK_LIST.get(id);
		else if(blockCustom.containsKey(id))
			return blockCustom.get(id);
		else
			return null;
	}

	public boolean isBlockUnexploadable(int id) {
		if (blockExplosion.containsKey(id))
			return blockExplosion.get(id);
		return false;
	}

	public static Logger getLog() {
		return logger;
	}

	public static SecureChests getInstance() {
		return instance;
	}

	public static void log(String msg, Object... arg) {
		if (arg == null || arg.length == 0) {
			logger.log(Level.INFO, msg);
		} else {
			logger.log(Level.INFO, new StringBuilder().append(MessageFormat.format(msg, arg)).toString());
		}
	}


	public void sendMessage(Vlevel Vl, Player player, String Message) {
		if (Vl == Vlevel.DEBUG && scVlevel.get(player).getDebug()) {
			player.sendMessage(ChatColor.BLUE + "[SCD]" + ChatColor.WHITE + " " + Message);
		} else if (Vl == Vlevel.COMMAND) {
			player.sendMessage(Config.getLocal(Language.prefix) + Message);
		} else if (Vl == Vlevel.OTHER && scVlevel.get(player).getOther()) {
			player.sendMessage(Config.getLocal(Language.prefix) + Message);
		} else if (Vl == Vlevel.OWN && scVlevel.get(player).getOwn()) {
			player.sendMessage(Config.getLocal(Language.prefix) + Message);
		} else if (Vl == Vlevel.DENY && scVlevel.get(player).getDeny()) {
			player.sendMessage(Config.getLocal(Language.prefix) + Message);
		} else if (Vl == Vlevel.OVERRIDE && scVlevel.get(player).getOverride()) {
			player.sendMessage(Config.getLocal(Language.prefix) + Message);
		}
	}

	public void sendMessage(Vlevel vl, CommandSender sender, String Message) {
		sender.sendMessage(ChatColor.BLUE + "[Secure Chests]" + ChatColor.WHITE + " " + Message);
	}


	public void displayHelp(Player player) {
		displayHelp(player, 1);
	}

	public void displayHelp(Player player, Integer page) {

		List<String> helpList = new ArrayList<String>();

		if (Permission.has(player, Permission.LOCK)) {
			helpList.add(ChatColor.AQUA + "/sc "+Config.getLocal(Language.COMMAND_LOCK)+" (/lock)" + ChatColor.GRAY + " - " + Config.getLocal(Language.HELP_LOCK));
			helpList.add(ChatColor.AQUA + "/sc "+Config.getLocal(Language.COMMAND_UNLOCK)+" (/unlock)" + ChatColor.GRAY + " - " + Config.getLocal(Language.HELP_UNLOCK));
			helpList.add(ChatColor.AQUA + "/sc "+Config.getLocal(Language.COMMAND_ADD)+" " + Config.getLocal(Language.USERNAME) + ChatColor.GRAY + " - " + Config.getLocal(Language.HELP_ADD));
			helpList.add(ChatColor.AQUA + "/sc "+Config.getLocal(Language.COMMAND_DENY)+" " + Config.getLocal(Language.USERNAME) + ChatColor.GRAY + " - " + Config.getLocal(Language.HELP_DENY));
			helpList.add(ChatColor.AQUA + "/sc "+Config.getLocal(Language.COMMAND_REMOVE)+" " + Config.getLocal(Language.USERNAME) + ChatColor.GRAY + " - " + Config.getLocal(Language.HELP_REMOVE));
			helpList.add(ChatColor.AQUA + "/sc "+Config.getLocal(Language.COMMAND_GADD)+" " + Config.getLocal(Language.USERNAME) + ChatColor.GRAY + " - " + Config.getLocal(Language.HELP_GADD));
			helpList.add(ChatColor.AQUA + "/sc "+Config.getLocal(Language.COMMAND_GREMOVE)+" " + Config.getLocal(Language.USERNAME) + ChatColor.GRAY + " - " + Config.getLocal(Language.HELP_GREMOVE));
			helpList.add(ChatColor.AQUA + "/sc "+Config.getLocal(Language.COMMAND_NOTICE)+ ChatColor.GRAY + " - " + Config.getLocal(Language.HELP_NOTICE));
		}
		if (Permission.has(player, Permission.LOCK_PUBLIC)) {
			helpList.add(ChatColor.AQUA + "/sc "+Config.getLocal(Language.COMMAND_PUBLIC)+ ChatColor.GRAY + " - " + Config.getLocal(Language.HELP_PUBLIC));
		}

		if (!Permission.has(player, Permission.INFO) && Permission.has(player, Permission.LOCK))
			helpList.add(ChatColor.AQUA + "/sc "+Config.getLocal(Language.COMMAND_INFO) + ChatColor.GRAY + " - " + Config.getLocal(Language.HELP_INFO));

		if (Permission.has(player, Permission.LOCK_TRANSFER) && !Permission.has(player, Permission.ADMIN_TRANSFER)) {
			helpList.add(ChatColor.AQUA + "/sc newowner username" + ChatColor.GRAY + " - " + Config.getLocal(Language.HELP_TRANSFER));
		} 
		else if (Permission.has(player, Permission.ADMIN_TRANSFER)) {
			helpList.add(ChatColor.RED + "/sc "+Config.getLocal(Language.COMMAND_TRANSFER)+" "+ Config.getLocal(Language.USERNAME) + ChatColor.GRAY + " - " + Config.getLocal(Language.HELP_TRANSFER_BYPASS));
		}

		if (Permission.has(player, Permission.INFO)) {
			helpList.add(ChatColor.RED + "/sc "+Config.getLocal(Language.COMMAND_INFO) + ChatColor.GRAY + " - " + Config.getLocal(Language.HELP_INFO_ALL));
		}

		if (Permission.has(player, Permission.BYPASS_LOCK)) {
			helpList.add(ChatColor.RED + "/sc "+Config.getLocal(Language.COMMAND_LOCK)+" " + Config.getLocal(Language.USERNAME) + " (/lock "+Config.getLocal(Language.USERNAME)+")" + ChatColor.GRAY + " - " + Config.getLocal(Language.HELP_LOCK_OTHER));
		}

		if (Permission.has(player, Permission.ADMIN_RELOAD)) {
			helpList.add(ChatColor.RED + "/sc "+Config.getLocal(Language.COMMAND_RELOAD) + ChatColor.GRAY + " - " + Config.getLocal(Language.HELP_RELOAD));
		}

		if (Permission.has(player, Permission.ADMIN_PURGE)) {
			helpList.add(ChatColor.RED + "/sc "+Config.getLocal(Language.COMMAND_PURGE) + ChatColor.GRAY + " - " + Config.getLocal(Language.HELP_LOCK_OTHER));
		}

		if (Permission.has(player, Permission.ADMIN_DELETE_PLAYER)) {
			helpList.add(ChatColor.RED + "/sc "+Config.getLocal(Language.COMMAND_DELETE_PLAYER)+" " + Config.getLocal(Language.USERNAME) + ChatColor.GRAY + " - " + Config.getLocal(Language.HELP_DELETE_PLAYER));
		}

		if (helpList.isEmpty() ) {
			helpList.add(Config.getLocal(Language.DONT_HAVE_PERMISSION).replace("%permission", Permission.LOCK.toString()));
		}

		int totalPageNum = (int) Math.ceil( (double)helpList.size() / (double)7 );

		page -= 1;

		if (page > totalPageNum-1 || page <= 0) {
			page = 0;
		}

		player.sendMessage(ChatColor.GOLD + "----- Secure Chests " + getDescription().getVersion() + " - Page " + (page+1) + "/" + totalPageNum + " -----");

		for (int i = (page * 7); i <= (page * 7) + 6 ;i++) {
			if (i >= helpList.size())
				break;
			player.sendMessage(helpList.get(i));
		}

		player.sendMessage(ChatColor.GOLD + "----- "+Config.getLocal(Language.HELP_FOOTER)+" -----");

	}

	private void initBlockData() {
		// Get current active block
		blockStatus.clear(); // clear the enable/disabled for all block during initialization.
		for (Integer key : BLOCK_LIST.keySet() ) { //start all blocks disabled then get proper status from config file
			blockStatus.put(key, false); //default to not allow locking.
			blockStatus.put(key, this.getConfig().getBoolean("Active." + BLOCK_CONFIG.get(key)));
			blockExplosion.put(key, true);	//Default to block all explosions.
			blockExplosion.put(key, this.getConfig().getBoolean("Block_Explosions." + BLOCK_CONFIG.get(key)));
		}
		if(getConfig().getBoolean("usecustom")) {
			Iterator<String> keys = getConfig().getConfigurationSection("custom").getKeys(false).iterator();
			while(keys.hasNext()) {
				String itemid = keys.next();
				try {
					int i = Integer.parseInt(itemid);
					String name = getConfig().getString("custom."+itemid);
					logger.log(Level.INFO, "[SecureChests] added custom block id " + i + " with name "+ name);
					blockStatus.put(i, true);
					blockExplosion.put(i, true);
					blockCustom.put(i, name);
				} catch (NumberFormatException e) {
					logger.log(Level.SEVERE, "[SecureChests] Invalid custom item ID " + itemid);
				}
			}
		}

	}

	@Override
	public void onEnable() {
		instance = this;
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(blockListener, this);
		pm.registerEvents(playerListener, this);
		pm.registerEvents(redstoneListener, this);
		pm.registerEvents(explosionListener, this);

		Plugin csplug = pm.getPlugin("ChestShop");
		if (csplug instanceof ChestShop)
		{
			chestshop = ((ChestShop) csplug);
			getLogger().log(Level.INFO, "ChestShop found.");
			pm.registerEvents(chestShopListener, this);    
		}
		
		//pm.registerEvents(inventoryListener, this); ill work on withdraw/deposit only stuff later.

		// load / create config
		FileConfiguration cfg = getConfig();
		FileConfigurationOptions cfgOptions = cfg.options();
		cfgOptions.copyDefaults(true).copyHeader(true);
		saveConfig();

		Config.setup();

		initBlockData(); //call this to get on/off status of lockable blocks.

		registerCommands();

		Plugin plug = pm.getPlugin("SimpleClans2");

		if (plug instanceof SimpleClans)
		{
			simpleClans = ((SimpleClans) plug);
			usingSimpleClans = true;
			getLogger().log(Level.INFO, "SimpleClans found.");    
		}


		if(setupPermissions()) {
			usingVault = true;
		}

		metrics();

		//if (getConfig().getBoolean("ghost_purge_on_startup")) {
		//	GhostPurge gp = new GhostPurge();
		//	gp.purge(true);
		//}

		LockManager = new LockManager();

		Player[] plist = getServer().getOnlinePlayers();
		for (Player p : plist) { //tisk tisk doing a /reload is not good! but better safe than sorry!
			scVlevel.put(p, getLockManager().getVerbLevel(p.getName()));
		}

		logger.log(Level.INFO, "[SecureChests] "+getDescription().getVersion()+" enabled.");    


	}

	public boolean vaultEnabled() {
		return usingVault;
	}

	public net.milkbowl.vault.permission.Permission getVault() {
		return permission;
	}

	private boolean setupPermissions() {
		if (getServer().getPluginManager().isPluginEnabled("Vault")) {
			RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
			if (permissionProvider != null) {
				permission = permissionProvider.getProvider();
			}
			return (permission != null);
		}
		return false;
	}

	public LockManager getLockManager() {
		return LockManager;
	}
	
    public ChestShop getChestShop() {
    	return chestshop;
    }
    
	private void registerCommands() {
		getCommand("lock").setExecutor(new LockCommand());
		getCommand("unlock").setExecutor(new UnLockCommand());
		getCommand("sc").setExecutor(new SCCommand());
		getCommand("securechest").setExecutor(new SCCommand());
		getCommand("securechests").setExecutor(new SCCommand());
		getCommand("schest").setExecutor(new SCCommand());
	}

	private void metrics() {
		try {
			MetricsSC metrics = new MetricsSC(this);
			metrics.start();
		} catch (IOException e) {
			logger.severe("Problems submitting plugin stats");
		}
	}

	public void reloadPlugin() {
		reloadConfig();
		Config.setup();
		initBlockData();
		logger.log(Level.INFO, "[SecureChests] Reload complete");
	}

	@Override
	public void onDisable() {
		getLockManager().closeConnection();
		logger.log(Level.INFO, "[SecureChests] Disabled."); 
	}

	// will return :
	// 1. exact name if online
	// 2. partial name if online
	// 3. if neither are true then return same name given
	public String myGetPlayerName(String name) { 
		Player caddPlayer = getServer().getPlayerExact(name);
		String pName;
		if(caddPlayer == null) {
			caddPlayer = getServer().getPlayer(name);
			if(caddPlayer == null) {
				pName = name;
			} else {
				pName = caddPlayer.getName();
			}
		} else {
			pName = caddPlayer.getName();
		}
		return pName;
	}

	public static File getFolder() {
		return dataFolder;
	}


}
