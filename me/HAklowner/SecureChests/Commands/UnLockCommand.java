package me.HAklowner.SecureChests.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.HAklowner.SecureChests.SecureChests;

public class UnLockCommand implements CommandExecutor {

	private final SecureChests plugin;
	
	public UnLockCommand(SecureChests instance) {
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
			plugin.sendMessage(player, "Now interact with a container/door to unlock it.");
			plugin.scCmd.put(player, 2);
			return true;
		} else {
			plugin.sendMessage(player, "You dont have permission to use SecureChests. (securechests.lock)");
		}
		return false;
	}
}
