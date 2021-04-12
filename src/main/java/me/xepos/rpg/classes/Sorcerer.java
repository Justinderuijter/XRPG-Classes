package me.xepos.rpg.classes;

import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.configuration.SorcererConfig;
import me.xepos.rpg.enums.ModifierType;
import me.xepos.rpg.tasks.BloodCorruptionTask;
import me.xepos.rpg.tasks.OverheatTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Sorcerer extends XRPGClass {
    public Sorcerer(XRPG plugin) {
        super(plugin);
    }

    private final SorcererConfig sorcererConfig = SorcererConfig.getInstance();

    private long OverheatCooldown = Utils.setSkillCooldown(sorcererConfig.overheatCooldown);

    @Override
    public void onHit(EntityDamageByEntityEvent e) {

    }

    @Override
    public void onHurt(EntityDamageByEntityEvent e) {

    }

    @Override
    public void onJoin(PlayerJoinEvent e) {

    }

    @Override
    public void onRespawn(PlayerRespawnEvent e) {

    }

    @Override
    public void onPlayerConsumeItem(PlayerItemConsumeEvent e) {

    }

    @Override
    public void onUseItem(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;

        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if (item.getType() == Material.ENCHANTED_BOOK) {
            if (Utils.isItemNameMatching(item, "Book of Flame")) {
                if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    doTrailOfFlames(e.getPlayer());
                } else if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
                    doOverheat(e.getPlayer());
                }

            } else if (Utils.isItemNameMatching(item, "Book of Darkness")) {
                if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    doSouldraw(e.getPlayer());
                } else if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
                    //Not sure yet
                }
            } else if (Utils.isItemNameMatching(item, "Book of Blood")) {
                if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    doBloodPurification(e.getPlayer());
                } else if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
                    doBloodCorruption(e.getPlayer());
                }
            }
        }

    }

    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent e) {

    }

    @Override
    public void onInteractWithEntity(PlayerInteractEntityEvent e) {

    }

    @Override
    public void onShootBow(EntityShootBowEvent e) {

    }

    private void doBloodPurification(Player caster) {
        List<LivingEntity> livingEntities = new ArrayList(caster.getWorld().getNearbyEntities(caster.getLocation(), 10, 10, 10, p -> p instanceof LivingEntity));

        for (LivingEntity livingEntity : livingEntities) {
            if (livingEntity instanceof Player) {
                Player target = (Player) livingEntity;
                if (ps.isPvPTypeSame(caster.getLocation(), target.getLocation()) && partyManager.isPlayerAllied(caster, target)) {
                    for (AttributeModifier mod : AttributeModifierManager.getInstance().getModifiers(ModifierType.NEGATIVE).keySet()) {
                        mod.getUniqueId();
                    }
                    cleanseBadPotionEffects(livingEntity);
                }
            } else {
                cleanseBadPotionEffects(livingEntity);
            }
        }
    }

    private void cleanseBadPotionEffects(LivingEntity livingTarget) {

        for (PotionEffectType potionEffectType : SorcererConfig.getInstance().negativeEffects) {
            if (livingTarget.hasPotionEffect(potionEffectType)) {
                livingTarget.removePotionEffect(potionEffectType);
            }
        }
    }

    private RayTraceResult doRayTrace(Player caster) {
        return caster.getLocation().getWorld().rayTrace(caster.getEyeLocation(), caster.getEyeLocation().getDirection(), 16, FluidCollisionMode.NEVER, true, 0.3, p -> p instanceof LivingEntity && p != caster);
    }

    private void doSouldraw(Player caster) {
        if (caster.getAttackCooldown() >= 1.0) {
            RayTraceResult result = doRayTrace(caster);
            if (result.getHitEntity() != null) {
                LivingEntity target = (LivingEntity) result.getHitEntity();
                target.damage(4, caster);
                //Heal the attacker for half of the damage dealt
                Utils.healLivingEntity(caster, target.getLastDamage() / 2);
                ((CraftPlayer) caster).getHandle().resetAttackCooldown();
            }
        }
    }

    private void doBloodCorruption(Player caster) {
        RayTraceResult result = doRayTrace(caster);
        if (result.getHitEntity() != null) {
            LivingEntity target = (LivingEntity) result.getHitEntity();
            new BloodCorruptionTask(caster, target).runTaskLater(plugin, 4 * 20L);
        }
    }

    private void doOverheat(Player caster) {
        if (!Utils.isSkillReady(OverheatCooldown)) {
            caster.sendMessage(Utils.getCooldownMessage("Overheat", OverheatCooldown));
            return;
        }

        RayTraceResult result = doRayTrace(caster);
        if (result.getHitEntity() != null) {
            //doRayTrace only returns livingEntities so no need to check
            new OverheatTask((LivingEntity) result.getHitEntity());
            OverheatCooldown = Utils.setSkillCooldown(sorcererConfig.overheatCooldown);
        }
    }

    private void doTrailOfFlames(Player caster) {
        RayTraceResult result = doRayTrace(caster);
        final Set<Location> locations = new HashSet<>();
        if (result != null && result.getHitEntity() != null) {
            //doRayTrace() only returns LivingEntity
            LivingEntity target = (LivingEntity) result.getHitEntity();

            new BukkitRunnable() {
                int count = 0;
                final int maxCount = 10;

                @Override
                public void run() {
                    if (count >= maxCount) {
                        cancel();
                        return;
                    }
                    locations.add(target.getLocation());

                    count++;
                }
            }.runTaskTimer(plugin, 10, 10);

            new BukkitRunnable() {
                final Set<LivingEntity> livingEntities = new HashSet<>();
                final int maxCount = 10;
                int count = 0;

                @Override
                public void run() {
                    if (count >= maxCount) {
                        cancel();
                        return;
                    }

                    for (Location location : locations) {
                        location.getWorld().spawnParticle(Particle.SMOKE_LARGE, location, 5, 0, 1, 0, 0.2);
                        location.getWorld().getNearbyEntities(location, 1, 2, 1, p -> p instanceof LivingEntity && p != caster && !livingEntities.contains(p)).forEach(x -> {
                            livingEntities.add((LivingEntity) x);
                        });

                    }

                    for (LivingEntity livingEntity : livingEntities) {
                        livingEntity.damage(4, caster);
                    }

                    count++;
                }
            }.runTaskTimer(plugin, 10, 10);

        }

    }
}
