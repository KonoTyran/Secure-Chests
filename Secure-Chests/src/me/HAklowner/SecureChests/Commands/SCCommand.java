package me.HAklowner.SecureChests.Commands;

import java.util.ArrayList;
import java.util.List;

import me.HAklowner.SecureChests.SecureChests;
import me.HAklowner.SecureChests.Permission;
import me.HAklowner.SecureChests.Config.Config;
import me.HAklowner.SecureChests.Config.Language;
import me.HAklowner.SecureChests.Utils.Verblevel;
import me.HAklowner.SecureChests.Utils.Vlevel;

import org.bukkit.ChatColor;
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
	// 11=newowner command
	// 12=info command.

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = null;
		if (sender instanceof Player)
		{
			player = (Player) sender;
		}
		else
		{
			//console commands

			if(args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?"))
			{
				plugin.sendMessage(Vlevel.CONSOLE, sender, "==SecureChests help menu==");
				plugin.sendMessage(Vlevel.CONSOLE, sender, "sc reload - reload config");
				plugin.sendMessage(Vlevel.CONSOLE, sender, "sc upgrade  - upgrades your flatfile to the sql database ###WARNING THIS WILL LAG THE SERVER####");
				return true;
			}

			if(args[0].equalsIgnoreCase("reload"))
			{
				plugin.reloadPlugin();
				plugin.sendMessage(Vlevel.CONSOLE, sender, "Config's reloaded.");
				return true;
			}
			else if (args[0].equalsIgnoreCase("upgrade"))
			{
				plugin.getLockManager().updateFromFlatFile();
				return true;
			}
			else if (args[0].equalsIgnoreCase("purge"))
			{
				plugin.getLockManager().purgeGhostEntry(true);
				return true;
			}

			plugin.sendMessage(Vlevel.CONSOLE, sender, "This Command can only be used by a player");
			return true;
		}

		//display help
		if(args.length == 0 || args[0].equalsIgnoreCase(Config.getLocal(Language.COMMAND_HELP)) || args[0].equalsIgnoreCase("?"))
		{
			if (args.length == 2)
			{
				try
				{
					int page = Integer.parseInt(args[1]);
					plugin.displayHelp(player, page);
					return true;
				}
				catch (NumberFormatException e)
				{
					plugin.displayHelp(player);
					return true;
				}
			}
			plugin.displayHelp(player , 0);
			return true;
		}

		//set Vlevel
		if (args[0].equalsIgnoreCase(Config.getLocal(Language.COMMAND_NOTICE)))
		{
			if (args.length == 3)
			{

				Verblevel vl = plugin.scVlevel.get(player);

				if (args[1].equalsIgnoreCase(Config.getLocal(Language.COMMAND_OWN)))
				{
					if (args[2].equalsIgnoreCase(Config.getLocal(Language.COMMAND_ON)) || args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("yes"))
					{
						vl.setOwn(true);
						plugin.getLockManager().saveVerbLevel(vl, player.getName());
						plugin.scVlevel.put(player, vl);
						plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.TOGGLE_OWN).replace("%toggle", ChatColor.GREEN+Config.getLocal(Language.COMMAND_ON)+ChatColor.WHITE));
						return true;
					}
					else if (args[2].equalsIgnoreCase(Config.getLocal(Language.COMMAND_OFF)) || args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("no"))
					{
						vl.setOwn(false);
						plugin.getLockManager().saveVerbLevel(vl, player.getName());
						plugin.scVlevel.put(player, vl);
						plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.TOGGLE_OWN).replace("%toggle", ChatColor.RED+Config.getLocal(Language.COMMAND_OFF)+ChatColor.WHITE));
						return true;
					}
				}
				else if (args[1].equalsIgnoreCase(Config.getLocal(Language.COMMAND_OTHER)))
				{
					if (args[2].equalsIgnoreCase(Config.getLocal(Language.COMMAND_ON)) || args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("yes"))
					{
						vl.setOther(true);
						plugin.getLockManager().saveVerbLevel(vl, player.getName());
						plugin.scVlevel.put(player, vl);
						plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.TOGGLE_OTHER).replace("%toggle", ChatColor.GREEN+Config.getLocal(Language.COMMAND_ON)+ChatColor.WHITE));
						return true;
					}
					else if (args[2].equalsIgnoreCase(Config.getLocal(Language.COMMAND_OFF)) || args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("no"))
					{
						vl.setOther(false);
						plugin.getLockManager().saveVerbLevel(vl, player.getName());
						plugin.scVlevel.put(player, vl);
						plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.TOGGLE_OTHER).replace("%toggle", ChatColor.RED+Config.getLocal(Language.COMMAND_OFF)+ChatColor.WHITE));
						return true;
					}
				}
				else if (args[1].equalsIgnoreCase(Config.getLocal(Language.COMMAND_OVERRIDE)))
				{
					if (args[2].equalsIgnoreCase(Config.getLocal(Language.COMMAND_ON)) || args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("yes"))
					{
						vl.setOverride(true);
						plugin.getLockManager().saveVerbLevel(vl, player.getName());
						plugin.scVlevel.put(player, vl);
						plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.TOGGLE_OVERRIDE).replace("%toggle", ChatColor.GREEN+Config.getLocal(Language.COMMAND_ON)+ChatColor.WHITE));
						return true;
					}
					else if (args[2].equalsIgnoreCase(Config.getLocal(Language.COMMAND_OFF)) || args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("no"))
					{
						vl.setOverride(false);
						plugin.getLockManager().saveVerbLevel(vl, player.getName());
						plugin.scVlevel.put(player, vl);
						plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.TOGGLE_OVERRIDE).replace("%toggle", ChatColor.RED+Config.getLocal(Language.COMMAND_OFF)+ChatColor.WHITE));
						return true;
					}
				}
				else if (args[1].equalsIgnoreCase("debug") && Permission.has(player, Permission.DEBUG))
				{
					if (args[2].equalsIgnoreCase(Config.getLocal(Language.COMMAND_ON)) || args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("yes"))
					{
						vl.setDebug(true);
						plugin.getLockManager().saveVerbLevel(vl, player.getName());
						plugin.scVlevel.put(player, vl);
						plugin.sendMessage(Vlevel.COMMAND, player, "Debug Notifications turned "+ChatColor.GREEN+Config.getLocal(Language.COMMAND_ON));
						return true;
					}
					else if (args[2].equalsIgnoreCase(Config.getLocal(Language.COMMAND_OFF)) || args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("no"))
					{
						vl.setDebug(false);
						plugin.getLockManager().saveVerbLevel(vl, player.getName());
						plugin.scVlevel.put(player, vl);
						plugin.sendMessage(Vlevel.COMMAND, player, "Debug Notifications turned "+ChatColor.RED+Config.getLocal(Language.COMMAND_OFF));
						return true;
					}
				}
				else if (args[1].equalsIgnoreCase(Config.getLocal(Language.COMMAND_DENY)))
				{
					if (args[2].equalsIgnoreCase(Config.getLocal(Language.COMMAND_ON)) || args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("yes"))
					{
						vl.setDeny(true);
						plugin.getLockManager().saveVerbLevel(vl, player.getName());
						plugin.scVlevel.put(player, vl);
						plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.TOGGLE_DENY).replace("%toggle", ChatColor.GREEN+Config.getLocal(Language.COMMAND_ON)+ChatColor.WHITE));
						return true;
					}
					else if (args[2].equalsIgnoreCase(Config.getLocal(Language.COMMAND_OFF)) || args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("no"))
					{
						vl.setDeny(false);
						plugin.getLockManager().saveVerbLevel(vl, player.getName());
						plugin.scVlevel.put(player, vl);
						plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.TOGGLE_DENY).replace("%toggle", ChatColor.RED+Config.getLocal(Language.COMMAND_OFF)+ChatColor.WHITE));
						return true;
					}
				}
			}

			plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.INVALID_SYNTAX).replace("%command", ChatColor.AQUA + "/sc " + Config.getLocal(Language.COMMAND_NOTICE) +" "+ Config.getLocal(Language.COMMAND_PARAM) + " " + Config.getLocal(Language.COMMAND_ON) + "/" + Config.getLocal(Language.COMMAND_OFF)));
			if (plugin.scVlevel.get(player).getOwn())
				plugin.sendMessage(Vlevel.COMMAND, player, ChatColor.GREEN + Config.getLocal(Language.COMMAND_OWN) +" "+ChatColor.WHITE+"- " + Config.getLocal(Language.NOTICE_OWN));

			else
				plugin.sendMessage(Vlevel.COMMAND, player, ChatColor.RED + Config.getLocal(Language.COMMAND_OWN) +" "+ChatColor.WHITE+"- "+ Config.getLocal(Language.NOTICE_OWN));

			if (plugin.scVlevel.get(player).getOther())
				plugin.sendMessage(Vlevel.COMMAND, player, ChatColor.GREEN + Config.getLocal(Language.COMMAND_OTHER) +" "+ChatColor.WHITE+"- " + Config.getLocal(Language.NOTICE_OTHER));

			else
				plugin.sendMessage(Vlevel.COMMAND, player, ChatColor.RED + Config.getLocal(Language.COMMAND_OTHER) +" "+ChatColor.WHITE+"- " + Config.getLocal(Language.NOTICE_OTHER));

			if (plugin.scVlevel.get(player).getDeny())
				plugin.sendMessage(Vlevel.COMMAND, player, ChatColor.GREEN + Config.getLocal(Language.COMMAND_DENY) +" "+ChatColor.WHITE+"- " + Config.getLocal(Language.NOTICE_DENY));

			else
				plugin.sendMessage(Vlevel.COMMAND, player, ChatColor.RED + Config.getLocal(Language.COMMAND_DENY) +" "+ChatColor.WHITE+"- " + Config.getLocal(Language.NOTICE_DENY));

			if(plugin.scVlevel.get(player).getOverride())
				plugin.sendMessage(Vlevel.COMMAND, player, ChatColor.GREEN + Config.getLocal(Language.COMMAND_OVERRIDE) +" "+ChatColor.WHITE+"- " + Config.getLocal(Language.NOTICE_OVERRIDE));

			else
				plugin.sendMessage(Vlevel.COMMAND, player, ChatColor.RED + Config.getLocal(Language.COMMAND_OVERRIDE) +" "+ChatColor.WHITE+"- " + Config.getLocal(Language.NOTICE_OVERRIDE));

			if (Permission.has(player, Permission.DEBUG))
				if (plugin.scVlevel.get(player).getDebug())
					plugin.sendMessage(Vlevel.COMMAND, player, ChatColor.GREEN + "Debug "+ChatColor.WHITE+"- Debug messages");

				else
					plugin.sendMessage(Vlevel.COMMAND, player, ChatColor.RED + "Debug "+ChatColor.WHITE+"- Debug messages");
			return true;
		}


		//info tools!
		if (args[0].equalsIgnoreCase(Config.getLocal(Language.COMMAND_INFO)))
		{
			plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.INTERACT_INFO));
			plugin.scCmd.put(player, 12);
			return true;
		}

		if (args[0].equalsIgnoreCase(Config.getLocal(Language.COMMAND_GINFO)))
		{
			GinfoCommand cmd = new GinfoCommand();
			return cmd.onCommand(sender, command, label, stripFirst(args));
		}

		//set scCmd to toggle public status
		if (args[0].equalsIgnoreCase(Config.getLocal(Language.COMMAND_PUBLIC)))
		{
			if (Permission.has(player, Permission.LOCK_PUBLIC))
			{
				plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.INTEARCT_PUBLIC));
				plugin.scCmd.put(player, 10);
				return true;
			}
			else
			{
				plugin.sendMessage(Vlevel.COMMAND,player, Config.getLocal(Language.DONT_HAVE_PERMISSION).replace("%permission", Permission.LOCK_PUBLIC.toString()));
				return true;
			}
		}

		//reload plugin's config files
		if (args[0].equalsIgnoreCase(Config.getLocal(Language.COMMAND_RELOAD)))
		{
			if (Permission.has(player, Permission.ADMIN_RELOAD))
			{
				plugin.reloadPlugin();
				plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.CONFIG_RELOAD));
				return true;
			}
			else
			{
				plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.DONT_HAVE_PERMISSION).replace("%permission", Permission.ADMIN_RELOAD.toString()));
				return true;
			}
		}


		if (args[0].equalsIgnoreCase(Config.getLocal(Language.COMMAND_PURGE)))
		{
			if (Permission.has(player, Permission.ADMIN_PURGE))
			{
				plugin.getLockManager().purgeGhostEntry(player);
				return true;
			}
			else
			{
				plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.DONT_HAVE_PERMISSION).replace("%permission", Permission.ADMIN_PURGE.toString()));
				return true;
			}
		}

		if (args[0].equalsIgnoreCase("deletep") || args[0].equalsIgnoreCase("dp") || args[0].equalsIgnoreCase(Config.getLocal(Language.COMMAND_DELETE_PLAYER)))
		{
			if (Permission.has(player, Permission.ADMIN_DELETE_PLAYER))
			{
				if (args.length == 2)
				{
					plugin.getLockManager().purgePlayer(plugin.myGetPlayerName(args[1]), player);
				}
				else
				{
					plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.INVALID_SYNTAX).replace("%command", "/sc " + Config.getLocal(Language.COMMAND_DELETE_PLAYER) + " ("+Config.getLocal(Language.USERNAME)+")"));
				}
				return true;
			}
			else
			{
				plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.DONT_HAVE_PERMISSION).replace("%permission", Permission.ADMIN_DELETE_PLAYER.toString()));
				return true;
			}
		}

		if (args[0].equalsIgnoreCase(Config.getLocal(Language.COMMAND_TRANSFER)))
		{
			if (Permission.has(player, Permission.ADMIN_TRANSFER))
			{
				if (args.length == 2)
				{
					String pName = plugin.myGetPlayerName(args[1]);
					plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.INTERACT_CHANGE_OWNERSHIP).replace("%username", pName));
					plugin.scAList.put(player, pName);
					plugin.scCmd.put(player, 11);
				}
				else
				{
					plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.INVALID_SYNTAX).replace("%command", "/sc " + Config.getLocal(Language.COMMAND_TRANSFER) + " ("+Config.getLocal(Language.USERNAME)+")"));
				}
				return true;
			}
			else
			{
				plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.DONT_HAVE_PERMISSION).replace("%permission", Permission.ADMIN_TRANSFER.toString()));
				return true;
			}
		}

		//send to /lock command
		if (args[0].equalsIgnoreCase(Config.getLocal(Language.COMMAND_LOCK)))
		{
			return new LockCommand().onCommand(sender, command, label, stripFirst(args));
		}

		//send to /unlock command
		if (args[0].equalsIgnoreCase(Config.getLocal(Language.COMMAND_UNLOCK)))
		{
			return new UnLockCommand().onCommand(sender, command, label, stripFirst(args));
		}

		//send to "add" command manager
		if (args[0].equalsIgnoreCase(Config.getLocal(Language.COMMAND_ADD)))
		{
			return new AddCommand().onCommand(sender, command, label, stripFirst(args));
		}

		//send to "Remove" command manager
		if (args[0].equalsIgnoreCase(Config.getLocal(Language.COMMAND_REMOVE)))
		{
			return new RemoveCommand().onCommand(sender, command, label, stripFirst(args));
		}

		//send to "Deny" command manager
		if (args[0].equalsIgnoreCase(Config.getLocal(Language.COMMAND_DENY)))
		{
			return new DenyCommand().onCommand(sender, command, label, stripFirst(args));
		}

		//send to "Gadd" command manager
		if (args[0].equalsIgnoreCase(Config.getLocal(Language.COMMAND_GADD)))
		{
			return new GaddCommand().onCommand(sender, command, label, stripFirst(args));
		}

		//send to "Gremove" command manager
		if (args[0].equalsIgnoreCase(Config.getLocal(Language.COMMAND_GREMOVE)))
		{
			return new GremoveCommand().onCommand(sender, command, label, stripFirst(args));
		}

		plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.UNKNOWN_COMMAND).replace("%command", "/sc " + Config.getLocal(Language.COMMAND_HELP)));
		
		return true;
	}
}