package me.xepos.rpg.listeners;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.datatypes.BaseProjectileData;
import me.xepos.rpg.datatypes.ExplosiveProjectileData;
import me.xepos.rpg.datatypes.ProjectileData;
import me.xepos.rpg.dependencies.protection.ProtectionSet;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

public class ProjectileListener implements Listener {
    private final XRPG plugin;
    private final ProtectionSet ps;

    public ProjectileListener(XRPG plugin) {
        this.plugin = plugin;
        this.ps = plugin.getProtectionSet();
    }

    @EventHandler
    public void projectileHit(ProjectileHitEvent e) {
        if (!plugin.projectiles.containsKey(e.getEntity().getUniqueId())) return;
        final Projectile projectile = e.getEntity();

        BaseProjectileData pData = plugin.projectiles.get(projectile.getUniqueId());
        plugin.projectiles.remove(e.getEntity().getUniqueId());

        if (pData instanceof ProjectileData) {
            ProjectileData projectileData = (ProjectileData) pData;


            if (e.getHitEntity() != null) {
                //Section for all projectiles
                e.getHitEntity().setFireTicks(projectileData.getFireTicks());
                if (projectileData.summonsLightning()) {
                    e.getHitEntity().getWorld().strikeLightning(e.getEntity().getLocation());
                }

                projectileBounceLogic(e, projectileData);

                //Section exclusively for projectiles that aren't arrows
                if (!(e.getEntity() instanceof Arrow)) {
                    if (e.getHitEntity() instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) e.getHitEntity();
                        livingEntity.damage(projectileData.getDamage() * projectileData.getDamageMultiplier(), (Player) projectileData.getProjectile().getShooter());
                    }
                    return;
                }

                //section exclusively for arrows.
                if (projectileData.getDamageMultiplier() < 1.0) {
                    if (e.getHitEntity() instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) e.getHitEntity();

                        livingEntity.setHealth(livingEntity.getHealth() * projectileData.getDamageMultiplier());
                    }
                }

            } else if (e.getHitBlock() != null) {

                if (projectileData.summonsLightning()) {
                    e.getHitBlock().getWorld().strikeLightning(e.getHitBlock().getLocation());
                }
            }

        } else if (pData instanceof ExplosiveProjectileData) {
            ExplosiveProjectileData explosiveData = (ExplosiveProjectileData) pData;

            //Determining location
            Location location = null;
            if (e.getHitBlock() != null) {
                location = e.getHitBlock().getLocation();
            } else if (e.getHitEntity() != null) {
                location = e.getHitEntity().getLocation();
            }

            if (location == null || location.getWorld() == null) return;


            //actual execution of skill
            if (explosiveData.summonsLightning()) {
                location.getWorld().strikeLightning(location);
            }

            if (explosiveData.shouldTeleport() && explosiveData.getProjectile().getShooter() instanceof Entity) {
                explosiveData.getShooter().teleport(location);
            }

            location.getWorld().createExplosion(location, explosiveData.getYield(), explosiveData.setsFire(), explosiveData.destroysBlocks(), explosiveData.getShooter());

            //Only triggers if potion effect is added to the data
            explosiveData.summonCloud();

        }

       /* int entityId = e.getEntity().getEntityId();
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
        }*/
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent e) {
        if (!plugin.projectiles.containsKey(e.getEntity().getUniqueId())) return;
        ExplosiveProjectileData explosiveData = (ExplosiveProjectileData) plugin.projectiles.get(e.getEntity().getUniqueId());

        Location location = explosiveData.getProjectile().getLocation();
        e.setCancelled(true);
        location.getWorld().createExplosion(location, explosiveData.getYield(), explosiveData.setsFire(), explosiveData.destroysBlocks(), explosiveData.getShooter());

        plugin.projectiles.remove(explosiveData.getProjectile().getUniqueId());

/*        int entityId = e.getEntity().getEntityId();
        if (plugin.fireBalls.containsKey(entityId)) {
            if (e.getEntity() instanceof Fireball) {
                meteorLogic(e);
                plugin.fireBalls.remove(entityId);
            }
        }*/
    }

