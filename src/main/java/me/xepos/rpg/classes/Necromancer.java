package me.xepos.rpg.classes;

import me.xepos.rpg.Utils;
import me.xepos.rpg.XRPG;

import me.xepos.rpg.configuration.NecromancerConfig;
import me.xepos.rpg.entities.NecromancerFollower;
import me.xepos.rpg.tasks.BleedTask;
import me.xepos.rpg.tasks.PurgatoryBatTask;
import net.minecraft.server.v1_16_R3.EntityLiving;
import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

//This class and its components are the heaviest nms using classes in this plugin.
//A solution should be sought to make this easily adaptable for multiple version.

public class Necromancer extends XRPGClass {
    public Necromancer(XRPG plugin) {
        super(plugin);
    }

    private final NecromancerConfig necromancerConfig = NecromancerConfig.getInstance();

    private long shadowSneakCooldown = Utils.setSkillCooldown(necromancerConfig.shadowSneakCooldown);
    private long boneShieldCooldown = Utils.setSkillCooldown(necromancerConfig.boneShieldCooldown);
    private long purgatoryBatCooldown = Utils.setSkillCooldown(necromancerConfig.purgatoryBatCooldown);
    public List<NecromancerFollower> followers = new ArrayList<>();

    @Override
    public void onHit(EntityDamageByEntityEvent e) {
        for (NecromancerFollower follower : followers) {
                EntityLiving entityLiving = ((CraftLivingEntity) e.getEntity()).getHandle();
                if (entityLiving instanceof NecromancerFollower) {
                    if (!followers.contains(entityLiving))
                        follower.setGoalTarget(entityLiving, EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true);
                } else {
                    follower.setGoalTarget(entityLiving, EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true);
                }
        }

        if (((Player) e.getDamager()).getInventory().getItemInMainHand().getType().toString().toLowerCase().contains("_hoe")) {
            e.setDamage(e.getDamage() + necromancerConfig.hoeFlatDamageBonus);
        }
    }

    @Override
    public void onHurt(EntityDamageByEntityEvent e) {
        Player player = (Player) e.getEntity();
        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        if (Utils.isSkillReady(boneShieldCooldown)) {
            if (player.getHealth() <= maxHealth / 2) {
                double absorptionHearts = this.followers.size() * necromancerConfig.shieldPerFollower;
                player.setAbsorptionAmount(player.getAbsorptionAmount() + absorptionHearts);
                player.sendMessage(ChatColor.DARK_GREEN + "Bone shield will absorb " + absorptionHearts + " damage!");
                player.sendMessage(ChatColor.RED + "Bone shield is now on cooldown for " + boneShieldCooldown + " seconds.");
                boneShieldCooldown = Utils.setSkillCooldown(necromancerConfig.boneShieldCooldown);
            }
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

    }

    @Override
    public void onUseItem(PlayerInteractEvent e) {
        Material handItem = e.getPlayer().getInventory().getItemInMainHand().getType();
        if (handItem.toString().toLowerCase().contains("_hoe")) {
            Player player = e.getPlayer();
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if(e.getPlayer().isSneaking())
                    doShadowSneak(player);
                else
                    doPurgatoryBat(player);
            }
        }

    }

    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent e) {

    }

    @Override
    public void onInteractWithEntity(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) e.getRightClicked();
            if (((CraftLivingEntity) entity).getHandle() instanceof NecromancerFollower) {
                NecromancerFollower follower = (NecromancerFollower) ((CraftLivingEntity) entity).getHandle();
                if (!e.getPlayer().isSneaking()) {
                    e.getPlayer().sendMessage(follower.getEntityType().toString());
                    e.getPlayer().sendMessage("Owner: " + follower.getOwner().getName());
                } else {
                    if (this.followers.contains(follower)) {
                        follower.getOwner().sendMessage("You unsummoned " + follower.getName());
                        this.followers.remove(follower);
                        follower.getBukkitEntity().remove();
                    }

                }

            }
        }
    }

    @Override
    public void onShootBow(EntityShootBowEvent e) {

    }


    private List<Bat> summonBats(Player player) {
        List<Bat> bats = new ArrayList<>();
        Vector velocity = new Vector(0, 1, 0);
        for (int i = 0; i < necromancerConfig.shadowSneakBatCount; i++) {
            Bat bat = (Bat) player.getWorld().spawnEntity(player.getLocation(), EntityType.BAT);
            bat.setInvulnerable(true);
            bat.setCollidable(false);
            bat.setVelocity(velocity);

            bats.add(bat);
        }
        return bats;
    }

    private void removeBats(List<Bat> batList, XRPG plugin, long delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Bat bat : batList) {
                    bat.remove();
                }
            }
        }.runTaskLater(plugin, delay);
    }

    private void doShadowSneak(Player player)
    {
        if (!Utils.isSkillReady(shadowSneakCooldown)) {
            player.sendMessage(Utils.getCooldownMessage("Shadow sneak", shadowSneakCooldown));
            return;
        }

        RayTraceResult result = player.getLocation().getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 20, FluidCollisionMode.NEVER, true, 0.3, p -> p instanceof LivingEntity && p != player);
        if (result != null && result.getHitEntity() != null) {
            LivingEntity livingEntity = (LivingEntity) result.getHitEntity();

            List<Bat> bats = summonBats(player);
            removeBats(bats, plugin, necromancerConfig.batDespawnDelay * 20);

            Vector direction = livingEntity.getLocation().getDirection().setY(0.).normalize().multiply(-2.);
            player.teleport(livingEntity.getLocation().add(direction), PlayerTeleportEvent.TeleportCause.PLUGIN);
            livingEntity.damage(necromancerConfig.shadowSneakDamage, player);

            new BleedTask(livingEntity, player, necromancerConfig.shadowSneakTicks, necromancerConfig.shadowSneakDamagePerTick).runTaskTimer(plugin, 11, 20);
        }
        shadowSneakCooldown = Utils.setSkillCooldown(necromancerConfig.shadowSneakCooldown);
    }

    @SuppressWarnings("all")
    private void doPurgatoryBat(Player player)
    {
        if (!Utils.isSkillReady(purgatoryBatCooldown))
        {
            player.sendMessage(Utils.getCooldownMessage("Purgatory Bat", purgatoryBatCooldown));
            return;
        }

        RayTraceResult result = player.getLocation().getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 20, FluidCollisionMode.ALWAYS, true, 0.3, p -> p instanceof LivingEntity && p != player);
        if (result != null && result.getHitEntity() != null) {
            LivingEntity livingEntity = (LivingEntity) result.getHitEntity();

            Bat bat = (Bat) livingEntity.getWorld().spawnEntity(livingEntity.getEyeLocation(), EntityType.BAT);
            bat.setAI(false);
            bat.setInvulnerable(true);
            bat.setCollidable(false);
            bat.setAwake(true);
            bat.setCustomName("Purgatory bat");
            bat.setCustomNameVisible(false);

            new PurgatoryBatTask(bat, player, necromancerConfig.purgatoryBatDps, necromancerConfig.purgatoryBatDuration, necromancerConfig.isBatDmgSource, plugin, necromancerConfig.purgatoryBatDuration * 20L)
                    .runTaskTimer(plugin, 10, 20);

            purgatoryBatCooldown = Utils.setSkillCooldown(necromancerConfig.purgatoryBatCooldown);
        }
    }
}
