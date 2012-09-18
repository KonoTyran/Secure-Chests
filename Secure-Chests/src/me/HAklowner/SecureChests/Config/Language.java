package me.HAklowner.SecureChests.Config;

import java.util.LinkedHashMap;
import java.util.Map;

import me.HAklowner.SecureChests.Config.Value;

public enum Language {
	prefix("&9[SC] &f"),
	
	DONT_HAVE_PERMISSION("you don't have permission (&b%permission&f)"),
	
	ACCESS_DENY("you don't have access to %owner's %block."),
	ACCESS_ALLOW("you have access to %owner's %block."),
	ACCESS_BYPASS("bypassing %owner's locked %block."),
	ACCESS_OWN("you own this %block"),
	
	BYPASS_UNLOCK("bypassing and unlocking %block owned by %owner."),
	
	INVALID_SYNTAX("expected syntax: %command"),
	
	INTERACT_INFO("interact with a lock to see its info."),
	INTERACT_LOCK("interact with a block to lock it."),
	INTERACT_LOCK_OTHER("interact with a block to lock it for %username."),
	INTERACT_UNLOCK("interact with a lock to unlock it."),
	INTEARCT_PUBLIC("interact with a lock to toggle its public status."),
	INTERACT_CHANGE_OWNERSHIP("interact with a lock to change ownership to %username."),
	INTERACT_ADD_GROUP("interact with a lock to add group %group to allow list."),
	INTERACT_ADD_CLAN("interact with a lock to add clan %clantag to allow list."),
	INTERACT_ADD_PLAYER("interact with a lock to add player %username to allow list."),
	INTERACT_DENY_GROUP("interact with a lock to add group %group to deny list."),
	INTERACT_DENY_CLAN("interact with a lock to add clan %clantag to deny list."),
	INTERACT_DENY_PLAYER("interact with a lock to add player %username to deny list."),
	INTERACT_REMOVE_GROUP("interact with a lock to remove group %group from access lists."),
	INTERACT_REMOVE_CLAN("interact with a lock to remove clan %clantag from access lists."),
	INTERACT_REMOVE_PLAYER("interact with a lock to remove player %username from access lists."),
	
	CLAN_NOT_FOUND("clan not found"),
	
	NOT_USING_SIMPLE_CLANS("Server not using Simple Clans, unable to add clan to access list."),
	NOT_USING_VAULT("Server not using Vault, unable to add group to access list."),
	
	
	RESOURCE_LOCK("resource lock"),
	PUBLIC("public"),
	HELP("help"),
	USERNAME("username"),
	VALID_PARAMATERS("valid paramaters"),
	DENY_LIST("deny list"),
	PLAYER("player"),
	OR("or"),
	CLAN("clan"),
	CLANTAG("clantag"),
	LANG_YES("yes"),
	LANG_NO("no"),
	GROUP("group"),
	ALLOW_LIST("allow list"),
	CONFIG_RELOAD("configuration reloaded"),
	
	UNKNOWN_COMMAND("unknown command. type &b%command&f for command list."),
	
	BLOCK_LOCKED("%block locked"),
	BLOCK_LOCKED_OTHER("%block locked for %username"),
	BLOCK_UNLOCKED("%block unlocked"),
	BLOCK_UNLOCKED_OTHER("%block owned by %owner unlocked"),
	
	NOTICE_OWN("'you own this lock' messages."),
	NOTICE_OTHER("when you have access to others locks."),
	NOTICE_DENY("'you don't have access' messages."),
	NOTICE_OVERRIDE("you have access because of permission nodes."),
	
	
	NOTICE_PUBLIC_ON("making %block Public."),
	NOTICE_PUBLIC_OFF("making %block private."),
	NOTICE_ALREADY_DENY("%type %name already on %block's deny list."),
	NOTICE_ALREADY_ALLOW("%type %name already on %block's allow list."),
	NOTICE_ADDED_DENY("%type %name added to %block's deny list."),
	NOTICE_ADDED_ALLOW("%type %name added to %block's allow list."),
	NOTICE_REMOVED("%type %name removed from %block's access lists."),
	NOTICE_UNABLE_TO_FIND("unable to find %type %name on %blocks access lists"),
	NOTICE_GADD_CLAN("adding clan %clantag to your global allow list."),
	NOTICE_GADD_PLAYER("adding player %username to your global allow list."),
	NOTICE_GADD_GROUP("adding group %group to your global allow list."),
	NOTICE_GADD_PLAYER_ALREADY("player %username already on global allow list."),
	NOTICE_GADD_CLAN_ALREADY("clan %clantag already on global allow list."),
	NOTICE_GADD_GROUP_ALREADY("group %group already on global allow list."),
	NOTICE_GREMOVE_CLAN("clan %clan removed from global allow list."),
	NOTICE_GREMOVE_GROUP("group %group removed from global allow list."),
	NOTICE_GREMOVE_PLAYER("player %username removed from global allow list."),
	NOTICE_GREMOVE_CLAN_NONE("clan %clan not on global allow list."),
	NOTICE_GREMOVE_GROUP_NONE("group %group not on global allow list."),
	NOTICE_GREMOVE_PLAYER_NONE("player %username not on global allow list."),
	
