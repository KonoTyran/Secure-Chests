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

public class GaddCommand {

	private final SecureChests plugin;

	public GaddCommand() {
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
								ChatColor.AQUA + "/sc " + Config.getLocal(Language.COMMAND_GADD) + " " + Config.getLocal(Language.USERNAME) + ChatColor.WHITE + " "+ Config.getLocal(Language.OR) +" " +
										ChatColor.AQUA + "/sc " + Config.getLocal(Language.COMMAND_GADD) + " c:" + Config.getLocal(Language.CLANTAG) + ChatColor.WHITE + " "+ Config.getLocal(Language.OR) +" " +
										ChatColor.AQUA + "/sc " + Config.getLocal(Language.COMMAND_GADD) + " g:" + Config.getLocal(Language.GROUP) + ChatColor.WHITE
								)
						);
			}
			else
			{
				if (args[0].toLowerCase().startsWith("c:") && plugin.usingSimpleClans) //they want to add a clan not a player
				{ 
					String clanTag = ChatColor.stripColor(args[0].substring(2));
					ClanManager cm = plugin.simpleClans.getClanManager();
					if (cm.existsClanByName(clanTag))
					{
						if (!plugin.getLockManager().clanOnGlobalList(player.getName(), clanTag))
						{
							plugin.getLockManager().addToGlobalList(player.getName(), clanTag, "clan");
							clanTag = cm.getClan(clanTag).getTag();
							plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_GADD_CLAN).replace("%clantag", clanTag + ChatColor.WHITE));
						}
						else
						{
							plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_GADD_CLAN_ALREADY).replace("%clantag", clanTag + ChatColor.WHITE));
						}
					}
					else
					{
						plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.CLAN_NOT_FOUND));
					}
				}
				else if (args[0].toLowerCase().startsWith("c:") && !plugin.usingSimpleClans)
				{
					plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOT_USING_SIMPLE_CLANS));
				}
				else if (args[0].toLowerCase().startsWith("g:") && plugin.vaultEnabled()) //they want to add a clan not a player
				{ 
					String group = ChatColor.stripColor(args[0].substring(2));
					if (!plugin.getLockManager().groupOnGlobalList(player.getName(), group))
					{
					plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_GADD_GROUP).replace("%group", group));
					plugin.getLockManager().addToGlobalList(player.getName(), group, "group");
					}
					else
					{
						plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_GADD_GROUP_ALREADY).replace("%group", group));
					}
				}
				else if (args[0].toLowerCase().startsWith("g:") && !plugin.vaultEnabled())
				{
					plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOT_USING_VAULT));
				}
				else
				{
					String pName = plugin.myGetPlayerName(ChatColor.stripColor(args[0]));

					if (!plugin.getLockManager().playerOnGlobalList(player.getName(), pName)){
						plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_GADD_PLAYER).replace("%username", pName));
						plugin.getLockManager().addToGlobalList(player.getName(), pName, "player");
					} else {
						plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_GADD_PLAYER_ALREADY).replace("%username", pName));
					}
				}
			}
		} else {
			plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.DONT_HAVE_PERMISSION).replace("%permission", Permission.LOCK.toString()));
		}
		return true;
	}

}
