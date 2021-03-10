package me.xepos.rpg.tasks;

import me.xepos.rpg.enums.DamageTakenSource;
import me.xepos.rpg.enums.MultiplierOperation;
import me.xepos.rpg.events.XRPGDamageTakenModifiedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RemoveDTModifierTask extends BukkitRunnable {

    private final Player player;
    private final DamageTakenSource source;

    public RemoveDTModifierTask(Player player, DamageTakenSource sourceAbility)
    {
        this.player = player;
        this.source = sourceAbility;
    }

    @Override
    public void run() {
        XRPGDamageTakenModifiedEvent eventRemove = new XRPGDamageTakenModifiedEvent(player, MultiplierOperation.REMOVED, source, 1.2);
        Bukkit.getServer().getPluginManager().callEvent(eventRemove);
    }
}
