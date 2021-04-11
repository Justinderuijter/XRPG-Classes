package me.xepos.rpg.tasks;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class BloodCorruptionTask extends BukkitRunnable {

    private final Location originalLocation;
    private final LivingEntity target;
    private final LivingEntity caster;

    public BloodCorruptionTask(LivingEntity caster, LivingEntity target) {
        this.originalLocation = target.getLocation();
        this.target = target;
        this.caster = caster;
    }

    @Override
    public void run() {
        double distance = 0;
        if (originalLocation.getWorld() == target.getWorld()) {
            distance = target.getLocation().distance(originalLocation);
        }
        target.damage(distance, caster);
    }
}
