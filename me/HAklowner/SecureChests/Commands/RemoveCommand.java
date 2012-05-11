package me.HAklowner.SecureChests.Commands;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.HAklowner.SecureChests.SecureChests;

public class RemoveCommand {

	private final SecureChests plugin;

	public RemoveCommand(SecureChests instance) {
		plugin = instance;
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
				plugin.sendMessage(player, "Correct command useage: /sc remove username");
			} else {
				if (args[0].toLowerCase().startsWith("c:") && plugin.usingSimpleClans) { //they want to add a clan not a player
					String clanTag = args[0].substring(2);
					ClanManager cm = plugin.simpleClans.getClanManager();
					if (cm.isClan(clanTag)) {
						Clan clan = cm.getClan(clanTag);
						plugin.sendMessage(player, "will remove clan " + clan.getTagLabel() + ChatColor.WHITE + " from the next owned block you interact with.");
						plugin.scCmd.put(player, 8); //value of 8 remove clan from access list.
						plugin.scClan.put(player, clan);
					} else {
						plugin.sendMessage(player, "will remove clan " + clanTag + " from the next owned block you interact with.");
						plugin.scCmd.put(player, 4); //value of 8 add clan to deny list.
						plugin.scAList.put(player, args[0].toLowerCase()); //inactive clan. pass though with c:clantag
					}
				} else if (args[0].toLowerCase().startsWith("c:") && !plugin.usingSimpleClans) {
					plugin.sendMessage(player, "Server not using Simple Clans, unable to add clan to access list.");
				} else {
					String pName = plugin.myGetPlayerName(args[0]);
					plugin.sendMessage(player, "will remove user " + pName + " from the next owned block you interact with.");
					plugin.scAList.put(player , pName);
					plugin.scCmd.put(player, 4);
				}
			}
		} else {
			plugin.sendMessage(player, "You dont have permission to use SecureChests. (securechests.lock)");
		}
		return true;
	}

}
