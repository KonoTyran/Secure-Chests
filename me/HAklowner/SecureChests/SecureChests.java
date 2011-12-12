package me.HAklowner.SecureChests;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
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

	public void onEnable() {
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.Normal, this);
		log.info("SecureChests Enabled");

	}

	public void onDisable() {
		log.info("SecureChestsDisabled");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}

		if (cmd.getName().equalsIgnoreCase("lock")){ // If the player typed /basic then do the following...
			if (player == null) {
				sender.sendMessage("this command can only be run by a player");
			} else {
				if (sender.hasPermission("securechests.lock")) {
					sender.sendMessage("Now open/punch a chest to lock it");
					scCmd.put(player, 1);
				} else {
					sender.sendMessage("You dont have permission to lock your chests");
				}
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("unlock")) {
			if (player == null) {
				sender.sendMessage("this command can only be run by a player");
			} else {
				if (sender.hasPermission("securechests.lock")) {
					sender.sendMessage("Now open/punch a chest to unlock it");
					scCmd.put(player, 2);
				} else {
					sender.sendMessage("You dont have permission to lock your chests");
				}
			}
		} else if (cmd.getName().equalsIgnoreCase("sc") || cmd.getName().equalsIgnoreCase("securechests") || cmd.getName().equalsIgnoreCase("securechest")) {
			if (player == null) {
				sender.sendMessage("this command can only be run by a player");
			} else {
				if (args.length == 0) { //get help menu
					sender.sendMessage("======== Secure Chests Help Menu ========");
					if (sender.hasPermission("securechests.lock")) {
						sender.sendMessage("/sc lock (/lock) - lock your chests        ");
						sender.sendMessage("/sc unlock (/unlock) - unlock your chests  ");
						sender.sendMessage("/sc cadd username - Add a user to chest access list  ");
						sender.sendMessage("/sc cremove username - remove a user from chest access list  ");
					} else {
						sender.sendMessage("You dont have access lock your chests! :(");
					}
					sender.sendMessage("=========================================");
				} else if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) { //get help menu
					sender.sendMessage("======== Secure Chests Help Menu ========");
					if (sender.hasPermission("securechests.lock")) {
						sender.sendMessage("/sc lock (/lock) - lock your chests        ");
						sender.sendMessage("/sc unlock (/unlock) - unlock your chests  ");
						sender.sendMessage("/sc cadd username - Add a user to chest access list  ");
						sender.sendMessage("/sc cremove username - remove a user from chest access list  ");
					} else {
						sender.sendMessage("You dont have access lock your chests! :(");
					}
					sender.sendMessage("=========================================");
				} else if (args[0].equalsIgnoreCase("lock")) { // Code to activate locking mode.
					if (sender.hasPermission("securechests.lock")) {
						sender.sendMessage("Now open/punch a chest to lock it");
						scCmd.put(player, 1);
					} else {
						sender.sendMessage("You dont have permission to lock your chests");
					}
				} else if (args[0].equalsIgnoreCase("unlock")) {
					if (sender.hasPermission("securechests.lock")) {
						sender.sendMessage("Now open/punch a chest to unlock it");
						scCmd.put(player, 2);
					} else {
						sender.sendMessage("You dont have permission to lock your chests");
					}
				} else if (args[0].equalsIgnoreCase("cadd")) {
					if (sender.hasPermission("securechests.lock")) {
						if (args.length != 2) {
							sender.sendMessage("Correct command useage: /sc cadd username");
						} else {
							Player caddPlayer = getServer().getPlayer(args[1]);
							String pName;
							if(caddPlayer == null) {
								caddPlayer = getServer().getPlayerExact(args[1]);
								if(caddPlayer == null) {
									sender.sendMessage("player "+args[1]+" has not logged into this server before! unable to add to access list.");
									return false;
								} else {
									pName = caddPlayer.getName();
									sender.sendMessage("will add offline user: " + pName + " to next opened chest you own.");
									
								}
							} else {
								pName = caddPlayer.getName();
								sender.sendMessage("will add user: " + pName + " to next opened chest you own.");
							}
							scAList.put(player , pName);
							scCmd.put(player, 3);
						}
					} 
				} else if (args[0].equalsIgnoreCase("cremove")) {
					if (sender.hasPermission("securechests.lock")) {
						if (args.length != 2) {
							sender.sendMessage("Correct command useage: /sc cremove username");
						} else {
							Player caddPlayer = getServer().getPlayer(args[1]);
							String pName;
							if(caddPlayer == null) {
								caddPlayer = getServer().getPlayerExact(args[1]);
								if(caddPlayer == null) {
									sender.sendMessage("player "+args[1]+" has not logged into this server before! unable to add to access list.");
									return false;
								} else {
									pName = caddPlayer.getName();
									sender.sendMessage("will add offline user: " + pName + " to next opened chest you own.");
									
								}
							} else {
								pName = caddPlayer.getName();
								sender.sendMessage("will remove user: " + pName + " to next opened chest you own.");
							}
							scAList.put(player , pName);
							scCmd.put(player, 4);
						}
					} else {
						sender.sendMessage("[Secure Chests] You dont have permission");
					}
				}
			}
		}
		return false;
	}
}