/*    private void meteorLogic(ExplosionPrimeEvent e) {
        Fireball fireball = (Fireball) e.getEntity();
        if (fireball.getShooter() instanceof Player) {
            double explosionStrength = plugin.fireBalls.get(fireball.getUniqueId()).;
            Player shooter = (Player) fireball.getShooter();
            Location loc = fireball.getLocation();
            e.setCancelled(true);
            loc.getWorld().createExplosion(loc, (float) explosionStrength, WizardConfig.getInstance().meteorSetFire, WizardConfig.getInstance().meteorDamageBlocks, shooter);
        }

    }*/


/*    private void smallFireballLogic(ProjectileHitEvent e, int entityId) {
        //Getting projectile instance in first if would probably improve performance slightly
        if (e.getHitEntity() != null && e.getHitEntity() instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) e.getHitEntity();
            if (e.getEntity().getShooter() instanceof Player) {
                Player shooter = (Player) e.getEntity().getShooter();
                livingEntity.damage(plugin.fireBalls.get(entityId).getDamage(), shooter);
                if (livingEntity.getFireTicks() == -1) {
                    shooter.sendMessage("Max Fire Ticks: " + livingEntity.getMaxFireTicks() + ", Fire Ticks: " + WizardConfig.getInstance().smallFireballFireTicks);
                    livingEntity.setFireTicks(WizardConfig.getInstance().smallFireballFireTicks);
                }

            }
        }
    }*/

    private void projectileBounceLogic(ProjectileHitEvent e, ProjectileData data) {
        if (e.getHitBlock() != null) {
            plugin.projectiles.remove(data.getProjectile().getUniqueId());
            return;
        }

        if (data.shouldBounce()) {
            if (e.getHitEntity() != null && e.getHitEntity() instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) e.getHitEntity();

                livingEntity.damage(data.getDamage(), data.getShooter());
                LivingEntity newTarget = Utils.getRandomLivingEntity(livingEntity, 20.0, 4.0, data.getShooter(), true);
                if (newTarget != null) {
                    Vector vector = newTarget.getLocation().toVector().subtract(livingEntity.getLocation().toVector());
                    Projectile newProjectile = livingEntity.launchProjectile(data.getProjectile().getClass(), vector);
                    newProjectile.setShooter(data.getProjectile().getShooter());

                    if (!plugin.projectiles.containsKey(newProjectile.getUniqueId())) {
                        ProjectileData projectileData = new ProjectileData(newProjectile, data.getDamage(), data.summonsLightning(), data.shouldTeleport(), 10000);
                        projectileData.setShouldBounce(true);
                        plugin.projectiles.put(newProjectile.getUniqueId(), projectileData);
                    }
                }
            }
        }

        /*if (e.getEntity().getShooter() instanceof Player) {
            Player shooter = (Player) e.getEntity().getShooter();

            //Remove fireball from list and do nothing, since we didn't hit an entity to 'bounce off of'.
            if (e.getHitBlock() != null) {
                plugin.fireBalls.remove(entityId);
                return;
            }

            if (e.getHitEntity() instanceof LivingEntity && e.getHitEntity() != null) {
                LivingEntity livingEntity = (LivingEntity) e.getHitEntity();

                livingEntity.damage(plugin.fireBalls.get(entityId).getDamage(), shooter);
                Fireball fireball = livingEntity.launchProjectile(SmallFireball.class);
                fireball.setShooter(shooter);
                fireball.setCustomName("Flame Slash");
                fireball.setCustomNameVisible(false);

                //Pick an entity in range...
                LivingEntity newTarget = Utils.getRandomLivingEntity(livingEntity, 20.0, 4.0, shooter, RavagerConfig.getInstance().ignoreVillagers);
                if (newTarget != null) {
                    //Shoot fireball towards said entity
                    Vector vector = newTarget.getLocation().toVector().subtract(livingEntity.getLocation().toVector());
                    fireball.setDirection(vector);
                    if (!plugin.fireBalls.containsKey(fireball.getEntityId()))
                        plugin.fireBalls.put(fireball.getEntityId(), new FireballData(6.0, 10));
                } else {
                    fireball.remove();
                }

            }
            plugin.fireBalls.remove(entityId);
        }*/
    }
}
