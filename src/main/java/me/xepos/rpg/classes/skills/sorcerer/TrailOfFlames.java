package me.xepos.rpg.classes.skills.sorcerer;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.HashSet;
import java.util.Set;

public class TrailOfFlames extends XRPGSkill {

    private boolean areVillagersIgnored = true;
    private int interval = 10;
    private byte maxProcs = 10;

    public TrailOfFlames(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getEventHandler("RIGHT_CLICK").addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerInteractEvent)) return;
        PlayerInteractEvent e = (PlayerInteractEvent) event;
        if (e.getItem() == null || e.getItem().getType() != Material.ENCHANTED_BOOK) return;

        if (Utils.isItemNameMatching(e.getItem(), "Book of Flame")) {
            doTrailOfFlames(e.getPlayer());
        }
    }

    @Override
    public void initialize() {

    }

    private void doTrailOfFlames(Player caster) {
        if (!isSkillReady()) {
            caster.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }

        RayTraceResult result = Utils.rayTrace(caster, 16, FluidCollisionMode.NEVER);
        final Set<Location> locations = new HashSet<>();
        if (result != null && result.getHitEntity() != null) {
            //Utils.rayTrace() only returns LivingEntity
            LivingEntity target = (LivingEntity) result.getHitEntity();

            new BukkitRunnable() {
                int count = 0;

                @Override
                public void run() {
                    if (count >= maxProcs) {
                        cancel();
                        return;
                    }
                    locations.add(target.getLocation());

                    count++;
                }
            }.runTaskTimer(getPlugin(), 10, interval);

            new BukkitRunnable() {
                final Set<LivingEntity> livingEntities = new HashSet<>();
                int count = 0;

                @Override
                public void run() {
                    if (count >= maxProcs) {
                        cancel();
                        return;
                    }

                    for (Location location : locations) {
                        location.getWorld().spawnParticle(Particle.FALLING_LAVA, location.clone().add(0, 2, 0), 5, 0.5, 0, 0.5, 2);
                        location.getWorld().getNearbyEntities(location, 1, 2, 1, p -> p instanceof LivingEntity && p != caster && !livingEntities.contains(p)).forEach(x -> {
                            livingEntities.add((LivingEntity) x);
                        });

                    }

                    for (LivingEntity livingEntity : livingEntities) {
                        if ((livingEntity instanceof Villager && areVillagersIgnored) ||
                                (livingEntity instanceof Player && !getProtectionSet().isLocationValid(caster.getLocation(), livingEntity.getLocation()))) {
                            //Skip over villagers if the config setting is true.
                            //Skip over players if they are allied or are in safezone.
                            continue;
                        }
                        livingEntity.damage(getDamage(), caster);
                    }

                    count++;
                }
            }.runTaskTimer(getPlugin(), 20, interval);

            setRemainingCooldown(getCooldown());
        }

    }

}
