package me.xepos.rpg.classes.skills.ranger;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.configuration.RangerConfig;
import me.xepos.rpg.utils.Utils;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;

public class SoulShot extends XRPGBowSkill {
    public SoulShot(XRPGPlayer xrpgPlayer, String skillName, XRPG plugin) {
        super(xrpgPlayer, skillName, plugin);
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
        Arrow arrow = (Arrow) e.getProjectile();

        arrow.setDamage(0);
        arrow.setCustomName("Soul");
        arrow.setCustomNameVisible(false);
        setCooldown(RangerConfig.getInstance().soulShotCooldown);
    }

    @Override
    public void initialize() {

    }
}
