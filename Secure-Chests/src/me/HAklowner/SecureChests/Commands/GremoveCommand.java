package me.HAklowner.SecureChests.Commands;

import com.p000ison.dev.simpleclans2.api.clan.ClanManager;
import com.p000ison.dev.simpleclans2.api.clanplayer.ClanPlayer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.HAklowner.SecureChests.Permission;
import me.HAklowner.SecureChests.SecureChests;
import me.HAklowner.SecureChests.Config.Config;
import me.HAklowner.SecureChests.Config.Language;
import me.HAklowner.SecureChests.Utils.Vlevel;

public class GremoveCommand {

	private final SecureChests plugin;

	public GremoveCommand() {
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

		if (Permission.has(player, Permission.LOCK)) {
			if (args.length != 1)
			{
				plugin.sendMessage(Vlevel.COMMAND, player, 
						Config.getLocal(Language.INVALID_SYNTAX)
						.replace("%command", 
								ChatColor.AQUA + "/sc " + Config.getLocal(Language.COMMAND_GREMOVE) + " " + Config.getLocal(Language.USERNAME) + ChatColor.WHITE + " "+ Config.getLocal(Language.OR) +" " +
										ChatColor.AQUA + "/sc " + Config.getLocal(Language.COMMAND_GREMOVE) + " c:" + Config.getLocal(Language.CLANTAG) + ChatColor.WHITE + " "+ Config.getLocal(Language.OR) +" " +
										ChatColor.AQUA + "/sc " + Config.getLocal(Language.COMMAND_GREMOVE) + " g:" + Config.getLocal(Language.GROUP) + ChatColor.WHITE
								)
						);
			}
			else if (args[0].toLowerCase().startsWith("c:")) { //they want to add a clan not a player
				String clanTag = ChatColor.stripColor(args[0].substring(2).toLowerCase());
				String colorTag = clanTag;
				if (plugin.usingSimpleClans)
				{
					ClanManager cm = plugin.simpleClans.getClanManager();
					if (cm.existsClanByName(clanTag))
					{
						colorTag = cm.getClan(clanTag).getTag();

					}
				}

				if (plugin.getLockManager().clanOnGlobalList(player.getName(), clanTag)){
					plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_GREMOVE_CLAN).replace("%clantag", colorTag + ChatColor.WHITE));
					plugin.getLockManager().removeFromGlobalList(player.getName(), clanTag, "clan");
					
				}
				else
				{
					plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_GREMOVE_CLAN_NONE).replace("%clantag", colorTag + ChatColor.WHITE));
				}
			}
			else if (args[0].toLowerCase().startsWith("g:") && plugin.vaultEnabled()) //remove a group
			{ 
				String group = args[0].substring(2);
				if (plugin.getLockManager().groupOnGlobalList(player.getName(), group))
				{
					plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_GREMOVE_GROUP).replace("%group", group));
					plugin.getLockManager().removeFromGlobalList(player.getName(), group, "group");
				}
				else
				{
					plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_GREMOVE_GROUP_NONE).replace("%group", group));
				}
			} else {
				String pName = plugin.myGetPlayerName(args[0]);
				if (plugin.getLockManager().playerOnGlobalList(player.getName(), pName)){
					plugin.getLockManager().removeFromGlobalList(player.getName(), pName, "player");
					plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_GREMOVE_PLAYER).replace("%username", pName));

				} else {
					plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_GREMOVE_PLAYER_NONE).replace("%username", pName));
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
