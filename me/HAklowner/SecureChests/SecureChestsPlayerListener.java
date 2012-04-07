package me.HAklowner.SecureChests;


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
        
        // ### make sure block is a CHEST #### //
        if(b.getTypeId() == 54 && plugin.getConfig().getBoolean("Chest")) {
            Player player = event.getPlayer();
            Location chestloc = b.getLocation();

            Location ccN = b.getLocation();
            Location ccE = b.getLocation();
            Location ccS = b.getLocation();
            Location ccW = b.getLocation();

            ccN = ccN.subtract(0,0,1);
            ccE = ccE.subtract(1,0,0);
            ccS = ccS.add(0,0,1);
            ccW = ccW.add(1,0,0);

            Boolean dchest = false;
            if (ccN.getBlock().getTypeId() == 54) {
                chestloc = chestloc.subtract(0, 0, 1);
                dchest = true;
            } else if (ccE.getBlock().getTypeId() == 54) {
                chestloc = chestloc.subtract(1, 0, 0);
                dchest = true;
            } else if (ccS.getBlock().getTypeId() == 54) {
                dchest = true;
            } else if (ccW.getBlock().getTypeId() == 54) {
                dchest = true;
            }

            //create the YAML string location
            String yamlloc = chestloc.getWorld().getName() + "." + chestloc.getBlockX() + "_" + chestloc.getBlockY() + "_" + chestloc.getBlockZ();

            //get name of chest owner
            String lockname = plugin.getStorageConfig().getString(yamlloc.concat(".owner"));

            //get the current /sc command status:
            // 0/null=none
            // 1= lock
            // 2= unlock
            // 3= add to chest access list
            // 4= remove from chest access list
            
            Integer cmdstatus = plugin.scCmd.get(player);
            if (cmdstatus == null)
            	cmdstatus = 0;

            //is if it is owned or not
            if(lockname == null) {
            	//if not owned and your in /lock mode
            	if (cmdstatus == 1) {
            		if (dchest)
            			player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Double Chest locked.");
            		else
            			player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Single Chest locked.");
            		plugin.getStorageConfig().set(yamlloc.concat(".owner"),player.getName());
            		plugin.scCmd.remove(player);
            		plugin.saveStorageConfig();              
            	} else if(cmdstatus == 6) { //check to see if they want to lock a chest on someone else's behalf
            		plugin.scCmd.remove(player);
            		String checkName = plugin.scAList.get(player);
            		if (dchest) {
            			player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Double Chest locked for "+checkName);
           			} else {                        
           				player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Single Chest locked for "+checkName);
           				plugin.getStorageConfig().set(yamlloc.concat(".owner"), checkName);
           				plugin.saveStorageConfig();
           				plugin.scAList.remove(player);                        
           			}
            	}            
            } else {
            	//It is owned by someone check if it is yours!
            	if (player.getName().equals(lockname)) { // it's yours!
            		if(cmdstatus == 2) {//check to see if they want to unlock this chest.
            			plugin.getStorageConfig().set(yamlloc, null);
    	             plugin.scCmd.remove(player);
    	             player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Chest unlocked");
    	             plugin.saveStorageConfig();
                  
            		} else if(cmdstatus == 3) { //check to see if they want to add a name to the access list.
            			plugin.scCmd.remove(player);
            			String checkName = plugin.scAList.get(player);
                        if (!plugin.getStorageConfig().getBoolean(yamlloc+".access."+checkName)){
                        	player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Adding "+checkName+" to access list.");
                        	plugin.getStorageConfig().set(yamlloc+".access."+checkName, true);
                            plugin.saveStorageConfig();
                            plugin.scAList.remove(player);
                        } else {
                            player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Player "+checkName+" already in access list.");
                        }
                    } else if(cmdstatus == 4) { //They want to Remove a name from the access list
                        plugin.scCmd.remove(player);
                        String checkName = plugin.scAList.get(player);
                        if (!plugin.getStorageConfig().getBoolean(yamlloc+".access."+checkName)){
                            player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Player "+checkName+" Not found in list.");
                        } else {
                            player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Player "+checkName+" Removed from list.");
                            plugin.getStorageConfig().set(yamlloc+".access."+checkName, null);
                            plugin.saveStorageConfig();
                            plugin.scAList.remove(player);
                        }
                    } else if(cmdstatus == 5) { //check to see if they want to add a name to the deny list.
                        plugin.scCmd.remove(player);
                        String checkName = plugin.scAList.get(player);
                        if (plugin.getStorageConfig().getBoolean(yamlloc+".access."+player.getName()) || plugin.getStorageConfig().get(yamlloc+".access."+player.getName()) == null){
                        	player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Adding "+checkName+" to deny list.");
                            plugin.getStorageConfig().set(yamlloc+".access."+checkName, false);
                            plugin.saveStorageConfig();
                            plugin.scAList.remove(player);
                        } else {
                          player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Player "+checkName+" already in deny list.");
                        }
                    
                    } else {// no commands to be executed just open chest.
                      player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" You own this Chest");
                      return;
                    }
            	} else { // chest owned by someone else                  

                    //check access list for your name

                	if (plugin.getStorageConfig().getBoolean(yamlloc+".access."+player.getName())){ //You are on the list!
                		player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" You have access to " + lockname + "'s chest." );
                		return; //allow you to open it!
                	} else if (plugin.getStorageConfig().get(yamlloc+".access."+player.getName()) != null) { //You are on the deny list! ohh no!
                		event.setCancelled(true);
                    } else if (plugin.getAListConfig().getBoolean(lockname+"." + player.getName())){ // you are on the global allow list! yay!
                    	player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" You have acces to " + lockname + "'s chest." );
                        return;
                    }
                	if(cmdstatus == 2) {//check to see if they want to unlock this chest.
  	                    if(player.hasPermission("securechests.bypass.break")) { //check for admin bypass
  	                    	plugin.getStorageConfig().set(yamlloc, null);
  	                    	plugin.scCmd.remove(player);
  	                    	player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests] "+ ChatColor.WHITE + lockname + "'s Chest unlocked");
  	                    	plugin.saveStorageConfig();
  	                    	return;                  
  	                    }
                	}                    
                	if(player.hasPermission("securechests.bypass.open")) { //check for admin bypass
                		player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" bypassing lock owned by player: " + lockname);
                		event.setCancelled(false);
                		return;
                	} else { //no bypass owned by someone else deny entry
                		player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Can not open chest, owned by:  " + lockname);
                		if(!(player.hasPermission("securechests.bypass.break") && event.getAction() == Action.LEFT_CLICK_BLOCK)) {
                			event.setCancelled(true);
                        }
                	}
                }
            }
        } //end check for chest block
        
     // ########   make sure block click is a FURNACE   ######### //
        else if((b.getTypeId() == 61 || b.getTypeId() == 62) && plugin.getConfig().getBoolean("Furnace")) { //furnace can have two states. off-61 or on-62 check for both
            Player player = event.getPlayer();
            Location chestloc = b.getLocation();

            //create the YAML string location
            String yamlloc = chestloc.getWorld().getName() + "." + chestloc.getBlockX() + "_" + chestloc.getBlockY() + "_" + chestloc.getBlockZ();

            //get name of chest owner
            String lockname = plugin.getStorageConfig().getString(yamlloc.concat(".owner"));

            //get the current /sc command status:
            // 0/null=none
            // 1= lock
            // 2= unlock
            // 3= add to chest access list
            // 4= remove from chest access list

            Integer cmdstatus = plugin.scCmd.get(player);
            if (cmdstatus == null)
                cmdstatus = 0;

            //is if it is owned or not
            if(lockname == null) {
                //if not owned and your in /lock mode
                if (cmdstatus == 1) {
                    player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Furnace locked.");
                    plugin.getStorageConfig().set(yamlloc.concat(".owner"),player.getName());
                    plugin.scCmd.remove(player);
                    plugin.saveStorageConfig();
                } else if(cmdstatus == 6) { //check to see if they want to lock a chest on someone else's behalf
                  plugin.scCmd.remove(player);
                  String checkName = plugin.scAList.get(player);
                    player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Furnace locked for "+checkName);
                    plugin.getStorageConfig().set(yamlloc.concat(".owner"), checkName);
                    plugin.saveStorageConfig();
                    plugin.scAList.remove(player);                        
                  }                    
            } else {
                //It is owned by someone check if it is yours!
                if (player.getName().equals(lockname)) { // it's yours!
                    if(cmdstatus == 2) {//check to see if they want to unlock this chest.
                        plugin.getStorageConfig().set(yamlloc, null);
                        plugin.scCmd.remove(player);
                        player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Furnace unlocked");
                        plugin.saveStorageConfig();

                    } else if(cmdstatus == 3) { //check to see if they want to add a name to the access list.
                        plugin.scCmd.remove(player);
                        String checkName = plugin.scAList.get(player);
                        if (!plugin.getStorageConfig().getBoolean(yamlloc+".access."+checkName)){
                            player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Adding "+checkName+" to access list.");
                            plugin.getStorageConfig().set(yamlloc+".access."+checkName, true);
                            plugin.saveStorageConfig();
                            plugin.scAList.remove(player);
                        } else {
                            player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Player "+checkName+" already in access list.");
                        }
                    } else if(cmdstatus == 4) { //They want to Remove a name from the access list
                        plugin.scCmd.remove(player);
                        String checkName = plugin.scAList.get(player);
                        if (!plugin.getStorageConfig().getBoolean(yamlloc+".access."+checkName)){
                            player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Player "+checkName+" Not found in list.");
                        } else {
                            player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Player "+checkName+" Removed from list.");
                            plugin.getStorageConfig().set(yamlloc+".access."+checkName, null);
                            plugin.saveStorageConfig();
                            plugin.scAList.remove(player);
                        }
                    } else if(cmdstatus == 5) { //check to see if they want to add a name to the deny list.
                    	plugin.scCmd.remove(player);
                    	String checkName = plugin.scAList.get(player);
                    	if (plugin.getStorageConfig().getBoolean(yamlloc+".access."+player.getName()) || plugin.getStorageConfig().get(yamlloc+".access."+player.getName()) == null){
                    		player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Adding "+checkName+" to deny list.");
                            plugin.getStorageConfig().set(yamlloc+".access."+checkName, false);
                            plugin.saveStorageConfig();
                            plugin.scAList.remove(player);
                        } else {
                            player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Player "+checkName+" already in deny list.");
                        } 
                    } else {// no commands to be executed just open chest.
                        player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" You own this Furnace");
                        return;
                    }
                } else { // chest owned by someone else

                    //check access list for your name

                    if (plugin.getStorageConfig().getBoolean(yamlloc+".access."+player.getName())){ //You are on the list!
                        player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" You have access to " + lockname + "'s furnace." );
                        return; //allow you to open it!
                    } else if (plugin.getStorageConfig().get(yamlloc+".access."+player.getName()) != null) { //You are on the deny list! ohh no!
                        event.setCancelled(true);
                    } else if (plugin.getAListConfig().getBoolean(lockname+"." + player.getName())){ // you are on the global allow list! yay!
                        player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" You have acces to " + lockname + "'s furnace." );
                        return;
                    }
                    if(cmdstatus == 2) {//check to see if they want to unlock this chest.
                      if(player.hasPermission("securechests.bypass.break")) { //check for admin bypass
                        plugin.getStorageConfig().set(yamlloc, null);
                        plugin.scCmd.remove(player);
                        player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests] "+ ChatColor.WHITE + lockname + "'s Furnace unlocked");
                        plugin.saveStorageConfig();
                        return;                  
                      }
                    }
                    if(player.hasPermission("securechests.bypass.open")) { //check for admin bypass
                        player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" bypassing lock owned by player: " + lockname);
                        event.setCancelled(false);
                        return;
                    } else { //no bypass owned by someone else deny entry
                        player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Can not open furnace, owned by:  " + lockname);
                        if(!(player.hasPermission("securechests.bypass.break") && event.getAction() == Action.LEFT_CLICK_BLOCK)) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }//end check for furnace block

        // ########   make sure block click is a DISPENSER   ######### //
        else if(b.getTypeId() == 23 && plugin.getConfig().getBoolean("Dispenser")) { //make sure block click is a DISPENSER & config allows locking/unlocking DISPENSER
              Player player = event.getPlayer();
              Location chestloc = b.getLocation();

              Location ccN = b.getLocation();
              Location ccE = b.getLocation();
              Location ccS = b.getLocation();
              Location ccW = b.getLocation();

              ccN = ccN.subtract(0,0,1);
              ccE = ccE.subtract(1,0,0);
              ccS = ccS.add(0,0,1);
              ccW = ccW.add(1,0,0);


              //create the YAML string location
              String yamlloc = chestloc.getWorld().getName() + "." + chestloc.getBlockX() + "_" + chestloc.getBlockY() + "_" + chestloc.getBlockZ();

              //get name of chest owner
              String lockname = plugin.getStorageConfig().getString(yamlloc.concat(".owner"));

              //get the current /sc command status:
              // 0/null=none
              // 1= lock
              // 2= unlock
              // 3= add to chest access list
              // 4= remove from chest access list

              Integer cmdstatus = plugin.scCmd.get(player);
              if (cmdstatus == null)
                  cmdstatus = 0;

              //is if it is owned or not
              if(lockname == null) {
                  //if not owned and your in /lock mode
                  if (cmdstatus == 1) {
                      player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Dispenser locked.");
                      plugin.getStorageConfig().set(yamlloc.concat(".owner"),player.getName());
                      plugin.scCmd.remove(player);
                      plugin.saveStorageConfig();
                  } else if(cmdstatus == 6) { //check to see if they want to lock a chest on someone else's behalf
                    plugin.scCmd.remove(player);
                    String checkName = plugin.scAList.get(player);
                      player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Dispenser locked for "+checkName);
                      plugin.getStorageConfig().set(yamlloc.concat(".owner"), checkName);
                      plugin.saveStorageConfig();
                      plugin.scAList.remove(player);                        
                    }                    
              } else {
                  //It is owned by someone check if it is yours!
                  if (player.getName().equals(lockname)) { // it's yours!
                      if(cmdstatus == 2) {//check to see if they want to unlock this chest.
                          plugin.getStorageConfig().set(yamlloc, null);
                          plugin.scCmd.remove(player);
                          player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Dispenser unlocked");
                          plugin.saveStorageConfig();

                      } else if(cmdstatus == 3) { //check to see if they want to add a name to the access list.
                          plugin.scCmd.remove(player);
                          String checkName = plugin.scAList.get(player);
                          if (!plugin.getStorageConfig().getBoolean(yamlloc+".access."+checkName)){
                              player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Adding "+checkName+" to access list.");
                              plugin.getStorageConfig().set(yamlloc+".access."+checkName, true);
                              plugin.saveStorageConfig();
                              plugin.scAList.remove(player);
                          } else {
                              player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Player "+checkName+" already in access list.");
                          }
                      } else if(cmdstatus == 4) { //They want to Remove a name from the access list
                          plugin.scCmd.remove(player);
                          String checkName = plugin.scAList.get(player);
                          if (!plugin.getStorageConfig().getBoolean(yamlloc+".access."+checkName)){
                              player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Player "+checkName+" Not found in list.");
                          } else {
                              player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Player "+checkName+" Removed from list.");
                              plugin.getStorageConfig().set(yamlloc+".access."+checkName, null);
                              plugin.saveStorageConfig();
                              plugin.scAList.remove(player);
                          }
                      } else if(cmdstatus == 5) { //check to see if they want to add a name to the deny list.
                          plugin.scCmd.remove(player);
                          String checkName = plugin.scAList.get(player);
                          if (plugin.getStorageConfig().getBoolean(yamlloc+".access."+player.getName()) || plugin.getStorageConfig().get(yamlloc+".access."+player.getName()) == null){
                              player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Adding "+checkName+" to deny list.");
                              plugin.getStorageConfig().set(yamlloc+".access."+checkName, false);
                              plugin.saveStorageConfig();
                              plugin.scAList.remove(player);
                          } else {
                              player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Player "+checkName+" already in deny list.");
                          } 
                      } else {// no commands to be executed just open chest.
                          player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" You own this Dispenser");
                          return;
                      }
                  } else { // chest owned by someone else

                      //check access list for your name

                      if (plugin.getStorageConfig().getBoolean(yamlloc+".access."+player.getName())){ //You are on the list!
                          player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" You have access to " + lockname + "'s dispenser." );
                          return; //allow you to open it!
                      } else if (plugin.getStorageConfig().get(yamlloc+".access."+player.getName()) != null) { //You are on the deny list! ohh no!
                          event.setCancelled(true);
                      } else if (plugin.getAListConfig().getBoolean(lockname+"." + player.getName())){ // you are on the global allow list! yay!
                          player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" You have acces to " + lockname + "'s dispenser." );
                          return;
                      }
                      if(cmdstatus == 2) {//check to see if they want to unlock this chest.
                        if(player.hasPermission("securechests.bypass.break")) { //check for admin bypass
                          plugin.getStorageConfig().set(yamlloc, null);
                          plugin.scCmd.remove(player);
                          player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests] "+ ChatColor.WHITE + lockname + "'s dispenser unlocked");
                          plugin.saveStorageConfig();
                          return;                  
                        }
                      }
                      if(player.hasPermission("securechests.bypass.open")) { //check for admin bypass
                          player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" bypassing lock owned by player: " + lockname);
                          event.setCancelled(false);
                          return;
                      } else { //no bypass owned by someone else deny entry
                          player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Can not open dispenser, owned by:  " + lockname);
                          if(!(player.hasPermission("securechests.bypass.break") && event.getAction() == Action.LEFT_CLICK_BLOCK)) {
                              event.setCancelled(true);
                          }
                      }
                  }
              }


      }//end check for dispenser block
        
        
        // ########   make sure block click is a DOOR   ######### //
        else if(b.getTypeId() == 64 && plugin.getConfig().getBoolean("Door")) { //make sure block click is a DOOR
            Player player = event.getPlayer();

			Location doorloc = b.getLocation();
			
			Door d = (Door)b.getState().getData();
			
			if (d.isTopHalf()) { //You clicked on the top part of the door! correct location to reflect bottom part
				doorloc = doorloc.subtract(0,1,0);
			}

            //create the YAML string location
            String yamlloc = doorloc.getWorld().getName() + "." + doorloc.getBlockX() + "_" + doorloc.getBlockY() + "_" + doorloc.getBlockZ();

            //get name of chest owner
            String lockname = plugin.getStorageConfig().getString(yamlloc.concat(".owner"));

            //get the current /sc command status:
            // 0/null=none
            // 1= lock
            // 2= unlock
            // 3= add to chest access list
            // 4= remove from chest access list

            Integer cmdstatus = plugin.scCmd.get(player);
            if (cmdstatus == null)
                cmdstatus = 0;

            //is if it is owned or not
            if(lockname == null) {
                //if not owned and your in /lock mode
                if (cmdstatus == 1) {
                    player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Door locked.");
                    plugin.getStorageConfig().set(yamlloc.concat(".owner"),player.getName());
                    plugin.scCmd.remove(player);
                    plugin.saveStorageConfig();
                } else if(cmdstatus == 6) { //check to see if they want to lock a chest on someone else's behalf
                  plugin.scCmd.remove(player);
                  String checkName = plugin.scAList.get(player);
                    player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Door locked for "+checkName);
                    plugin.getStorageConfig().set(yamlloc.concat(".owner"), checkName);
                    plugin.saveStorageConfig();
                    plugin.scAList.remove(player);                        
                  }
            } else {
                //It is owned by someone check if it is yours!
                if (player.getName().equals(lockname)) { // it's yours!
                    if(cmdstatus == 2) {//check to see if they want to unlock this chest.
                        plugin.getStorageConfig().set(yamlloc, null);
                        plugin.scCmd.remove(player);
                        player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Door unlocked");
                        plugin.saveStorageConfig();

                    } else if(cmdstatus == 3) { //check to see if they want to add a name to the access list.
                        plugin.scCmd.remove(player);
                        String checkName = plugin.scAList.get(player);
                        if (!plugin.getStorageConfig().getBoolean(yamlloc+".access."+checkName)){
                            player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Adding "+checkName+" to access list.");
                            plugin.getStorageConfig().set(yamlloc+".access."+checkName, true);
                            plugin.saveStorageConfig();
                            plugin.scAList.remove(player);
                        } else {
                            player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Player "+checkName+" already in access list.");
                        }
                    } else if(cmdstatus == 4) { //They want to Remove a name from the access list
                        plugin.scCmd.remove(player);
                        String checkName = plugin.scAList.get(player);
                        if (!plugin.getStorageConfig().getBoolean(yamlloc+".access."+checkName)){
                            player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Player "+checkName+" Not found in list.");
                        } else {
                            player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Player "+checkName+" Removed from list.");
                            plugin.getStorageConfig().set(yamlloc+".access."+checkName, null);
                            plugin.saveStorageConfig();
                            plugin.scAList.remove(player);
                        }
                    } else if(cmdstatus == 5) { //check to see if they want to add a name to the deny list.
                        plugin.scCmd.remove(player);
                        String checkName = plugin.scAList.get(player);
                        if (plugin.getStorageConfig().getBoolean(yamlloc+".access."+player.getName()) || plugin.getStorageConfig().get(yamlloc+".access."+player.getName()) == null){
                            player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Adding "+checkName+" to deny list.");
                            plugin.getStorageConfig().set(yamlloc+".access."+checkName, false);
                            plugin.saveStorageConfig();
                            plugin.scAList.remove(player);
                        } else {
                            player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Player "+checkName+" already in deny list.");
                        } 
                    } else {// no commands to be executed just open chest.
                        player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" You own this Door");
                        return;
                    }
                } else { // chest owned by someone else

                    //check access list for your name

                    if (plugin.getStorageConfig().getBoolean(yamlloc+".access."+player.getName())){ //You are on the list!
                        player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" You have access to " + lockname + "'s door." );
                        return; //allow you to open it!
                    } else if (plugin.getStorageConfig().get(yamlloc+".access."+player.getName()) != null) { //You are on the deny list! ohh no!
                        event.setCancelled(true);
                    } else if (plugin.getAListConfig().getBoolean(lockname+"." + player.getName())){ // you are on the global allow list! yay!
                        player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" You have acces to " + lockname + "'s door." );
                        return;
                    }
                    if(cmdstatus == 2) {//check to see if they want to unlock this chest.
                    	if(player.hasPermission("securechests.bypass.break")) { //check for admin bypass
                    		plugin.getStorageConfig().set(yamlloc, null);
                    		plugin.scCmd.remove(player);
                    		player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests] "+ ChatColor.WHITE + lockname + "'s Doors unlocked");
                    		plugin.saveStorageConfig();
                    		return;                  
                    	}
                    }
                    if(player.hasPermission("securechests.bypass.open")) { //check for admin bypass
                    	player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" bypassing lock owned by player: " + lockname);
                    	event.setCancelled(false);
                    	return;
                    } else { //no bypass owned by someone else deny entry
                    	player.sendMessage(ChatColor.DARK_BLUE + "[Secure Chests]"+ChatColor.WHITE+" Can not open door, owned by:  " + lockname);
                    	if(!(player.hasPermission("securechests.bypass.break") && event.getAction() == Action.LEFT_CLICK_BLOCK)) {
                    		event.setCancelled(true);
                    	}
                    }
                }
            }
        }//end check for DOOR block
    }//end onPlayerInteract();

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        if (plugin.scAList.get(event.getPlayer()) != null) {
            plugin.scAList.remove(event.getPlayer());
        }
        if (plugin.scCmd.get(event.getPlayer()) != null) {
            plugin.scCmd.remove(event.getPlayer());
        }
    }
}
