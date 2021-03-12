package me.xepos.rpg.dependencies;


import me.xepos.rpg.XRPG;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getServer;

public class PartyManagerFactory {

    private final static String configPartyManager = JavaPlugin.getPlugin(XRPG.class).getConfig().getString("Party Manager Type", "Factions");

    public static IPartyManager getPartyManager() {

        if (configPartyManager != null) {
            Plugin plugin = getServer().getPluginManager().getPlugin(configPartyManager);
            if (plugin != null && plugin.isEnabled()) {
                if (configPartyManager.equalsIgnoreCase("Parties"))
                    return new PartiesManager();

                else if (configPartyManager.equalsIgnoreCase("factions"))
                    return checkFactions();
            }
        }
        return new DefaultManager();
    }

    @SuppressWarnings("unused")
    private static IPartyManager checkFactions() {
        boolean isFactionsUUID = true;
        boolean isFactions3 = true;
        IPartyManager manager = new DefaultManager();
        try {
            Class<?> checkForPlayer = Class.forName("com.massivecraft.factions.FPlayer");
            Class<?> checkForPlayers = Class.forName("com.massivecraft.factions.FPlayers");
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            isFactionsUUID = false;
        }

        try {
            Class<?> checkForMPlayer = Class.forName("com.massivecraft.factions.entity.MPlayer");
            Class<?> checkForFaction = Class.forName("com.massivecraft.factions.entity.Faction");
            Class<?> checkForRel = Class.forName("com.massivecraft.factions.Rel");
        } catch (ClassNotFoundException | NoClassDefFoundError ex) {
            isFactions3 = false;
        }

        if (isFactions3 && !isFactionsUUID) {
            System.out.println("Found Factions3");
            return new FactionsManager();
        } else if (isFactionsUUID && !isFactions3) {
            System.out.println("Found FactionsUUID");
            return new FactionsUUIDManager();
        } else {
            return new DefaultManager();
        }
    }
}
