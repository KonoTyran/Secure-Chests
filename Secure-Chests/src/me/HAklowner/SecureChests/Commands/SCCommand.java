package me.HAklowner.SecureChests.Commands;

import java.util.ArrayList;
import java.util.List;

import me.HAklowner.SecureChests.SecureChests;
import me.HAklowner.SecureChests.Utils.GhostPurge;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class SCCommand implements CommandExecutor {

	private final SecureChests plugin;

	public SCCommand() {
		plugin = SecureChests.getInstance();
	}


	private String[] stripFirst(String[] args) {
		List<String> lArgs = new ArrayList<String>();
		for (String s : args) {
			lArgs.add(s);
		}
		lArgs.remove(0);
		String[] newArgs = new String[lArgs.size()];
		return lArgs.toArray(newArgs);
	}

	// command status:
	// 0/null=none
	// 1= lock
	// 2= unlock
	// 3= add to chest access list
	// 4= remove from chest access list
	// 5= add to deny list
	// 6= lock for other (perms already checked).
	// 7= add clan to access list.
	// 8= remove clan from access list.
	// 9= add clan to deny list.
	// 10=toggle public status.

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		} else {
			//console commands
			
			if(args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
				plugin.sendMessage(sender, "==SecureChests help menu==");
				plugin.sendMessage(sender, "sc reload - reload config");
				plugin.sendMessage(sender, "sc upgrade  - upgrades your flatfile to the sql database ###WARNING THIS WILL LAG THE SERVER####");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("reload")) {
				plugin.reloadPlugin();
				plugin.sendMessage(sender, "Config's reloaded.");
				return true;
			} else if (args[0].equalsIgnoreCase("upgrade")) {
				plugin.getLockManager().updateFromFlatFile();
				return true;
			}
				
			plugin.sendMessage(sender, "This Command can only be used by a player");
			return true;
		}

		//display help
		if(args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
			if (args.length == 2) {
				try {
					int page = Integer.parseInt(args[1]);
					plugin.displayHelp(player, page);
					return true;
				} catch (NumberFormatException e) {
					plugin.displayHelp(player);
					return true;
				}
			}
			plugin.displayHelp(player , 0);
			return true;
		}

		//set scCmd to toggle public status
		if (args[0].equalsIgnoreCase("public")) {
			if (sender.hasPermission("securechests.lock.public")) {
				plugin.sendMessage(player, "Now interact with an owned block to toggle its public status.");
				plugin.scCmd.put(player, 10);
				return true;
			} else {
				plugin.sendMessage(player, "You don't have permission to create public chests. (securechests.lock.public)");
				return true;
			}
		}

		//reload plugin's config files
		if (args[0].equalsIgnoreCase("reload")) {
			if (sender.hasPermission("securechests.admin.reload")) {
				plugin.reloadPlugin();
				plugin.sendMessage(player, "Config's reloaded.");
				return true;
			} else {
				plugin.sendMessage(player, "You don't have permission to reload SecureChests. (securechests.admin.reload)");
				return true;
			}
		}
		
		
		if (args[0].equalsIgnoreCase("purge")) {
			if (sender.hasPermission("securechests.admin.purge")) {
				GhostPurge gp = new GhostPurge();
				gp.purge(player);
				return true;
			} else {
				plugin.sendMessage(player, "You don't have permission to do that. (securechests.admin.purge)");
				return true;
			}
		}
		
		if (args.length == 2 && args[0].equalsIgnoreCase("newowner")) {
			if (sender.hasPermission("securechests.bypass.changeowner")) {
				String pName = plugin.myGetPlayerName(args[1]);
				plugin.sendMessage(player, "Now click on a chest to change ownership to "+ pName);
				plugin.scAList.put(player, pName);
				plugin.scCmd.put(player, 10);
				return true;
			} else {
				plugin.sendMessage(player, "You don't have permission to do that. (securechests.admin.changeowner)");
				return true;
			}
		}

		//send to /lock command
		if (args[0].equalsIgnoreCase("lock")) {
			LockCommand cmd = new LockCommand();
			return cmd.onCommand(sender, command, label, stripFirst(args));
		}

		//send to /unlock command
		if (args[0].equalsIgnoreCase("unlock")) {
			UnLockCommand cmd = new UnLockCommand();
			return cmd.onCommand(sender, command, label, stripFirst(args));
		}

		//send to "add" command manager
		if (args[0].equalsIgnoreCase("add")) {
			AddCommand cmd = new AddCommand();
			return cmd.onCommand(sender, command, label, stripFirst(args));
		}

		//send to "Remove" command manager
		if (args[0].equalsIgnoreCase("remove")) {
			RemoveCommand cmd = new RemoveCommand();
			return cmd.onCommand(sender, command, label, stripFirst(args));
		}

		//send to "Deny" command manager
		if (args[0].equalsIgnoreCase("deny")) {
			DenyCommand cmd = new DenyCommand();
			return cmd.onCommand(sender, command, label, stripFirst(args));
		}

		//send to "Gadd" command manager
		if (args[0].equalsIgnoreCase("gadd")) {
			GaddCommand cmd = new GaddCommand();
			return cmd.onCommand(sender, command, label, stripFirst(args));
		}

		//send to "Gremove" command manager
		if (args[0].equalsIgnoreCase("gremove")) {
			GremoveCommand cmd = new GremoveCommand();
			return cmd.onCommand(sender, command, label, stripFirst(args));
		}

		plugin.sendMessage(player, "Unknown command. type \"/sc help\" for command list.");
		return true;
	}
}