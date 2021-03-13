package me.xepos.rpg.listeners;

import me.xepos.rpg.utils.Utils;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.configuration.RavagerConfig;
import me.xepos.rpg.configuration.WizardConfig;
import me.xepos.rpg.dependencies.protection.ProtectionSet;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class ProjectileListener implements Listener
{
    private final XRPG plugin;
    private final ProtectionSet ps;

    public ProjectileListener(XRPG plugin)
    {
        this.plugin = plugin;
        this.ps = plugin.getProtectionSet();
    }

    @EventHandler
    public void projectileHit(ProjectileHitEvent e)
    {
        int entityId = e.getEntity().getEntityId();
        if(e.getEntity() instanceof SmallFireball)
        {
            if(plugin.fireBalls.containsKey(entityId))
            {
                if(e.getEntity().getCustomName() != null && e.getEntity().getCustomName().equals("Flame Slash")){
                    flameSlashLogic(e, entityId);
                    return;
                }
                smallFireballLogic(e,entityId);
                plugin.fireBalls.remove(entityId);
            }
        }else if(e.getEntity() instanceof Arrow && e.getEntity().getShooter() instanceof Player)
        {
            Arrow arrow = (Arrow)e.getEntity();
            Location location = null;
            if (e.getHitEntity() != null)
                location = e.getHitEntity().getLocation();
            else if (e.getHitBlock() != null)
                location = e.getHitBlock().getLocation();

            if (location != null && location.getWorld() != null)
            {

                if (arrow.getCustomName() != null && ps.isLocationValid(((Player)arrow.getShooter()).getLocation(), arrow.getLocation()))
                {
                    switch (arrow.getCustomName()) {
                        case "Lightning":
                            location.getWorld().strikeLightning(location);
                            break;
                        case "Explosion":
                            arrow.remove();
                            location.getWorld().createExplosion(location, 2F, false, false, (Entity) e.getEntity().getShooter());
                            break;
                        case "Ender":
                            ((Player) arrow.getShooter()).teleport(location.add(0, 1, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                            break;
                        case "Darkness":
                            AreaEffectCloud cloud = arrow.getWorld().spawn(arrow.getLocation(), AreaEffectCloud.class);
                            cloud.setSource(arrow.getShooter());
                            cloud.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 50, 0), true);
                            break;
                        case "Soul":
                            if(e.getHitEntity() != null && e.getHitEntity() instanceof LivingEntity)
                            {
                                LivingEntity livingEntity = (LivingEntity) e.getHitEntity();
                                livingEntity.setHealth(livingEntity.getHealth() * 0.75);
                            }
                            break;
                    }
                }

            }
        }
    }
    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent e)
    {
        int entityId = e.getEntity().getEntityId();
        if(plugin.fireBalls.containsKey(entityId))
        {
            if(e.getEntity() instanceof Fireball)
            {
                meteorLogic(e);
                plugin.fireBalls.remove(entityId);
            }
        }
    }

    private void meteorLogic(ExplosionPrimeEvent e)
    {
        Fireball fireball = (Fireball)e.getEntity();
        if (fireball.getShooter() instanceof Player)
        {
            double explosionStrength = plugin.fireBalls.get(fireball.getEntityId());
            Player shooter = (Player)fireball.getShooter();
            Location loc = fireball.getLocation();
            e.setCancelled(true);
            loc.getWorld().createExplosion(loc, (float)explosionStrength, WizardConfig.getInstance().meteorSetFire, WizardConfig.getInstance().meteorDamageBlocks, shooter);
        }

    }


    private void smallFireballLogic(ProjectileHitEvent e, int entityId)
    {
        //Getting projectile instance in first if would probably improve performance slightly
        if (e.getHitEntity() != null && e.getHitEntity() instanceof LivingEntity)
        {
            LivingEntity livingEntity = (LivingEntity) e.getHitEntity();
            if (e.getEntity().getShooter() instanceof Player)
            {
                Player shooter = (Player)e.getEntity().getShooter();
                livingEntity.damage(plugin.fireBalls.get(entityId), shooter);
                if (livingEntity.getFireTicks() == -1)
                {
                    shooter.sendMessage("Max Fire Ticks: " + livingEntity.getMaxFireTicks() + ", Fire Ticks: " + WizardConfig.getInstance().smallFireballFireTicks);
                    livingEntity.setFireTicks(WizardConfig.getInstance().smallFireballFireTicks);
                }

            }
        }
    }

    private void flameSlashLogic(ProjectileHitEvent e, int entityId)
    {
        if (e.getEntity().getShooter() instanceof Player) {
            Player shooter = (Player) e.getEntity().getShooter();

            //Remove fireball from list and do nothing, since we didn't hit an entity to 'bounce off of'.
            if (e.getHitBlock() != null) {
                plugin.fireBalls.remove(entityId);
                return;
            }

            if (e.getHitEntity() instanceof LivingEntity && e.getHitEntity() != null) {
                LivingEntity livingEntity = (LivingEntity) e.getHitEntity();

                livingEntity.damage(plugin.fireBalls.get(entityId), shooter);
                Fireball fireball = livingEntity.launchProjectile(SmallFireball.class);
                fireball.setShooter(shooter);
                fireball.setCustomName("Flame Slash");
                fireball.setCustomNameVisible(false);

                //Pick an entity in range...
                LivingEntity newTarget = Utils.getRandomLivingEntity(livingEntity, 20.0 , 4.0, shooter, RavagerConfig.getInstance().ignoreVillagers);
                if (newTarget != null) {
                    //Shoot fireball towards said entity
                    Vector vector = newTarget.getLocation().toVector().subtract(livingEntity.getLocation().toVector());
                    fireball.setDirection(vector);
                    if (!plugin.fireBalls.containsKey(fireball.getEntityId()))
                        plugin.fireBalls.put(fireball.getEntityId(), 6.0);
                }
                else
                {
                     fireball.remove();
                }

            }
            plugin.fireBalls.remove(entityId);
        }
    }
}
