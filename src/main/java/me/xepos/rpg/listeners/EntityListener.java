package me.xepos.rpg.listeners;

import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.Necromancer;
import me.xepos.rpg.entities.NecromancerFollower;
import me.xepos.rpg.utils.Utils;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

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

        }
    }
    //endregion
}
