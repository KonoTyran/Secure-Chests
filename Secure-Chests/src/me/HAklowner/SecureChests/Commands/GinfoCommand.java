package me.HAklowner.SecureChests.Commands;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.HAklowner.SecureChests.Permission;
import me.HAklowner.SecureChests.SecureChests;
import me.HAklowner.SecureChests.Config.Config;
import me.HAklowner.SecureChests.Config.Language;
import me.HAklowner.SecureChests.Utils.Atype;
import me.HAklowner.SecureChests.Utils.Vlevel;
import net.sacredlabyrinth.phaed.simpleclans.Clan;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GinfoCommand
{
	private final SecureChests plugin;

	GinfoCommand()
	{
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

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Player player = null;
		if (sender instanceof Player)
		{
			player = (Player) sender;
		}
		else
		{
			plugin.sendMessage(Vlevel.CONSOLE, sender, "This command can only be run by players.");
			return true;
		}

		String pname = player.getName();
		if (args.length == 0)
		{
			pname = plugin.myGetPlayerName(pname);
		}
		else if (args.length == 1 && Permission.has(player, Permission.INFO))
		{
			pname = plugin.myGetPlayerName(args[0]);
		}
		else if (args.length == 1)
		{
			plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.DONT_HAVE_PERMISSION).replace("%permission", Permission.INFO.toString()));
		}
		else
		{
			plugin.sendMessage(Vlevel.COMMAND, player, "Invalid command usage.");
			plugin.sendMessage(Vlevel.COMMAND, player, ChatColor.AQUA + "/sc ginfo (player)");
		}

		Map<Atype, List<String>> info = plugin.getLockManager().getGlobalAccessList(pname);
		Set<Atype> keys = info.keySet();


		plugin.sendMessage(Vlevel.COMMAND, player, ChatColor.GOLD + "======== "+pname+"'s Global List ========");
		
		for (Atype key : keys)
		{
			if(key == Atype.Clan)
			{
				List<String> names = info.get(Atype.Clan);
				Set<String> fixedlist = new HashSet<String>();
				for (String tag : names )
				{
					String ctag = tag;
					if (plugin.usingSimpleClans)
					{
						Clan c = plugin.simpleClans.getClanManager().getClan(tag);
						if (c != null) {
							ctag = c.getTagLabel() + ChatColor.GREEN;
						}
					}
					fixedlist.add(ctag);
				}
				plugin.sendMessage(Vlevel.COMMAND, player, "Clans:");
				plugin.sendMessage(Vlevel.COMMAND, player, ChatColor.GREEN + fixedlist.toString());
			}
			if (key == Atype.Player)
			{
				List<String> names = info.get(Atype.Player);
				plugin.sendMessage(Vlevel.COMMAND, player, "Players: ");
				plugin.sendMessage(Vlevel.COMMAND, player, ChatColor.GREEN + names.toString());
			}
			if (key == Atype.Group)
			{
				List<String> groups = info.get(Atype.Group);
				plugin.sendMessage(Vlevel.COMMAND, player, "Groups: ");
				plugin.sendMessage(Vlevel.COMMAND, player, ChatColor.GREEN + groups.toString());
			}
		}
		
		return true;
	}
}
