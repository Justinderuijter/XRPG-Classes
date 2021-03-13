package me.xepos.rpg.tasks;

import me.xepos.rpg.utils.Utils;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.entities.NecromancerFollower;
import me.xepos.rpg.enums.DamageTakenSource;
import me.xepos.rpg.enums.MultiplierOperation;
import me.xepos.rpg.events.XRPGDamageTakenModifiedEvent;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Bat;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class PurgatoryBatTask extends BukkitRunnable {

    private byte count = 0;
    private final byte maxCount;
    private final Bat bat;
    private final Player player;
    private final double damage;
    private final boolean isBatDmgSource;
    private final XRPG plugin;
    private final long delay;

    public PurgatoryBatTask(Bat bat, Player player, double damage, byte maxCount, boolean isBatDmgSource, XRPG plugin, long debuffDuration){
        this.bat = bat;
        this.player = player;
        this.damage = damage;
        this.isBatDmgSource =isBatDmgSource;
        this.maxCount = maxCount;
        this.plugin = plugin;
        this.delay = debuffDuration;
    }

    @Override
    public void run() {
        if (count >= maxCount)
        {
            bat.remove();
            this.cancel();
            return;
        }


        List<LivingEntity> livingEntities = new ArrayList(bat.getWorld().getNearbyEntities(bat.getLocation(), 2, 2, 2, p -> p instanceof LivingEntity && p != player && p != bat && !(((CraftLivingEntity) p).getHandle() instanceof NecromancerFollower)));
        for (LivingEntity entity : livingEntities) {
            if (isBatDmgSource)
                entity.damage(damage, bat);
            else
                entity.damage(damage, player);

            if (entity instanceof Player) {
                if (count == 0) {
                    XRPGPlayer xrgPlayer = Utils.GetRPG((Player) entity);
                    XRPGDamageTakenModifiedEvent event = new XRPGDamageTakenModifiedEvent((Player)entity, MultiplierOperation.ADDED, DamageTakenSource.PURGATORY_BAT, 1.2);
                    Bukkit.getServer().getPluginManager().callEvent(event);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            XRPGDamageTakenModifiedEvent eventRemove = new XRPGDamageTakenModifiedEvent((Player)entity, MultiplierOperation.REMOVED, DamageTakenSource.PURGATORY_BAT, 1.2);
                            Bukkit.getServer().getPluginManager().callEvent(eventRemove);
                        }
                    }.runTaskLater(plugin, delay);
                }
            }
        }
        count++;
    }
}
