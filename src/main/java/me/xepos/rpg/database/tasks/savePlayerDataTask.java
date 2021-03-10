package me.xepos.rpg.database.tasks;

import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.database.IDatabaseManager;
import org.bukkit.scheduler.BukkitRunnable;

public class savePlayerDataTask extends BukkitRunnable {

    private final IDatabaseManager databaseManager;
    private final XRPGPlayer xrpgPlayer;

    public savePlayerDataTask(IDatabaseManager databaseManager, XRPGPlayer xrpgPlayer){
        this.databaseManager = databaseManager;
        this.xrpgPlayer = xrpgPlayer;
    }

    @Override
    public void run() {
        databaseManager.savePlayerData(xrpgPlayer);
    }
}
