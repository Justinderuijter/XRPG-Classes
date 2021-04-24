package me.xepos.rpg.classes.skills.necromancer;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.configuration.NecromancerConfig;
import me.xepos.rpg.tasks.BleedTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ShadowSneak extends XRPGSkill {
    public ShadowSneak(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin) {
        super(xrpgPlayer, skillName, cooldown, plugin);

        xrpgPlayer.getSneakRightClickEventHandler().addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerInteractEvent)) return;
        PlayerInteractEvent e = (PlayerInteractEvent) event;
        doShadowSneak(e.getPlayer());

    }

    @Override
    public void initialize() {

    }

    private void doShadowSneak(Player player) {
        if (!isSkillReady()) {
            player.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }

        RayTraceResult result = player.getLocation().getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 20, FluidCollisionMode.NEVER, true, 0.3, p -> p instanceof LivingEntity && p != player);
        if (result != null && result.getHitEntity() != null) {
            LivingEntity livingEntity = (LivingEntity) result.getHitEntity();
            NecromancerConfig necromancerConfig = NecromancerConfig.getInstance();

            List<Bat> bats = summonBats(player);
            removeBats(bats, getPlugin(), necromancerConfig.batDespawnDelay * 20);

            Vector direction = livingEntity.getLocation().getDirection().setY(0.).normalize().multiply(-2.);
            player.teleport(livingEntity.getLocation().add(direction), PlayerTeleportEvent.TeleportCause.PLUGIN);
            if (livingEntity instanceof Player && getProtectionSet().isLocationValid(player.getLocation(), livingEntity.getLocation()) && !getPartyManager().isPlayerAllied(player, (Player) livingEntity)) {
                livingEntity.damage(necromancerConfig.shadowSneakDamage, player);

                new BleedTask(livingEntity, player, necromancerConfig.shadowSneakTicks, necromancerConfig.shadowSneakDamagePerTick).runTaskTimer(getPlugin(), 11, 20);
            }
            setRemainingCooldown(necromancerConfig.shadowSneakCooldown);
        }
    }

    private List<Bat> summonBats(Player player) {
        List<Bat> bats = new ArrayList<>();
        Vector velocity = new Vector(0, 1, 0);
        for (int i = 0; i < NecromancerConfig.getInstance().shadowSneakBatCount; i++) {
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
}
