package me.xepos.rpg.classes.skill.assassin;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.classes.skill.XRPGSkill;
import me.xepos.rpg.configuration.AssassinConfig;
import me.xepos.rpg.enums.SkillActivationType;
import me.xepos.rpg.utils.Utils;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class CutThroat extends XRPGSkill {
    public CutThroat(XRPG plugin, SkillActivationType activationType, String skillName) {
        super(plugin, activationType, skillName);
    }

    public CutThroat(XRPG plugin, List<SkillActivationType> activationTypes, String skillName) {
        super(plugin, activationTypes, skillName);
    }

    @Override
    public void activate(Event event) {
        if (event instanceof EntityDamageByEntityEvent) {
            AssassinConfig assassinConfig = AssassinConfig.getInstance();
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            LivingEntity entity = (LivingEntity) e.getEntity();
            if (e.getEntity() instanceof LivingEntity) {
                Vector attackerDirection = e.getDamager().getLocation().getDirection();
                Vector victimDirection = e.getEntity().getLocation().getDirection();
                //determine if the dot product between the vectors is greater than 0
                //If it is, we can conclude that the attack was a backstab
                if (attackerDirection.dot(victimDirection) > 0) {
                    if (entity.getHealth() <= entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / (100 / assassinConfig.executeThreshold) && Utils.isSkillReady(getCooldown())) {
                        entity.setHealth(0.0);
                        if (entity instanceof Player) {
                            e.getDamager().getWorld().getNearbyEntities(e.getDamager().getLocation(), 10, 5, 10, p -> p instanceof Player).forEach(p -> p.sendMessage(entity.getName() + " was executed by " + e.getDamager().getName() + "!"));
                        }
                        setCooldown(assassinConfig.cutThroatCooldown);

                    } else {
                        double finalDmg = e.getDamage() * assassinConfig.backStrikeMultiplier;
                        e.setDamage(finalDmg);
                        e.getDamager().sendMessage("Backstrike dealt " + finalDmg + " damage!");
                    }
                }
            }
        }
    }

    @Override
    public void initialize() {

    }
}
