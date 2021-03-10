package me.xepos.rpg.listeners;

import me.xepos.rpg.Utils;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.Necromancer;
import me.xepos.rpg.entities.NecromancerFollower;
import me.xepos.rpg.entities.type.*;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.lang.reflect.Constructor;

public class EntityListener implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        doNecromancerCheck(e);
    }

    //region Necromancer logic
    @SuppressWarnings("unchecked")
    public void doNecromancerCheck(EntityDeathEvent e)
    {
        if (((CraftEntity)e.getEntity()).getHandle() instanceof NecromancerFollower) {
            NecromancerFollower follower = (NecromancerFollower) ((CraftLivingEntity)e.getEntity()).getHandle();
            if (follower.getOwner() instanceof Player)
            {
                XRPGPlayer xrpgPlayer = Utils.GetRPG((Player) follower.getOwner());
                if (xrpgPlayer.getPlayerClass() instanceof Necromancer)
                {
                    Necromancer necromancer = (Necromancer) xrpgPlayer.getPlayerClass();
                    necromancer.followers.remove(follower);
                }
            }

        } else {
            if (e.getEntity().getKiller() != null && Utils.GetRPG(e.getEntity().getKiller()).getPlayerClass() instanceof Necromancer) {

                EntityTypes<? extends NecromancerFollower> type =
                        (EntityTypes<? extends NecromancerFollower>) ((CraftEntity) e.getEntity()).getHandle().getEntityType();

                Player owner = e.getEntity().getKiller();

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
                    }
                    catch (Exception ex)
                    {
                        if (entityTypeName.contains("llager")
                                || entityTypeName.equalsIgnoreCase("witch")
                                || entityTypeName.equalsIgnoreCase("vindicator")
                                || entityTypeName.equalsIgnoreCase("illusioner")
                                || entityTypeName.equalsIgnoreCase("evoker"))
                        {
                            instance = new FollowerZombieVillager(EntityTypes.ZOMBIE_VILLAGER, e.getEntity().getLocation(), owner);
                        }
                        else {
                            instance = new FollowerZombie(EntityTypes.ZOMBIE, e.getEntity().getLocation(), owner);
                        }

                    }finally {
                        //then do this stuff
                        if(instance != null)
                        {
                            NecromancerFollower follower = (NecromancerFollower)  instance;
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
    //endregion
}
