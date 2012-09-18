package me.HAklowner.SecureChests.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.HAklowner.SecureChests.Permission;
import me.HAklowner.SecureChests.SecureChests;
import me.HAklowner.SecureChests.Config.Config;
import me.HAklowner.SecureChests.Config.Language;
import me.HAklowner.SecureChests.Utils.Vlevel;

public class LockCommand implements CommandExecutor {

	private final SecureChests plugin;
	
	public LockCommand() {
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
		
		if(args.length == 1 && Permission.has(player, Permission.BYPASS_LOCK)) {
			String pName = plugin.myGetPlayerName(args[0]);
			plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.INTERACT_LOCK_OTHER).replace("%username", pName));
			plugin.scAList.put(player, pName);
			plugin.scCmd.put(player, 6);
			return true;
		}
		if (Permission.has(player, Permission.LOCK)) {
			plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.INTERACT_LOCK));
			plugin.scCmd.put(player, 1);
			return true;
		} else {
			plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.DONT_HAVE_PERMISSION).replace("%permission", Permission.LOCK.toString()));
		}
		
		return false;
	}
}
