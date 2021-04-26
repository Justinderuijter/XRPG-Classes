package me.xepos.rpg.classes.skills.wizard;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class WindBarrier extends XRPGSkill {
    public WindBarrier(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin) {
        super(xrpgPlayer, skillName, cooldown, plugin);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
        Player player = (Player) e.getEntity();

        if (player.getHealth() <= player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 2) {
            if (e.getDamager() instanceof Projectile || e.getDamager() instanceof Explosive) {
                if (isSkillReady()) {
                    e.setCancelled(true);
                    setRemainingCooldown(getCooldown());
                    player.sendMessage(ChatColor.RED + getSkillName() + " is now on cooldown for " + getRemainingCooldown() + " seconds!");
                }
            }
        }
    }

    @Override
    public void initialize() {

    }
}
