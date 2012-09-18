package me.HAklowner.SecureChests;

import org.bukkit.entity.Player;

/**
 * @author Acrobot
 * modified by HAklowner
 */

public enum Permission {
    LOCK("securechests.lock"),
    LOCK_PUBLIC("securechests.lock.public"),
    LOCK_TRANSFER("securechests.lock.transfer"),
    
    INFO("securechests.info"),
    
    DEBUG("securechests.debug"),
    
    BYPASS_LOCK("securechests.bypass.lock"),
    BYPASS_UNLOCK("securechests.bypass.unlock"),
    BYPASS_UNLOCK_GROUP("securechests.bypass.unlock."),
    BYPASS_OPEN("securechests.bypass.open"),
    BYPASS_OPEN_GROUP("securechests.bypass.open."),
    BYPASS_BREAK("securechests.bypass.break"),
    BYPASS_BREAK_GROUP("securechests.bypass.break."),
    
    ADMIN("securechests.admin"),
    ADMIN_TRANSFER("securechests.admin.transfer"),
    ADMIN_RELOAD("securechests.admin.reload"),
    ADMIN_PURGE("securechests.admin.purge"),
    ADMIN_DELETE_PLAYER("securechests.admin.deleteplayer");
   

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public static boolean has(Player player, Permission perm) {
        return has(player, perm.permission);
    }
    
    public static boolean GroupName(Player p, Permission perm, String name) {
        if (has(p, Permission.ADMIN)) {
            return true;
        }

        String node = perm.permission + name;
        return hasPermissionSet(p, node) || hasPermissionSet(p, node.toLowerCase());
    }

    private static boolean hasPermissionSet(Player p, String perm) {
        return p.isPermissionSet(perm) && p.hasPermission(perm);
    }

    public static boolean has(Player player, String node) {
        return player.hasPermission(node) || player.hasPermission(node.toLowerCase());
    }

    public String toString() {
        return permission;
    }
}
