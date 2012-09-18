package me.HAklowner.SecureChests.Config;

import java.io.File;

import me.HAklowner.SecureChests.SecureChests;

import org.bukkit.ChatColor;

/**
 * @author Acrobot
 */
public class Config {
    private static BreezeConfiguration languageConfig;

    public static void setup() {
        File configFolder = SecureChests.getFolder();
        
        languageConfig = BreezeConfiguration.loadConfiguration(new File(configFolder, "language.yml"), Language.getValues());

    }

    private static String getColored(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String getLocal(Language lang) {
        return getColored(languageConfig.getString(lang.name()));
    }



}