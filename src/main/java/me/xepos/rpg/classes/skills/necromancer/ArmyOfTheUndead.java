package me.xepos.rpg.classes.skills.necromancer;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.Necromancer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.entities.NecromancerFollower;
import me.xepos.rpg.entities.type.FollowerZombie;
import me.xepos.rpg.entities.type.FollowerZombieVillager;
import me.xepos.rpg.utils.Utils;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ArmyOfTheUndead extends XRPGSkill {
    private final List<NecromancerFollower> followers = new ArrayList<>();

    public ArmyOfTheUndead(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin) {
        super(xrpgPlayer, skillName, cooldown, plugin);

        xrpgPlayer.getDamageDealtEventHandler().addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

        if (!(e.getEntity() instanceof LivingEntity)) return;
        LivingEntity livingEntity = (LivingEntity) e.getEntity();

        if (livingEntity.getHealth() <= e.getFinalDamage()) {

        }


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

    @SuppressWarnings("unchecked")
    private void doSomething(EntityDamageByEntityEvent e) {
        if (Utils.GetRPG((Player) e.getDamager()).getDamageDealtEventHandler()) {

            EntityTypes<? extends NecromancerFollower> type =
                    (EntityTypes<? extends NecromancerFollower>) ((CraftEntity) e.getEntity()).getHandle().getEntityType();

            Player owner = (Player) e.getDamager();

            if (owner != null) {
                owner.sendMessage("Owner not null!");


                Object instance = null;
                String entityTypeName = Utils.enumTypeFormatter(e.getEntity().getType().name(), "_");

                owner.sendMessage("me.xepos.rpg.entities.type.Follower" + entityTypeName);
                owner.sendMessage(type.toString());

                try {
                    //Get supported type
                    Class<?> clazz = Class.forName("me.xepos.rpg.entities.type.Follower" + entityTypeName);
                    Constructor<?> constructor = clazz.getConstructor(EntityTypes.class, Location.class, LivingEntity.class);
                    instance = constructor.newInstance(type, e.getEntity().getLocation(), owner);
                } catch (Exception ex) {
                    if (entityTypeName.contains("llager")
                            || entityTypeName.equalsIgnoreCase("witch")
                            || entityTypeName.equalsIgnoreCase("vindicator")
                            || entityTypeName.equalsIgnoreCase("illusioner")
                            || entityTypeName.equalsIgnoreCase("evoker")) {
                        instance = new FollowerZombieVillager(EntityTypes.ZOMBIE_VILLAGER, e.getEntity().getLocation(), owner);
                    } else {
                        instance = new FollowerZombie(EntityTypes.ZOMBIE, e.getEntity().getLocation(), owner);
                    }

                } finally {
                    //then do this stuff
                    if (instance != null) {
                        NecromancerFollower follower = (NecromancerFollower) instance;
                        //follower.setOwner(owner);

                        Necromancer necromancer = (Necromancer) Utils.GetRPG(e.getEntity().getKiller()).getPlayerClass();
                        necromancer.followers.add(follower);

                        WorldServer world = ((CraftWorld) owner.getWorld()).getHandle();
                        world.addEntity(follower);
                    }
                }

            }
        }
    }
}
