package me.xepos.rpg.listeners;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.database.IDatabaseManager;
import me.xepos.rpg.database.tasks.savePlayerDataTask;
import me.xepos.rpg.enums.DamageTakenSource;
import me.xepos.rpg.handlers.ShootBowEventHandler;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;


public class ClassListener implements Listener {
    private final XRPG plugin;
    private final IDatabaseManager databaseManager;

    public ClassListener(XRPG plugin, IDatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
    }

    //Giving other plugins more opportunity to cancel this event
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent e) {

        if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer((Player) e.getDamager());
            if (xrpgPlayer.isStunned())
                e.setCancelled(true);
            else
                xrpgPlayer.getEventHandler("DAMAGE_DEALT").invoke(e);
        }

        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            player.sendMessage("You have been hurt!");
            XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(player);
            if (xrpgPlayer.dmgTakenMultipliers.size() > 0) {
                for (DamageTakenSource dtSource : xrpgPlayer.dmgTakenMultipliers.keySet()) {
                    e.setDamage(e.getDamage() * xrpgPlayer.dmgTakenMultipliers.get(dtSource));
                }
            }
            xrpgPlayer.getEventHandler("DAMAGE_TAKEN").invoke(e);
            //xrpgPlayer.onHurt(e);
        }
    }

    @EventHandler
    public void onPrePlayerJoin(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            this.databaseManager.loadPlayerData(e.getUniqueId());
        }

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        XRPGPlayer xrpgPlayer = null;
        if (plugin.getRPGPlayers().containsKey(player.getUniqueId())) {
            xrpgPlayer = plugin.getXRPGPlayer(player);
            xrpgPlayer.setPlayer(player);
        }

        if (xrpgPlayer != null && xrpgPlayer.getPlayer() != null) {
            player.sendMessage("You are now " + xrpgPlayer.getClassDisplayName());
            Utils.onJoinEffect(player);
        } else {
            player.kickPlayer("Something went wrong while loading XRPG data.");
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        UUID playerId = e.getPlayer().getUniqueId();
        XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(playerId);
        new savePlayerDataTask(databaseManager, xrpgPlayer).runTaskAsynchronously(plugin);
        plugin.removeXRPGPlayer(playerId);
    }


    @EventHandler
    public void onPlayerConsumeItem(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();
        plugin.getXRPGPlayer(player).getEventHandler("CONSUME_ITEM");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(e.getPlayer());
        //Utils.GetRPG(player).onUseItem(e);


        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getPlayer().isSneaking()) {
                xrpgPlayer.getEventHandler("SNEAK_RIGHT_CLICK").invoke(e);
            } else {
                xrpgPlayer.getEventHandler("RIGHT_CLICK").invoke(e);
            }

        } else if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {

            doBowCycle(e.getItem(), xrpgPlayer);

            if (e.getPlayer().isSneaking()) {
                xrpgPlayer.getEventHandler("SNEAK_LEFT_CLICK").invoke(e);
            } else {
                xrpgPlayer.getEventHandler("LEFT_CLICK").invoke(e);
            }
        }

    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(e.getPlayer());
        if (e.getPlayer().isSneaking()) {
            xrpgPlayer.getEventHandler("SNEAK_RIGHT_CLICK_ENTITY").invoke(e);
        } else {
            xrpgPlayer.getEventHandler("RIGHT_CLICK_ENTITY").invoke(e);
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
//        if (e.getEntity().getShooter() instanceof Player) {
//            Player player = (Player) e.getEntity().getShooter();
//            Utils.GetRPG(player).onProjectileLaunch(e);
//        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            plugin.getXRPGPlayer(player).getEventHandler("SHOOT_BOW").invoke(e);
        }
    }

    private void doBowCycle(ItemStack item, XRPGPlayer xrpgPlayer) {
        if (item != null && item.getType() == Material.BOW) {
            ((ShootBowEventHandler) xrpgPlayer.getEventHandler("SHOOT_BOW")).next();
        }
    }
}
