package me.xepos.rpg.classes.skills.ranger;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;

public class Focus extends XRPGSkill {

    public Focus(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin) {
        super(xrpgPlayer, skillName, cooldown, plugin);

        setRemainingCooldown(-1);
        xrpgPlayer.getEventHandler("SHOOT_BOW").addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityShootBowEvent)) return;
        EntityShootBowEvent e = (EntityShootBowEvent) event;
        if (e.getProjectile() instanceof Arrow) {
            ((Arrow) e.getProjectile()).setCritical(true);
        }


    }

    @Override
    public void initialize() {

    }
}
