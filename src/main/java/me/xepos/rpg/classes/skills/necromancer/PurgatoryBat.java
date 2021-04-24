package me.xepos.rpg.classes.skills.necromancer;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.configuration.NecromancerConfig;
import me.xepos.rpg.tasks.PurgatoryBatTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;

public class PurgatoryBat extends XRPGSkill {
    public PurgatoryBat(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin) {
        super(xrpgPlayer, skillName, cooldown, plugin);

        xrpgPlayer.getRightClickEventHandler().addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerInteractEvent)) return;
        PlayerInteractEvent e = (PlayerInteractEvent) event;

        doPurgatoryBat(e.getPlayer());
    }

    @Override
    public void initialize() {

    }

    @SuppressWarnings("all")
    private void doPurgatoryBat(Player player) {
        if (!isSkillReady()) {
            player.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }

        RayTraceResult result = player.getLocation().getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 20, FluidCollisionMode.ALWAYS, true, 0.3, p -> p instanceof LivingEntity && p != player);
        if (result != null && result.getHitEntity() != null) {
            LivingEntity livingEntity = (LivingEntity) result.getHitEntity();
            NecromancerConfig necromancerConfig = NecromancerConfig.getInstance();

            Bat bat = (Bat) livingEntity.getWorld().spawnEntity(livingEntity.getEyeLocation(), EntityType.BAT);
            bat.setAI(false);
            bat.setInvulnerable(true);
            bat.setCollidable(false);
            bat.setAwake(true);
            bat.setCustomName("Purgatory bat");
            bat.setCustomNameVisible(false);

            new PurgatoryBatTask(bat, player, necromancerConfig.purgatoryBatDps, necromancerConfig.purgatoryBatDuration, necromancerConfig.isBatDmgSource, getPlugin(), necromancerConfig.purgatoryBatDuration * 20L)
                    .runTaskTimer(getPlugin(), 10, 20);

            setRemainingCooldown(necromancerConfig.purgatoryBatCooldown);
        }
    }
}