	NOTICE_LOCK_OWN("you own this %block."),
	NOTICE_LOCK_DENY("you dont have access to %owner's %block."),
	NOTICE_LOCK_ACCESS("you have access to %owner's %block."),
	NOTICE_LOCK_OVERRIDE("bypassing locked %block owned by %owner."),
	NOTICE_LOCK_PUBLIC("public %block."),
	
	TOGGLE_OWN("Own Notifications turned %toggle."),
	TOGGLE_OTHER("Other Notifications turned %toggle."),
	TOGGLE_DENY("Deny Notifications turned %toggle."),
	TOGGLE_OVERRIDE("Override Notifications turned %toggle."),
	
	HELP_LOCK("lock your chests/furnaces/doors/etc..."),
	HELP_UNLOCK("unlock your chests/furnaces/doors/etc..."),
	HELP_ADD("Add a user to container/door access list."),
	HELP_DENY("Add a user to chest container/door list (will override global access list)."),
	HELP_REMOVE("remove a user from container/door access list."),
	HELP_GADD("Add a user to your global allow list."),
	HELP_GREMOVE("remove user from global allow list."),
	HELP_NOTICE("change verbose levels."),
	HELP_PUBLIC("toggle locks public status."),
	HELP_INFO("view info on locks you own."),
	HELP_INFO_ALL("view info on anyones locks."),
	HELP_TRANSFER("transfer a lock you own to 'username'."),
	HELP_TRANSFER_BYPASS("transfer any lock to 'username'."),
	HELP_LOCK_OTHER("lock chest for someone else."),
	HELP_RELOAD("reload config/lang files."),
	HELP_PURGE("(EXPERIMENTAL) purge ghost locks from database."),
	HELP_DELETE_PLAYER("completely remove player from all databases."),
	HELP_FOOTER("use '/sc help #' to get to other pages"),
	
	COMMAND_HELP("help"),
	COMMAND_PUBLIC("public"),
	COMMAND_NOTICE("notice"),
	COMMAND_OFF("off"),
	COMMAND_ON("on"),
	COMMAND_LOCK("lock"),
	COMMAND_UNLOCK("unlock"),
	COMMAND_GADD("gadd"),
	COMMAND_GREMOVE("gremove"),
	COMMAND_ADD("add"),
	COMMAND_REMOVE("remove"),
	COMMAND_DENY("deny"),
	COMMAND_INFO("info"),
	COMMAND_GINFO("ginfo"),
	COMMAND_RELOAD("reload"),
	COMMAND_PURGE("purge"),
	COMMAND_TRANSFER("transfer"),
	COMMAND_DELETE_PLAYER("deleteplayer"),
	COMMAND_PARAM("param"),
	COMMAND_OWN("own"),
	COMMAND_OTHER("other"),
	COMMAND_OVERRIDE("override");
	
	private final String text;
    private static final Map<String, Value> LANGUAGE_STRINGS = new LinkedHashMap<String, Value>();

    private Language(String def) {
        text = def;
    }

    public String toString() {
        return text;
    }

    public Value getValue() {
        return new Value(text);
    }

    public static Map<String, Value> getValues() {
        return LANGUAGE_STRINGS;
    }

    static {
        for (Language property : Language.values()) {
            LANGUAGE_STRINGS.put(property.name(), property.getValue());
        }
    }
}
