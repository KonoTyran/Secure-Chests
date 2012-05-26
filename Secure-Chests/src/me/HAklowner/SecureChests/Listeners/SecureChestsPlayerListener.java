package me.HAklowner.SecureChests.Listeners;

import me.HAklowner.SecureChests.Lock;
import me.HAklowner.SecureChests.SecureChests;
import net.sacredlabyrinth.phaed.simpleclans.Clan;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.material.Door;

public class SecureChestsPlayerListener implements Listener{

	public SecureChests plugin;

	public SecureChestsPlayerListener(SecureChests instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(final PlayerInteractEvent event) {

		//make sure we are dealing with a block and not clicking on air or an entity
		if (!(event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
			return;


		Block b = event.getClickedBlock();
		Player player = event.getPlayer();

		//START NEW CODE

		if(SecureChests.BLOCK_LIST.containsKey(b.getTypeId()) && plugin.blockStatus.get(b.getTypeId())) {//check to see if block clicked is on the watch list and is enabled.

			Location blockLoc = b.getLocation();
			String blockName = SecureChests.BLOCK_LIST.get(b.getTypeId());

			if(b.getTypeId() == 54) { //do double chest location corrections
				Location ccN = b.getLocation();
				Location ccE = b.getLocation();
				Location ccS = b.getLocation();
				Location ccW = b.getLocation();

				ccN = ccN.subtract(0,0,1);
				ccE = ccE.subtract(1,0,0);
				ccS = ccS.add(0,0,1);
				ccW = ccW.add(1,0,0);

				//Boolean dchest = false;
				if (ccN.getBlock().getTypeId() == 54) {
					blockLoc = blockLoc.subtract(0, 0, 1);
					//    dchest = true;
				} else if (ccE.getBlock().getTypeId() == 54) {
					blockLoc = blockLoc.subtract(1, 0, 0);
					//    dchest = true;
				} else if (ccS.getBlock().getTypeId() == 54) {
					//    dchest = true;
				} else if (ccW.getBlock().getTypeId() == 54) {
					//    dchest = true;
				}
			} //END Chest location corrections.

			//Start Door Corrections
			else if(b.getTypeId() == 64) { //make sure block click is a DOOR

				Door d = (Door)b.getState().getData();

				if (d.isTopHalf()) { //You clicked on the top part of the door! correct location to reflect bottom part
					blockLoc = blockLoc.subtract(0,1,0);
				}
			}//End Door Corrections

			Lock lock = plugin.getLockManager().getLock(blockLoc);
			//lock.setLocation(blockLoc);


			//get the current /sc command status:
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
			// 10=Admin ownership transfer

			Integer cmdStatus = plugin.scCmd.remove(player);
			String otherPlayer = plugin.scAList.remove(player);
			Clan clan = plugin.scClan.remove(player);

			if (cmdStatus == null) {
				cmdStatus = 0;
			}

			if(lock.isLocked()) {
				//The block has a locked status. lets now get the owner
				String owner = lock.getOwner();
				Integer access = lock.getAccess(player);


				if (cmdStatus != 0) {  // they want to run a command on said locked block.
					event.setCancelled(true); // stop them from actualy interacting with block so we can run the command.

					
					//check for ownership change first
					if (cmdStatus == 10 && player.hasPermission("securechests.bypass.changeowner")) {
						event.setCancelled(true);
						plugin.sendMessage(player, "Transferring ownership of " +blockName+" from "+owner+" to "+otherPlayer);
						lock.setOwner(otherPlayer);
						lock.updateLock();
						return;
					} 
					
					if(access == 1) { //it's yours yay!
						if (cmdStatus == 2) {//unlock and stop from further interacton.
							lock.unlock();
							plugin.sendMessage(player, blockName + " Unlocked.");
						} else if (cmdStatus == 3) {
							if(lock.addToAccessList(otherPlayer))
								plugin.sendMessage(player, otherPlayer + " added to " + blockName + "'s access list.");
							else
								plugin.sendMessage(player, "Player " + otherPlayer + " already on " + blockName + "'s access list.");
						} else if (cmdStatus == 7) { //add clan to access list.
							if(lock.addToAccessList(clan))
								plugin.sendMessage(player, "clan " + clan.getTagLabel() + ChatColor.WHITE + " added to " + blockName + "'s access list.");
							else
								plugin.sendMessage(player, "clan " + clan.getTagLabel() + ChatColor.WHITE + " already on " + blockName + "'s access list.");
						} else if (cmdStatus == 8) { //remove clan from access list.
							if(lock.removeFromAccessList(clan))
								plugin.sendMessage(player, "clan " + clan.getTagLabel() + ChatColor.WHITE + " removed from " + blockName + "'s access list.");
							else
								plugin.sendMessage(player, "Unable to find clan " + clan.getTagLabel() + ChatColor.WHITE + " on " + blockName + "'s access list.");
						} else if (cmdStatus == 9) { //add clan to deny list.
							if(lock.addToDenyList(clan))
								plugin.sendMessage(player, "clan " + clan.getTagLabel() + ChatColor.WHITE + " added to " + blockName + "'s deny list.");
							else
								plugin.sendMessage(player, "clan " + clan.getTagLabel() + ChatColor.WHITE + " already on " + blockName + "'s deny list.");
						} else if (cmdStatus == 4) {
							if (otherPlayer.toLowerCase().startsWith("c:")) {
								String clanTag = otherPlayer.substring(2);
								if(lock.removeFromAccessList(otherPlayer)) 
									plugin.sendMessage(player, "clan " + clanTag + " removed from " + blockName + "'s access list.");
								else 
									plugin.sendMessage(player, "Unable to find clan " + clanTag + " on " + blockName + "'s access list.");
							} else {
								if(lock.removeFromAccessList(otherPlayer))
									plugin.sendMessage(player, otherPlayer + " removed from " + blockName + "'s access list.");
								else
									plugin.sendMessage(player, "Unable to find " + otherPlayer + " on " + blockName + "'s access list.");
							}
						} else if (cmdStatus == 5) {
							if(lock.addToDenyList(otherPlayer))
								plugin.sendMessage(player, otherPlayer + " Added to " + blockName + "'s deny list.");
							else
								plugin.sendMessage(player, "Player " + otherPlayer + " already on " + blockName + "'s deny list.");
						} else if (cmdStatus == 10) { //toggle public status
							if(lock.isPublic()) {
								lock.setPublic(false);
								plugin.sendMessage(player, "making " + blockName + " private.");
							} else {
								lock.setPublic(true);
								plugin.sendMessage(player, "making " + blockName + " public.");
							}
						}
						return;
					} //End owned block commands.

					//start bypass detection.
					if (cmdStatus == 2 && player.hasPermission("securechests.bypass.unlock")) {
						event.setCancelled(true); //they want to unlock somone elses chest and they have permission to.
						plugin.sendMessage(player, "Bypassing and unlocking "+blockName+" owned by "+owner+".");
						lock.unlock();
						return;
					}

					return;
				} //end command checking.

				//check to see if they can open the chest.
				if(access == 1) { //it's yours yay!
					plugin.sendMessage(player, "You own this "+ blockName +".");
					return;
				} 

				if (access == 4) { 
					plugin.sendMessage(player, "Public "+blockName+".");
					return;
				}
				
				if (access == 2) { //your on the allow list
					plugin.sendMessage(player, "You have access to " + owner + "'s "+ blockName +".");
					return;
				}

				if (access == 3) {
					plugin.sendMessage(player, "bypassing lock owned by " + owner + ".");
					return;
				}

				plugin.sendMessage(player, "Can not open " + blockName + " owned by " + owner + ".");
				event.setCancelled(true);

			} else { //The block is NOT locked.
				if (cmdStatus == 1) { //not locked and we want to lock it!
					lock.lock(player.getName());
					plugin.sendMessage(player, "Locking " + blockName + ".");
					event.setCancelled(true);
					return;
				}
				
				if (cmdStatus == 6) {
					lock.lock(otherPlayer);
					plugin.sendMessage(player, "Locking " + blockName + " for " + otherPlayer + ".");
					return;
				}
			}
		}
	}//end onPlayerInteract();

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		plugin.scAList.remove(event.getPlayer());
		plugin.scCmd.remove(event.getPlayer());
		plugin.scClan.remove(event.getPlayer());
	}
}
