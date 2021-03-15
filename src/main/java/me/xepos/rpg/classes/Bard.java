package me.xepos.rpg.classes;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.configuration.BardConfig;
import me.xepos.rpg.tasks.HealOverTimeTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class Bard extends XRPGClass {
    public Bard(XRPG plugin) {
        super(plugin);
    }

    private final BardConfig bardConfig = BardConfig.getInstance();

    private long phoenixsBlessingCooldown = Utils.setSkillCooldown(bardConfig.phoenixsBlessingCooldown);
    private long balladCooldown = Utils.setSkillCooldown(bardConfig.balladCooldown);
    private long soundBarrierCooldown = Utils.setSkillCooldown(bardConfig.soundBarrierCooldown);
    private long eGoldenAppleCooldown = Utils.setSkillCooldown(bardConfig.eGoldenAppleCooldown);
    private long goldenAppleCooldown = Utils.setSkillCooldown(bardConfig.goldenAppleCooldown);
    private long potionCooldown = Utils.setSkillCooldown(bardConfig.potionCooldown);
    private boolean isSoundBarrierActive = false;

    @Override
    public void onHit(EntityDamageByEntityEvent e) {
        if (isSoundBarrierActive) {
            e.setCancelled(true);
            return;
        }

        e.setDamage(e.getDamage() * bardConfig.damageMultiplier);
    }

    @Override
    public void onHurt(EntityDamageByEntityEvent e) {
        Player player = (Player) e.getEntity();
        if (e.getFinalDamage() >= player.getHealth()) {
            if (!Utils.isSkillReady(soundBarrierCooldown))
                return;
            isSoundBarrierActive = true;
            player.sendMessage(ChatColor.DARK_GREEN + "Sound Barrier prevented your death!");
            e.setDamage(0);
            player.setNoDamageTicks(bardConfig.soundBarrierDuration * 20);
            soundBarrierCooldown = Utils.setSkillCooldown(bardConfig.soundBarrierCooldown);
            new BukkitRunnable() {
                @Override
                public void run() {
                    isSoundBarrierActive = false;
                }
            }.runTaskLater(plugin, bardConfig.soundBarrierDuration * 20L);
        }
    }

    @Override
    public void onJoin(PlayerJoinEvent e) {
        applyEffects(e.getPlayer());
    }

    @Override
    public void onRespawn(PlayerRespawnEvent e) {
        applyEffects(e.getPlayer());
    }

    @Override
    public void onPlayerConsumeItem(PlayerItemConsumeEvent e) {
        Material item = e.getItem().getType();
        Location location = e.getPlayer().getLocation();

        List<PotionEffect> potionEffects = new ArrayList<>();
        List<Player> players;

        switch (item) {
            case ENCHANTED_GOLDEN_APPLE:
                if (!Utils.isSkillReady(eGoldenAppleCooldown)) {
                    e.getPlayer().sendMessage(Utils.getCooldownMessage("Enchanted Golden Apple AoE", eGoldenAppleCooldown));
                    e.setCancelled(true);
                    return;
                }

                potionEffects.add(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 3));
                potionEffects.add(new PotionEffect(PotionEffectType.REGENERATION, 600, 1));
                potionEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 6000, 0));
                potionEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 6000, 0));

                eGoldenAppleCooldown = Utils.setSkillCooldown(bardConfig.eGoldenAppleCooldown);
                break;
            case GOLDEN_APPLE:
                if (!Utils.isSkillReady(goldenAppleCooldown) || !Utils.isSkillReady(eGoldenAppleCooldown)) {
                    e.getPlayer().sendMessage(Utils.getCooldownMessage("Golden Apple AoE", Math.max(eGoldenAppleCooldown, goldenAppleCooldown)));
                    e.setCancelled(true);
                    return;
                }

                potionEffects.add(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
                potionEffects.add(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));

                goldenAppleCooldown = Utils.setSkillCooldown(bardConfig.goldenAppleCooldown);
                break;
            case POTION:
                if (!Utils.isSkillReady(potionCooldown)) {
                    e.getPlayer().sendMessage(Utils.getCooldownMessage("Potion AoE Heal", potionCooldown));
                    return;
                }

                potionEffects.add(new PotionEffect(PotionEffectType.HEAL, 1, 1));

                potionCooldown = Utils.setSkillCooldown(bardConfig.potionCooldown);
                break;
            default:
                return;

        }
        players = new ArrayList(location.getWorld().getNearbyEntities(location, 10, 5, 10, p -> p instanceof Player && p != e.getPlayer() && partyManager.isPlayerAllied(e.getPlayer(), (Player) p)));
        if (potionEffects.size() > 0) {
            for (Player player : players) {
                for (PotionEffect potionEffect : potionEffects) {
                    player.addPotionEffect(potionEffect);
                }
            }
        }
    }

    @Override
    public void onUseItem(PlayerInteractEvent e) {
        String heldItemName = e.getPlayer().getInventory().getItemInMainHand().getType().toString().toLowerCase();
        if (heldItemName.contains("axe") || heldItemName.contains("_sword") || heldItemName.contains("_shovel")) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Player player = e.getPlayer();
                if (player.isSneaking()) {
                    doPhoenixBlessing(player);
                } else {
                    doBallad(player);
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

    @SuppressWarnings("all")
    private void doPhoenixBlessing(Player player) {
        if (!Utils.isSkillReady(phoenixsBlessingCooldown)) {
            player.sendMessage(Utils.getCooldownMessage("Phoenix's Blessing", phoenixsBlessingCooldown));
            return;
        }


        RayTraceResult result = player.getLocation().getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), bardConfig.maxCastRange, FluidCollisionMode.NEVER, true, 0.3, p -> p instanceof LivingEntity && p != player);
        if (result != null && result.getHitEntity() != null) {
            final int noDamageTickAmount = 100;
            LivingEntity entity = (LivingEntity) result.getHitEntity();

            if (entity instanceof Player) {
                if (ps.isLocationValid(player.getLocation(), null)) {
                    entity.setNoDamageTicks(noDamageTickAmount);
                }
            } else {
                entity.setNoDamageTicks(noDamageTickAmount);
            }

            player.sendMessage(ChatColor.DARK_GREEN + "You applied Phoenix's blessing to " + entity.getName() + "!");

            List<Player> nearbyPlayers = new ArrayList(entity.getLocation().getWorld().getNearbyEntities(entity.getLocation(), 16, 16, 16, p -> p instanceof Player && p != player));
            for (Player nearbyPlayer : nearbyPlayers) {
                nearbyPlayer.sendMessage(ChatColor.RED + player.getName() + " applied Phoenix's Blessing to " + entity.getName() + " for 5 seconds!");
            }
            phoenixsBlessingCooldown = Utils.setSkillCooldown(bardConfig.phoenixsBlessingCooldown);
        }
    }

    @SuppressWarnings("all")
    private void doBallad(Player caster) {
        if (!Utils.isSkillReady(balladCooldown)) {
            caster.sendMessage(Utils.getCooldownMessage("Ballad", balladCooldown));
            return;
        }

        List<Player> nearbyPlayers = new ArrayList(caster.getLocation().getWorld().getNearbyEntities(caster.getLocation(), 10, 5, 10, p -> p instanceof Player));
        for (Player nearbyPlayer : nearbyPlayers) {
            if (partyManager.isPlayerAllied(caster, nearbyPlayer))
                new HealOverTimeTask(nearbyPlayer, bardConfig.balledHealPerProc, bardConfig.balledMaxProcs).runTaskTimer(plugin, 1L, bardConfig.balledProcDelay * 20L);
        }
        balladCooldown = Utils.setSkillCooldown(bardConfig.balladCooldown);
    }

}
