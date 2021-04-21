package me.xepos.rpg.classes.skills.guardian;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.configuration.GuardianConfig;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

public class Taunt extends XRPGSkill {
    public Taunt(XRPGPlayer xrpgPlayer, String skillName, XRPG plugin) {
        super(xrpgPlayer, skillName, plugin);

        xrpgPlayer.getDamageDealtEventHandler().addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
        Player player = (Player) e.getDamager();

        double range = GuardianConfig.getInstance().mobAggroRangeOnHit;
        List<Entity> entities = e.getEntity().getNearbyEntities(range, range, range);
        for (Entity entity : entities) {
            if (entity instanceof Monster) {
                player.sendMessage(entity.getName() + "is now targeting you!");
                ((Monster) entity).setTarget(player);
            }
        }
    }

    @Override
    public void initialize() {

    }
}
