package me.HAklowner.SecureChests.Listeners;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.HAklowner.SecureChests.Lock;
import me.HAklowner.SecureChests.Permission;
import me.HAklowner.SecureChests.SecureChests;
import me.HAklowner.SecureChests.Config.Config;
import me.HAklowner.SecureChests.Config.Language;
import me.HAklowner.SecureChests.Utils.Atype;
import me.HAklowner.SecureChests.Utils.Vlevel;
import net.sacredlabyrinth.phaed.simpleclans.Clan;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class SecureChestsPlayerListener implements Listener {

	public SecureChests plugin;

	public SecureChestsPlayerListener(SecureChests instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(PlayerInteractEvent event) {

		//make sure we are dealing with a block and not clicking on air or an entity
		if (!(event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
			return;

		Block b = event.getClickedBlock();
		Player player = event.getPlayer();

		//START NEW CODE
		if(plugin.isBlockEnabled(b.getTypeId())) {//check to see if block clicked is on the watch list and is enabled.

			Location blockLoc = b.getLocation();
			String blockName = plugin.getBlockName(b.getTypeId());


			Lock lock = plugin.getLockManager().getLock(blockLoc);
			//lock.setLocation(blockLoc);


			Integer cmdStatus = plugin.scCmd.remove(player);
			String aListEntry = plugin.scAList.remove(player);
			Atype at = plugin.scAtype.remove(player);

			if (cmdStatus == null) {
				cmdStatus = 0;
			}

			if(lock != null) {
				//The block has a locked status. lets now get the owner
				String owner = lock.getOwner();
				Integer access = lock.getAccess(player);

				String clantag = "uhoh";
				//get color tag for clan if it exists
				if (at == Atype.Clan && plugin.usingSimpleClans)
				{
					Clan clan = plugin.simpleClans.getClanManager().getClan(aListEntry);
					if (clan != null)
					{
						clantag = clan.getTagLabel();
					}
					else
					{
						clantag = aListEntry;
					}
				}


				if (cmdStatus != 0) // they want to run a command on said locked block.
				{
					event.setCancelled(true); // stop them from actualy interacting with block so we can run the command.


					//check for ownership change first
					if (cmdStatus == 11 && (Permission.has(player, Permission.ADMIN_TRANSFER) || (access == 1 && Permission.has(player, Permission.LOCK_TRANSFER))))
					{
						event.setCancelled(true);
						plugin.sendMessage(Vlevel.COMMAND, player, "Transferring ownership of " +blockName+" from "+owner+" to "+aListEntry);
						lock.setOwner(aListEntry);
						lock.updateLock();
						return;
					}
					else if (cmdStatus == 11) //trying to change owner without permission
					{
						if(access == 1)
						{
							plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.DONT_HAVE_PERMISSION).replace("%permission", Permission.LOCK_TRANSFER.toString()));
						}
						else
						{
							plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.DONT_HAVE_PERMISSION).replace("%permission", Permission.ADMIN_TRANSFER.toString()));
						}
						
					}
					else if (cmdStatus == 12 && (Permission.has(player, Permission.INFO) || access == 1 ))
					{
						Map<String, Boolean> info = lock.getPlayerAccessList();
						Set<String> names = info.keySet();
						Set<String> allow = new HashSet<String>();
						Set<String> deny = new HashSet<String>();
						if(plugin.usingSimpleClans)
						{
							Map<String, Boolean> cinfo = lock.getClanAccessList();
							Set<String> cnames = cinfo.keySet();
							for (String cname : cnames) {
								Clan c = plugin.simpleClans.getClanManager().getClan(cname);
								plugin.sendMessage(Vlevel.DEBUG, player, "Populating info tag for clan: " + cname);
								if (c != null)
								{
									plugin.sendMessage(Vlevel.DEBUG, player, cname + " found");
									String ctag = c.getColorTag();
									
									if (cinfo.get(cname))
									{
										allow.add("c:"+ctag + ChatColor.GREEN);
									}
									else
									{
										deny.add("c:"+ctag + ChatColor.RED);
									}
								}
								else
								{
									plugin.sendMessage(Vlevel.DEBUG, player, cname + " not found.");
									
									if (cinfo.get(cname))
									{
										allow.add("c:" + cname);
									}
									else
									{
										deny.add("c:" + cname);
									}
								}
							}
						}
						
						if (plugin.vaultEnabled())
						{
							Map<String, Boolean> ginfo = lock.getGroupAccessList();
							Set<String> gnames = ginfo.keySet();
							for (String gname : gnames)
							{
								if (ginfo.get(gname))
								{
									allow.add("g:" + gname);
								}
								else
								{
									deny.add("g:" + gname);
								}
							}
						}
						
						
						for (String name : names) {
							if (info.get(name)) {
								allow.add(name);
							} else {
								deny.add(name);
							}
						}

						String pub = ChatColor.RED + Config.getLocal(Language.LANG_NO);
						if (lock.isPublic())
							pub = ChatColor.GREEN + Config.getLocal(Language.LANG_YES);

						String reslock = ChatColor.RED + Config.getLocal(Language.LANG_NO);
						if (lock.isResouseLocked())
							reslock = ChatColor.GREEN + Config.getLocal(Language.LANG_YES);

						plugin.sendMessage(Vlevel.COMMAND, player, ChatColor.GOLD + "======== "+owner+"'s "+blockName+" ========");
						plugin.sendMessage(Vlevel.COMMAND, player, ChatColor.AQUA + Config.getLocal(Language.PUBLIC) +": " + pub + ChatColor.AQUA + " " + Config.getLocal(Language.RESOURCE_LOCK) +": " + reslock);
						plugin.sendMessage(Vlevel.COMMAND, player, ChatColor.RED + Config.getLocal(Language.DENY_LIST)+":");
						plugin.sendMessage(Vlevel.COMMAND, player, ChatColor.RED + deny.toString());
						plugin.sendMessage(Vlevel.COMMAND, player, ChatColor.GREEN + Config.getLocal(Language.ALLOW_LIST) + ":");
						plugin.sendMessage(Vlevel.COMMAND, player, ChatColor.GREEN + allow.toString());

					}

					if(access == 1) //it's yours yay!
					{ 
						if (cmdStatus == 2)  //unlock and stop from further interacton.
						{
							lock.unlock();
							plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.BLOCK_UNLOCKED).replace("%block", blockName));
						}
						else if (cmdStatus == 3) //Add to access list
						{
							plugin.sendMessage(Vlevel.DEBUG, player, "add detected type: "+at.toString()+" alist entry: " + aListEntry);
							if(at == Atype.Player) //player
							{
								if(lock.addToAccessList(aListEntry))
								{
									plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_ADDED_ALLOW).replace("%type", Config.getLocal(Language.PLAYER)).replace("%name", aListEntry).replace("%block", blockName));
								} 
								else
								{
									plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_ALREADY_ALLOW).replace("%type", Config.getLocal(Language.PLAYER)).replace("%name", aListEntry).replace("%block", blockName));
								}
							}
							else if (at == Atype.Clan) //Clan
							{ 
								if(lock.addToAccessList(Atype.Clan, aListEntry))
								{
									plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_ADDED_ALLOW).replace("%type", Config.getLocal(Language.CLAN)).replace("%name", clantag + ChatColor.WHITE).replace("%block", blockName));
								}
								else
								{
									plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_ALREADY_ALLOW).replace("%type", Config.getLocal(Language.CLAN)).replace("%name", clantag + ChatColor.WHITE).replace("%block", blockName));
								}
							}
							else if (at == Atype.Group)
							{
								if(lock.addToAccessList(Atype.Group, aListEntry))
								{
									plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_ADDED_ALLOW).replace("%type", Config.getLocal(Language.GROUP)).replace("%name", aListEntry).replace("%block", blockName));
								}
								else
								{
									plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_ALREADY_ALLOW).replace("%type", Config.getLocal(Language.GROUP)).replace("%name", aListEntry).replace("%block", blockName));
								}
							}
						}
						else if (cmdStatus == 4) //remove from access list
						{
							plugin.sendMessage(Vlevel.DEBUG, player, "Removal detected type: "+at.toString()+" alist entry: " + aListEntry);
							if (at == Atype.Player)
							{
								if(lock.removeFromAccessList(Atype.Player, aListEntry))
								{
									plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_REMOVED).replace("%type", Config.getLocal(Language.PLAYER)).replace("%name", aListEntry).replace("%block", blockName));
								}
								else
								{
									plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_UNABLE_TO_FIND).replace("%type", Config.getLocal(Language.PLAYER)).replace("%name", aListEntry).replace("%block", blockName));
								}
							}
							else if (at == Atype.Clan)
							{
								if(lock.removeFromAccessList(Atype.Clan, aListEntry)) 
								{
									plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_REMOVED).replace("%type", Config.getLocal(Language.CLAN)).replace("%name", clantag + ChatColor.WHITE).replace("%block", blockName));
								}
								else
								{
									plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_UNABLE_TO_FIND).replace("%type", Config.getLocal(Language.CLAN)).replace("%name", clantag + ChatColor.WHITE).replace("%block", blockName));
								}
							}
							else if (at == Atype.Group)
							{
								if(lock.removeFromAccessList(Atype.Group, aListEntry)) 
								{
									plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_REMOVED).replace("%type", Config.getLocal(Language.GROUP)).replace("%name", aListEntry).replace("%block", blockName));
								}
								else
								{
									plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_UNABLE_TO_FIND).replace("%type", Config.getLocal(Language.GROUP)).replace("%name", aListEntry).replace("%block", blockName));
								}
							}
						}
						else if (cmdStatus == 5) //add to deny list
						{
							if (at == Atype.Player)
							{
								if(lock.addToDenyList(Atype.Player, aListEntry))
								{
									plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_ADDED_DENY).replace("%type", Config.getLocal(Language.PLAYER)).replace("%name", aListEntry).replace("%block", blockName));
								}
								else
								{
									plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_ALREADY_DENY).replace("%type", Config.getLocal(Language.PLAYER)).replace("%name", aListEntry).replace("%block", blockName));
								}
							}
							else if (at == Atype.Clan)
							{
								if(lock.addToDenyList(Atype.Clan, aListEntry))
								{
									plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_ADDED_DENY).replace("%type", Config.getLocal(Language.CLAN)).replace("%name", clantag + ChatColor.WHITE).replace("%block", blockName));
								}
								else
								{
									plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_ALREADY_DENY).replace("%type", Config.getLocal(Language.CLAN)).replace("%name", clantag + ChatColor.WHITE).replace("%block", blockName));
								}
							}
							else if (at == Atype.Group)
							{
								if (lock.addToDenyList(Atype.Group, aListEntry))
								{
									plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_ADDED_DENY).replace("%type", Config.getLocal(Language.GROUP)).replace("%name", aListEntry).replace("%block", blockName));
								}
								else
								{
									plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_ALREADY_DENY).replace("%type", Config.getLocal(Language.GROUP)).replace("%name", aListEntry).replace("%block", blockName));
								}
							}
						}
						else if (cmdStatus == 10) //toggle public status
						{ 
							if(lock.isPublic())
							{
								lock.setPublic(false);
								plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_PUBLIC_OFF).replace("%block", blockName));
							}
							else
							{
								lock.setPublic(true);
								plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.NOTICE_PUBLIC_ON).replace("%block", blockName));
							}
						}
						return;
					} //End owned block commands.

					//start bypass detection.
					if (cmdStatus == 2 && Permission.has(player, Permission.BYPASS_UNLOCK))
					{
						event.setCancelled(true); //they want to unlock somone elses chest and they have permission to.
						plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.BYPASS_UNLOCK).replace("%block", blockName).replace("%owner", owner));
						lock.unlock();
						return;
					}

					return;
				} //end command checking.

				//check to see if they can open the chest.
				if(access == 1) { //it's yours yay!
					plugin.sendMessage(Vlevel.OWN, player, Config.getLocal(Language.NOTICE_LOCK_OWN).replace("%block", blockName));
					return;
				} 

				if (access == 4) { 
					plugin.sendMessage(Vlevel.OTHER, player, Config.getLocal(Language.NOTICE_LOCK_PUBLIC).replace("%block", blockName));
					return;
				}

				if (access == 2) { //your on the allow list
					plugin.sendMessage(Vlevel.OTHER, player, Config.getLocal(Language.NOTICE_LOCK_ACCESS).replace("%block", blockName).replace("%owner", owner));
					return;
				}

				if (access == 3) {
					plugin.sendMessage(Vlevel.OVERRIDE, player, Config.getLocal(Language.NOTICE_LOCK_OVERRIDE).replace("%block", blockName).replace("%owner", owner));
					return;
				}

				plugin.sendMessage(Vlevel.DENY, player, Config.getLocal(Language.NOTICE_LOCK_DENY).replace("%owner", owner).replace("%block", blockName));
				event.setCancelled(true);

			} else if(cmdStatus != 0) { //The block is NOT locked. and we want to run a command on it.
				Lock newlock = new Lock(blockLoc);

				if (cmdStatus == 1) { //not locked and we want to lock it!
					newlock.lock(player.getName());
					plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.BLOCK_LOCKED).replace("%block", blockName));
					event.setCancelled(true);
					return;
				}

				if (cmdStatus == 6 && Permission.has(player, Permission.BYPASS_LOCK)) {
					newlock.lock(aListEntry);
					plugin.sendMessage(Vlevel.COMMAND, player, Config.getLocal(Language.BLOCK_LOCKED_OTHER).replace("%block", blockName).replace("%username", aListEntry));
					event.setCancelled(true);
					return;
				}
			}
		}
	}//end onPlayerInteract();

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		plugin.scAList.remove(event.getPlayer());
		plugin.scCmd.remove(event.getPlayer());
		plugin.scAtype.remove(event.getPlayer());
		plugin.scVlevel.remove(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		Player player = event.getPlayer();
		plugin.scVlevel.put(player, plugin.getLockManager().getVerbLevel(player.getName()));
	}
}
