package me.xepos.rpg.tasks;

import me.xepos.rpg.Utils;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.configuration.RavagerConfig;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class RavagerLandTask extends BukkitRunnable
{

    private final Player player;
    public RavagerLandTask(Player player)
    {
        this.player = player;
    }

    @Override
    public void run() {
        if (!player.isOnline())
        {
            this.cancel();
            return;
        }

        if (!player.isFlying() && player.getLocation().subtract(0, 0.1, 0).getBlock().getType().isSolid())
        {
            List<Entity> entities =  new ArrayList<>(player.getLocation().getWorld().getNearbyEntities(player.getLocation(), 4, 2, 4, p -> p instanceof LivingEntity && p != player));
            for (Entity entity:entities) {
                LivingEntity livingEntity = (LivingEntity) entity;
                livingEntity.damage(10, player);
                livingEntity.addPotionEffect(RavagerConfig.getInstance().slowEffect);
            }

            this.cancel();
        }
    }
}
