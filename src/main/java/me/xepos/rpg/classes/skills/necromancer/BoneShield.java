package me.xepos.rpg.classes.skills.necromancer;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class BoneShield extends XRPGSkill {
    private ArmyOfTheUndead armyOfTheUndead;

    public BoneShield(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin, @Nullable ArmyOfTheUndead armyOfTheUndead) {
        super(xrpgPlayer, skillName, cooldown, plugin);

        this.armyOfTheUndead = armyOfTheUndead;
        xrpgPlayer.getDamageTakenEventHandler().addSkill(this);
    }

    public BoneShield(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin) {
        super(xrpgPlayer, skillName, cooldown, plugin);

        xrpgPlayer.getDamageTakenEventHandler().addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
        Player player = (Player) e.getEntity();

        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        if (isSkillReady()) {
            if (player.getHealth() <= maxHealth / 2) {

                double absorptionHearts;
                if (armyOfTheUndead == null)
                    absorptionHearts = getDamage();
                else
                    absorptionHearts = this.armyOfTheUndead.getFollowerCount() * getDamage();

                player.setAbsorptionAmount(player.getAbsorptionAmount() + absorptionHearts);
                player.sendMessage(ChatColor.DARK_GREEN + getSkillName() + " will absorb " + absorptionHearts + " damage!");
                player.sendMessage(Utils.getPassiveCooldownMessage(getSkillName(), getCooldown()));
                setRemainingCooldown(getCooldown());
            }
        }
    }

    @Override
    public void initialize() {
        for (XRPGSkill skill : getXRPGPlayer().getDamageDealtEventHandler().getSkills()) {
            if (skill instanceof ArmyOfTheUndead) {
                this.armyOfTheUndead = (ArmyOfTheUndead) skill;
                return;
            }
        }
    }
}
