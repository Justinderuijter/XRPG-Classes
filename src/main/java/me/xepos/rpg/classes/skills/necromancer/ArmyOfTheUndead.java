package me.xepos.rpg.classes.skills.necromancer;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.entities.NecromancerFollower;
import net.minecraft.server.v1_16_R3.EntityLiving;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.ArrayList;
import java.util.List;

public class ArmyOfTheUndead extends XRPGSkill {
    private List<NecromancerFollower> followers = new ArrayList<>();

    public ArmyOfTheUndead(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin) {
        super(xrpgPlayer, skillName, cooldown, plugin);

        xrpgPlayer.getDamageDealtEventHandler().addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

        for (NecromancerFollower follower : followers) {
            EntityLiving entityLiving = ((CraftLivingEntity) e.getEntity()).getHandle();
            if (entityLiving instanceof NecromancerFollower) {
                if (!followers.contains(entityLiving))
                    follower.setGoalTarget(entityLiving, EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true);
            } else {
                follower.setGoalTarget(entityLiving, EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true);
            }
        }
    }

    @Override
    public void initialize() {

    }

    public List<NecromancerFollower> getFollowers() {
        return followers;
    }

    public int getFollowerCount() {
        return followers.size();
    }
}
