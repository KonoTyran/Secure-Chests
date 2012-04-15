package me.HAklowner.SecureChests;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SecureChests extends JavaPlugin {

	//ClassListeners
	private final SecureChestsPlayerListener playerListener = new SecureChestsPlayerListener(this);
	private final SecureChestsBlockListener blockListener = new SecureChestsBlockListener(this);
	private final SecureChestsRedstoneListener redstoneListener = new SecureChestsRedstoneListener(this);

	//Define the logger
	Logger log = Logger.getLogger("Minecraft");
	
	
	
	public static final Map<Integer, String> BLOCK_LIST = createMap();
	
	private static Map<Integer, String> createMap() {
		Map<Integer, String> result = new HashMap<Integer, String>();
		result.put(23, "dispenser");
		result.put(54, "chest");
		result.put(61, "furnace");
		result.put(62, "furnace");
		result.put(64, "door");
		result.put(96, "trapdoor");
		result.put(107, "fence gate");
		result.put(117, "potion stand");
		return Collections.unmodifiableMap(result);	
	}
	
	public Map<Integer, Boolean> blockStatus = new HashMap<Integer, Boolean>();
	public Map<Player, Integer> scCmd = new HashMap<Player, Integer>();
	public Map<Player, String> scAList = new HashMap<Player, String>();	

	//begin chest storage config commands

	private FileConfiguration storage = null;
	private File storageConfFile = new File("plugins/SecureChests/", "storage.yml");

	public FileConfiguration getStorageConfig() {
		if (storage == null) {
			reloadStorageConfig();
		}
		return storage;
	}

	public void reloadStorageConfig() {
		storage = YamlConfiguration.loadConfiguration(storageConfFile);
	}
	public void saveStorageConfig() {
		try {
			storage.save(storageConfFile);
		} catch(IOException ex) {
			Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + storageConfFile, ex);
		}
	}

	//end chest storage config commands

	//begin player global access list config commands

	private FileConfiguration aList = null;
	private File aListConfFile = new File("plugins/SecureChests/", "accesslist.yml");

	public FileConfiguration getAListConfig() {
		if (aList == null) {
			reloadAListConfig();
		}
		return aList;
	}

	public void reloadAListConfig() {
		aList = YamlConfiguration.loadConfiguration(aListConfFile);
	}

	public void saveAListConfig() {
		try {
			aList.save(aListConfFile);
		} catch(IOException ex) {
			Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + aListConfFile, ex);
		}
	}
	//end player global access list config commands
	
	public void displayMessage(Player player, String Message) {
		player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]" + ChatColor.WHITE + " " + Message);
	}
	

	public void displayHelp(Player player) {
		player.sendMessage(ChatColor.GOLD + "----- Secure Chests " + getDescription().getVersion() + " -----");
		if (player.hasPermission("securechests.lock")) {
			player.sendMessage(ChatColor.WHITE + "/sc lock (/lock)" + ChatColor.GRAY + " - lock your chests/furnaces/doors/etc...");
			player.sendMessage(ChatColor.WHITE + "/sc unlock (/unlock)" + ChatColor.GRAY + " - unlock your chests/furnaces/doors/etc...");
			player.sendMessage(ChatColor.WHITE + "/sc add username" + ChatColor.GRAY + " - Add a user to container/door access list");
			player.sendMessage(ChatColor.WHITE + "/sc deny username" + ChatColor.GRAY + " - Add a user to chest container/door list (will override global access list)");
			player.sendMessage(ChatColor.WHITE + "/sc remove username" + ChatColor.GRAY + " - remove a user from container/door access list");
			player.sendMessage(ChatColor.WHITE + "/sc gadd username" + ChatColor.GRAY + " - Add a user to your global allow");
			player.sendMessage(ChatColor.WHITE + "/sc gremove username" + ChatColor.GRAY + " - remove user from global allow list");
		} else {
			player.sendMessage(ChatColor.RED + "You dont have access to lock your chests! :(");
		}
		if (player.hasPermission("securechests.reload")) {
			player.sendMessage(ChatColor.WHITE + "/sc reload" + ChatColor.GRAY + " - reload config files");
		}
	}

	public void onEnable() {
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(blockListener, this);
		pm.registerEvents(playerListener, this);
		pm.registerEvents(redstoneListener, this);
		try{     // if config.yml is missing from package, create your own.
			FileConfiguration config = getConfig();
			if(!config.contains("Furnace")){
				config.set("Furnace", true);
			}
			if(!config.contains("Door")){
				config.set("Door", false);
			}
			if(!config.contains("Chest")){
				config.set("Chest", true);
			}    
			if(!config.contains("Dispenser")){
				config.set("Dispenser", false);
			}  
			if(!config.contains("Trapdoor")){
				config.set("Trapdoor", false);
			}  
			if(!config.contains("Potion")){
				config.set("Potion", true);
			}  
			if(!config.contains("Gate")){
				config.set("Gate", true);
			}  
			saveConfig();
		}catch(Exception e1){
			e1.printStackTrace();
		} // END TRY    
		// load / create config
		FileConfiguration cfg = getConfig();
		FileConfigurationOptions cfgOptions = cfg.options();
		cfgOptions.copyDefaults(true);
		cfgOptions.copyHeader(true);
		saveConfig();      
		
		blockStatus.clear(); // clear the enable/disabled for all block during initialization.
		// Get current active block
		for (Integer key : BLOCK_LIST.keySet() ) { //Start with all block default to disabled.
			blockStatus.put(key, false);
		}
		
		if(getConfig().getBoolean("Chest"))
			blockStatus.put(54, true);
		if(getConfig().getBoolean("Furnace")) {
			blockStatus.put(61, true);
			blockStatus.put(62, true);
		}
		if(getConfig().getBoolean("Door"))
			blockStatus.put(64, true);
		if(getConfig().getBoolean("Dispenser"))
			blockStatus.put(23, true);
		if(getConfig().getBoolean("Trapdoor"))
			blockStatus.put(96, true);
		if(getConfig().getBoolean("Gate"))
			blockStatus.put(107, true);
		if(getConfig().getBoolean("Potion"))
			blockStatus.put(117, true);
		
		// log initilization and continue
		log.info("[" + getDescription().getName() + "] " + getDescription().getVersion() + " enabled.");    
	}
	
	private void reloadPlugin() {
    	reloadAListConfig();
    	reloadStorageConfig();
    	reloadConfig();
		blockStatus.clear(); // clear the enable/disabled for all block during initialization.
		// Get current active block
		for (Integer key : BLOCK_LIST.keySet() ) { //Start with all block default to disabled.
			blockStatus.put(key, false);
		}
		
		if(getConfig().getBoolean("Chest"))
			blockStatus.put(54, true);
		if(getConfig().getBoolean("Furnace")) {
			blockStatus.put(61, true);
			blockStatus.put(62, true);
		}
		if(getConfig().getBoolean("Door"))
			blockStatus.put(64, true);
		if(getConfig().getBoolean("Dispenser"))
			blockStatus.put(23, true);
		if(getConfig().getBoolean("Trapdoor"))
			blockStatus.put(96, true);
		if(getConfig().getBoolean("Gate"))
			blockStatus.put(107, true);
		if(getConfig().getBoolean("Potion"))
			blockStatus.put(117, true);
    	log.info("[" + getDescription().getName() + "] Reload complete");
	}
	
	public void onDisable() {
		//saveStorageConfig();
		//saveAListConfig();
		//saveConfig();
		log.info("[" + getDescription().getName() + "] " + getDescription().getVersion() + " Disabled."); 
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
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}

		if (cmd.getName().equalsIgnoreCase("lock")){ // If the player typed /basic then do the following...
			if (player == null) {
				sender.sendMessage("this command can only be run by a player.");
			} else {
				if(args.length == 1) {
					if (sender.hasPermission("securechests.bypass.lock")) {
						String pName = myGetPlayerName(args[0]);
						displayMessage(player, "Now interact with a container/door to lock it for "+pName+".");
						scAList.put(player, pName);
						scCmd.put(player, 6);
					} else {
						displayMessage(player, "You dont have permission to lock other's Blocks!");
					}
				} else if (args.length == 0 && sender.hasPermission("securechests.lock")) {
					displayMessage(player, "Now interact with a container/door to lock it.");
					scCmd.put(player, 1);
				} else {
					displayMessage(player, "You dont have permission to lock this!");
				}
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("unlock")) {
				if (player == null) {
					displayMessage(player, "this command can only be run by a player.");
				} else {
					if (sender.hasPermission("securechests.lock")) {
						displayMessage(player, "Now interact with a container/door to unlock it.");
						scCmd.put(player, 2);
					} else {
						displayMessage(player, "You dont have permission to lock your Blocks!");
					}
				}
		} else if (cmd.getName().equalsIgnoreCase("sc") || cmd.getName().equalsIgnoreCase("securechests") || cmd.getName().equalsIgnoreCase("securechest")) {
			if (player == null) {
				if(args[0].equalsIgnoreCase("reload") ) {
					reloadPlugin();
				} else {
					displayMessage(player, "this command can only be run by a player.");
				}
			} else {
				if(args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) { //get help menu
					displayHelp(player);
				} else if (args[0].equalsIgnoreCase("lock") && args.length == 1) { // Code to activate locking mode.
					if (sender.hasPermission("securechests.lock")) {
						displayMessage(player, "Now interact with a container/door to lock it.");
						scCmd.put(player, 1);
					} else {
						displayMessage(player, "You dont have permission to lock your chests.");
					}
				} else if(args[0].equalsIgnoreCase("lock") && args.length == 2) {
					if (sender.hasPermission("securechests.bypass.lock")) {
						String pName = myGetPlayerName(args[1]);
						displayMessage(player, "Now interact with a container/door to lock it for " + pName);
						scAList.put(player, pName);
						scCmd.put(player, 6);				 
					} else {					
						displayMessage(player, "You dont have permission to lock other's Blocks!");
					}
	        } else if (args[0].equalsIgnoreCase("unlock")) { // UNLOCK!
	        	if (sender.hasPermission("securechests.lock")) {
	        		displayMessage(player, "Now interact with a container/door to unlock it.");
	        		scCmd.put(player, 2);
        		} else {
	        		displayMessage(player, "You dont have permission to lock your Blocks!");
        		}
	        } else if (args[0].equalsIgnoreCase("add")) {  //Add player to chest access list.
	        	if (sender.hasPermission("securechests.lock")) {
	        		if (args.length != 2) {
	        			displayMessage(player, "Correct command useage: /sc add username");
	        		} else {
	        			String pName = myGetPlayerName(args[1]);
	        			displayMessage(player, "will add user " + pName + " to the next owned block you interact with.");
	        			scAList.put(player , pName);
	        			scCmd.put(player, 3);
	        		}
	        	} 
	        } else if (args[0].equalsIgnoreCase("remove")) { // Remove player from chest access list
	        	if (sender.hasPermission("securechests.lock")) {
	        		if (args.length != 2) {
	        			displayMessage(player, "Correct command useage: /sc remove username");
	        		} else {
	        			String pName = myGetPlayerName(args[1]);
	        			displayMessage(player, "will remove user " + pName + " from the next owned block you interact with.");
	        			scAList.put(player , pName);
	        			scCmd.put(player, 4);
	        		}
	        	} else {
	        		displayMessage(player, "You dont have permission.");
	        	}
	        } else if (args[0].equalsIgnoreCase("deny")) { // Remove player from chest access list
	        	if (sender.hasPermission("securechests.lock")) {
	        		if (args.length != 2) {
	        			displayMessage(player, "Correct command useage: /sc deny username");
	        		} else {
	        			String pName = myGetPlayerName(args[1]);
	        			displayMessage(player, "will add user " + pName + " to the deny list of the next owned block you interact with.");
	        			scAList.put(player , pName);
	        			scCmd.put(player, 5);
	        		}
	        	} else {
	        		displayMessage(player, "You dont have permission.");
	        	}
	        } else if (args[0].equalsIgnoreCase("gadd")) { //Add to global access list!
	        	if (sender.hasPermission("securechests.lock")) {
	        		if (args.length != 2) {
	        			displayMessage(player, "Correct command useage: /sc gadd username");
	        		} else {
	        			String pName = myGetPlayerName(args[1]);

	        			if (!getAListConfig().getBoolean(sender.getName()+"." + pName)){
	        				displayMessage(player, "Adding " + pName + " to your global allow list.");
	        				getAListConfig().set(sender.getName()+"." + pName, true);
	        				saveAListConfig();
	        			} else {
	        				displayMessage(player, "Player "+pName+" already in access list.");
	        			}
	        		}
	        	} else {
	        		displayMessage(player, "You dont have permission");
	        	}
	        } else if (args[0].equalsIgnoreCase("gremove")) { //Add to global access list!
	        	if (sender.hasPermission("securechests.lock")) {
	        		if (args.length != 2) {
	        			displayMessage(player, "Correct command useage: /sc gremove username");
	        		} else {
	        			String pName = myGetPlayerName(args[1]);
	        			if (!getAListConfig().getBoolean(sender.getName()+"." + pName)){
	        				displayMessage(player, "Player " + pName + " Not on your global access list");
	        			} else {
	        				getAListConfig().set(sender.getName()+"." + pName, null);
	        				saveAListConfig();
	        				displayMessage(player, "Player "+pName+" Removed from your global access list.");
	        			}
	        		}
	        	} else {
	        		displayMessage(player, "You dont have permission");
	        	}
	        } else if (args[0].equalsIgnoreCase("reload") && player.hasPermission("securechests.reload")) {
	        	reloadPlugin();
	        	displayMessage(player, "Config Reloaded.");
	        } else {
	        	displayMessage(player, "Unknown command. type \"/sc help\" for command list.");
	        }
			}//End command checks!
		}
		return false;
	}
}
