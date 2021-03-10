package me.xepos.rpg.database;

import me.xepos.rpg.XRPG;
import org.bukkit.plugin.java.JavaPlugin;

public class DatabaseManagerFactory {

    public static IDatabaseManager getDatabaseManager() {

        return JavaPlugin.getPlugin(XRPG.class).getConfig().getBoolean("MySQL.use MySQL", false) ? new MySQLDatabaseManager() : new JSONDatabaseManager();
    }
}
