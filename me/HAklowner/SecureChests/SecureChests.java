package me.HAklowner.SecureChests;

import java.io.File;
import java.io.IOException;
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

	//Define the logger
	Logger log = Logger.getLogger("Minecraft");


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

	public void displayHelp(Player player) {
		player.sendMessage("======== Secure Chests Help Menu ========");
		if (player.hasPermission("securechests.lock")) {
			player.sendMessage("/sc lock (/lock) - lock your chests");
			player.sendMessage("/sc unlock (/unlock) - unlock your chests");
			player.sendMessage("/sc add username - Add a user to chest access list");
			player.sendMessage("/sc deny username - Add a user to chest deny list (will override global access list)");
			player.sendMessage("/sc gadd username - Add a user to your global allow");
			player.sendMessage("/sc gremove username - remove user from global allow list");
			player.sendMessage("/sc remove username - remove a user from chest access list");
		} else {
			player.sendMessage("You dont have access lock your chests! :(");
		}
		if (player.hasPermission("securechests.reload")) {
			player.sendMessage("/sc reload - reload config files");
		}
		player.sendMessage("=======================================");
	}
	
	public void onEnable() {
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(blockListener, this);
		pm.registerEvents(playerListener, this);

    try{     // if config.yml is missing from package, create your own.
      FileConfiguration config = getConfig();
      File SecureChests = new File(getDataFolder(),"config.yml");
      SecureChests.mkdir();
      if(!config.contains("Furnace")){
        config.set("Furnace", false);
      }
      if(!config.contains("Door")){
        config.set("Door", false);
      }    
      saveConfig();
    }catch(Exception e1){
      e1.printStackTrace();
    } // END TRY
    
    FileConfiguration cfg = getConfig();
    FileConfigurationOptions cfgOptions = cfg.options();
    cfgOptions.copyDefaults(true);
    cfgOptions.copyHeader(true);
    saveConfig();
    
    log.info("SecureChests Enabled");
	}
		
	public void onDisable() {
		log.info("SecureChestsDisabled");
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
				sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" this command can only be run by a player");
			} else {
				if (sender.hasPermission("securechests.lock")) {
					sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Now interact with a chest to lock it");
					scCmd.put(player, 1);
				} else {
					sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" You dont have permission to lock your chests");
				}
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("unlock")) {
			if (player == null) {
				sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" this command can only be run by a player");
			} else {
				if (sender.hasPermission("securechests.lock")) {
					sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Now interact with a chest to unlock it");
					scCmd.put(player, 2);
				} else {
					sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" You dont have permission to lock your chests");
				}
			}
		} else if (cmd.getName().equalsIgnoreCase("sc") || cmd.getName().equalsIgnoreCase("securechests") || cmd.getName().equalsIgnoreCase("securechest")) {
			if (player == null) {
				sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" this command can only be run by a player");
			} else {
				if(args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) { //get help menu
					displayHelp(player);
				} else if (args[0].equalsIgnoreCase("lock")) { // Code to activate locking mode.
					if (sender.hasPermission("securechests.lock")) {
						sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Now interact with a chest to lock it");
						scCmd.put(player, 1);
					} else {
						sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" You dont have permission to lock your chests.");
					}
				} else if (args[0].equalsIgnoreCase("unlock")) { // UNLOCK!
					if (sender.hasPermission("securechests.lock")) {
						sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Now interact with a chest to unlock it.");
						scCmd.put(player, 2);
					} else {
						sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" You dont have permission to lock your chests.");
					}
				} else if (args[0].equalsIgnoreCase("add")) {  //Add player to chest access list.
					if (sender.hasPermission("securechests.lock")) {
						if (args.length != 2) {
							sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Correct command useage: /sc add username");
						} else {
							String pName = myGetPlayerName(args[1]);
							sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" will add user " + pName + " to the next owned chest you interact with.");
							scAList.put(player , pName);
							scCmd.put(player, 3);
						}
					} 
				} else if (args[0].equalsIgnoreCase("remove")) { // Remove player from chest access list
					if (sender.hasPermission("securechests.lock")) {
						if (args.length != 2) {
							sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Correct command useage: /sc remove username");
						} else {
							String pName = myGetPlayerName(args[1]);
							sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" will remove user " + pName + " from the next owned chest you interact with.");
							scAList.put(player , pName);
							scCmd.put(player, 4);
						}
					} else {
						sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" You dont have permission.");
					}
				} else if (args[0].equalsIgnoreCase("deny")) { // Remove player from chest access list
					if (sender.hasPermission("securechests.lock")) {
						if (args.length != 2) {
							sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Correct command useage: /sc deny username");
						} else {
							String pName = myGetPlayerName(args[1]);
							sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" will add user " + pName + " to the deny list of the next owned chest you interact with.");
							scAList.put(player , pName);
							scCmd.put(player, 5);
						}
					} else {
						sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" You dont have permission.");
					}
				} else if (args[0].equalsIgnoreCase("gadd")) { //Add to global access list!
					if (sender.hasPermission("securechests.lock")) {
						if (args.length != 2) {
							sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Correct command useage: /sc gadd username");
						} else {
							String pName = myGetPlayerName(args[1]);
							
							if (!getAListConfig().getBoolean(sender.getName()+"." + pName)){
								sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Adding " + pName + " to your global allow list.");
								getAListConfig().set(sender.getName()+"." + pName, true);
								saveAListConfig();
							} else {
								player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Player "+pName+" already in access list.");
							}
						}
					} else {
						sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" You dont have permission");
					}
				} else if (args[0].equalsIgnoreCase("gremove")) { //Add to global access list!
					if (sender.hasPermission("securechests.lock")) {
						if (args.length != 2) {
							sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Correct command useage: /sc gremove username");
						} else {
							String pName = myGetPlayerName(args[1]);
							if (!getAListConfig().getBoolean(sender.getName()+"." + pName)){
								sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Player " + pName + " Not on your global access list");
							} else {
								getAListConfig().set(sender.getName()+"." + pName, null);
								saveAListConfig();
								player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Player "+pName+" Removed from your global access list.");
							}
						}
					} else {
						sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" You dont have permission");
					}
				} else if (args[0].equalsIgnoreCase("reload")) {
					reloadAListConfig();
					reloadStorageConfig();
					reloadConfig();
					sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Reload complete");
				} else {
					sender.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" unknown command. type \"/sc help\" for command list.");
				}
			}//End command checks!
		}
		return false;
	}
}
