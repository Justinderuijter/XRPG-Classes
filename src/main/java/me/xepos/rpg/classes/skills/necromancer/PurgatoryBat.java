package me.xepos.rpg.classes.skills.necromancer;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.IEffectDuration;
import me.xepos.rpg.classes.skills.IRepeatingTrigger;
import me.xepos.rpg.classes.skills.XRPGSkill;
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

public class PurgatoryBat extends XRPGSkill implements IRepeatingTrigger, IEffectDuration {

    private int interval = 20;
    private byte maxCount = 5;
    private int duration = 6;

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

            Bat bat = (Bat) livingEntity.getWorld().spawnEntity(livingEntity.getEyeLocation(), EntityType.BAT);
            bat.setAI(false);
            bat.setInvulnerable(true);
            bat.setCollidable(false);
            bat.setAwake(true);
            bat.setCustomName("Purgatory bat");
            bat.setCustomNameVisible(false);

            new PurgatoryBatTask(bat, player, getDamage(), maxCount, false, getPlugin(), duration * 20L)
                    .runTaskTimer(getPlugin(), 10, interval);

            setRemainingCooldown(getCooldown());
        }
    }

    @Override
    public int getInterval() {
        return interval;
    }

    @Override
    public void setInterval(int delay) {
        this.interval = delay;
    }

    @Override
    public byte getMaxProcs() {
        return maxCount;
    }

    @Override
    public void setMaxProcs(byte maxProcs) {
        this.maxCount = maxProcs;
    }

    @Override
    public int getEffectDuration() {
        return duration;
    }

    @Override
    public void setEffectDuration(int duration) {
        this.duration = duration;
    }
}
