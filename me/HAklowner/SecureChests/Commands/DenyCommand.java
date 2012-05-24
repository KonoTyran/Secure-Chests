package me.HAklowner.SecureChests.Commands;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.HAklowner.SecureChests.SecureChests;

public class DenyCommand {

	private final SecureChests plugin;

	public DenyCommand() {
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

		if (sender.hasPermission("securechests.lock")) {
			if (args.length != 1) {
				plugin.sendMessage(player, "Correct command usage: /sc deny username");
				return true;
			} else {
				if (args[0].toLowerCase().startsWith("c:") && plugin.usingSimpleClans) { //they want to add a clan not a player
					String clanTag = args[0].substring(2);
					ClanManager cm = plugin.simpleClans.getClanManager();
					if (cm.isClan(clanTag)) {
						Clan clan = cm.getClan(clanTag);
						plugin.sendMessage(player, "will add clan " + clan.getTagLabel() + ChatColor.WHITE + " to the deny list of next owned block you interact with.");
						plugin.scCmd.put(player, 9); //value of 9 add clan to deny list.
						plugin.scClan.put(player, clan);
						return true;
					} else {
						plugin.sendMessage(player, "Clan not found.");
						return true;
					}
				} else if (args[0].toLowerCase().startsWith("c:") && !plugin.usingSimpleClans) {
					plugin.sendMessage(player, "Server not using Simple Clans, unable to add clan to access list.");
					return true;
				} else {
					String pName = plugin.myGetPlayerName(args[0]);
					plugin.sendMessage(player, "will add user " + pName + " to the deny list of the next owned block you interact with.");
					plugin.scAList.put(player , pName);
					plugin.scCmd.put(player, 5);
					return true;
				}
			}
		} else {
			plugin.sendMessage(player, "You don't have permission to use SecureChests. (securechests.lock)");
			return true;
		}
	}
}
