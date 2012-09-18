package me.HAklowner.SecureChests.Commands;

import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.HAklowner.SecureChests.Permission;
import me.HAklowner.SecureChests.SecureChests;
import me.HAklowner.SecureChests.Config.Config;
import me.HAklowner.SecureChests.Config.Language;
import me.HAklowner.SecureChests.Utils.Atype;
import me.HAklowner.SecureChests.Utils.Vlevel;

public class RemoveCommand {

	private final SecureChests plugin;

	public RemoveCommand() {
		plugin = SecureChests.getInstance();
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
			sender.sendMessage("[SecureChests] This Command can only be used by a player");
			return true;
		}

		if (Permission.has(player, Permission.LOCK))
		{
			if (args.length != 1)
			{
				plugin.sendMessage(Vlevel.COMMAND, player, 
						Config.getLocal(Language.INVALID_SYNTAX)
						.replace("%command", 
								ChatColor.AQUA + "/sc " + Config.getLocal(Language.COMMAND_REMOVE) + " " + Config.getLocal(Language.USERNAME) + ChatColor.WHITE + " "+ Config.getLocal(Language.OR) +" " +
										ChatColor.AQUA + "/sc " + Config.getLocal(Language.COMMAND_REMOVE) + " c:" + Config.getLocal(Language.CLANTAG) + ChatColor.WHITE + " "+ Config.getLocal(Language.OR) +" " +
										ChatColor.AQUA + "/sc " + Config.getLocal(Language.COMMAND_REMOVE) + " g:" + Config.getLocal(Language.GROUP) + ChatColor.WHITE
								)
						);
			}
			else
			{
				if (args[0].toLowerCase().startsWith("c:")) //they want to add a clan not a player
				{ 
					String clanTag = args[0].substring(2);
					String colorTag = clanTag;
					if (plugin.usingSimpleClans) {
						ClanManager cm = plugin.simpleClans.getClanManager();
						if (cm.isClan(clanTag)) {
							colorTag = cm.getClan(clanTag).getTagLabel();
						}
					}
					plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.INTERACT_REMOVE_CLAN).replace("%clantag", colorTag + ChatColor.WHITE));
					plugin.scCmd.put(player, 4);
					plugin.scAList.put(player, clanTag);
					plugin.scAtype.put(player, Atype.Clan);
				}
				else if (args[0].toLowerCase().startsWith("g:"))
				{
					String group = args[0].substring(2);
					plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.INTERACT_REMOVE_GROUP).replace("%group", group));
					plugin.scCmd.put(player, 4); //value of 4 remove from access list
					plugin.scAList.put(player, group); //inactive clan. pass though with c:clantag
					plugin.scAtype.put(player, Atype.Group);
				}
				else
				{
					String pName = plugin.myGetPlayerName(args[0]);
					plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.INTERACT_REMOVE_PLAYER).replace("%username", pName));
					plugin.scAList.put(player , pName);
					plugin.scCmd.put(player, 4);
					plugin.scAtype.put(player, Atype.Player);
				}
			}
		}
		else
		{
			plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.DONT_HAVE_PERMISSION).replace("%permission", Permission.LOCK.toString()));
		}
		return true;
	}
}
