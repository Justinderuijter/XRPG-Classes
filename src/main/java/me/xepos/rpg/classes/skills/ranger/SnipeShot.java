package me.xepos.rpg.classes.skills.ranger;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.configuration.RangerConfig;
import me.xepos.rpg.utils.Utils;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class SnipeShot extends XRPGBowSkill {
    public SnipeShot(XRPGPlayer xrpgPlayer, String skillName, XRPG plugin) {
        super(xrpgPlayer, skillName, plugin);

        xrpgPlayer.getShootBowEventHandler().addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityShootBowEvent)) return;
        EntityShootBowEvent e = (EntityShootBowEvent) event;
        if (!(e.getProjectile() instanceof Arrow)) return;

        if (!isSkillReady()) {
            e.getEntity().sendMessage(Utils.getCooldownMessage(getSkillName(), getCooldown()));
            return;
        }
        doSnipeShot(e, (Arrow) e.getProjectile());
        setCooldown(RangerConfig.getInstance().snipeShotCooldown);
    }

    @Override
    public void initialize() {

    }

    private void doSnipeShot(EntityShootBowEvent e, Arrow arrow) {
        float force = e.getForce();
        arrow.setGravity(false);
        arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
        arrow.setPierceLevel(Math.round(force) + 1);
        arrow.setDamage(arrow.getDamage() * RangerConfig.getInstance().snipeShotDamageMultiplier * force);

        new BukkitRunnable() {
            @Override
            public void run() {
                arrow.remove();
            }
        }.runTaskLater(getPlugin(), (int) (force * 300));
    }
}
