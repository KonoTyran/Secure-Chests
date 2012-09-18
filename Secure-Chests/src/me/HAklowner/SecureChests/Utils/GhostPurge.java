package me.HAklowner.SecureChests.Utils;

import me.HAklowner.SecureChests.SecureChests;

import org.bukkit.entity.Player;

public class GhostPurge {

	private SecureChests plugin;
	private Boolean console = true;
	private Player player;

	public GhostPurge () {
		plugin = SecureChests.getInstance();
	}

	public void purge(Player player) {
		console = false;
		this.player = player;
		purge();
	}

	public void purge(boolean fromconsole) {
		if (fromconsole) { 
			console = true;
			purge();
		}
	}

	private void sendmessage(String msg) {
		if (!console) { //send to player if player started command
			plugin.sendMessage(Vlevel.COMMAND, player, msg);
		}
		//send to console regardless of who started it.
		SecureChests.log("[" + plugin.getDescription().getName() + "] "+msg);
	}

	private void purge() {
		sendmessage("Not yet added :(.");
	}
}