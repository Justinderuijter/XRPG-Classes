package me.xepos.rpg.listeners;

import me.xepos.rpg.*;
import me.xepos.rpg.database.IDatabaseManager;
import me.xepos.rpg.database.tasks.savePlayerDataTask;
import me.xepos.rpg.dependencies.IPartyManager;
import me.xepos.rpg.enums.DamageTakenSource;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;

import java.util.UUID;


public class ClassListener implements Listener
{
    private final XRPG plugin;
    private final IDatabaseManager databaseManager;

    public ClassListener(XRPG plugin, IDatabaseManager databaseManager)
    {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e)
    {

        if(e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity)
        {
            XRPGPlayer xrpgPlayer = Utils.GetRPG((Player)e.getDamager());
            if (xrpgPlayer.isStunned())
                e.setCancelled(true);
            else
                xrpgPlayer.onHit(e);
        }

        if (e.getEntity() instanceof Player)
        {
            Player player = (Player)e.getEntity();
            player.sendMessage("You have been hurt!");
            XRPGPlayer xrpgPlayer = Utils.GetRPG(player);
            if (xrpgPlayer.dmgTakenMultipliers.size() > 0)
            {
                for (DamageTakenSource dtSource:xrpgPlayer.dmgTakenMultipliers.keySet()) {
                    e.setDamage(e.getDamage() * xrpgPlayer.dmgTakenMultipliers.get(dtSource));
                }
            }
            xrpgPlayer.onHurt(e);
        }
    }

    @EventHandler
    public void onPrePlayerJoin(AsyncPlayerPreLoginEvent e){
        if (e.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED){
            this.databaseManager.loadPlayerData(e.getUniqueId());
        }

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        Player player = e.getPlayer();
        XRPGPlayer xrpgPlayer = null;
        if (XRPG.RPGPlayers.containsKey(player.getUniqueId()))
        {
            xrpgPlayer = Utils.GetRPG(player);
            xrpgPlayer.setPlayer(player);
        }

        if (xrpgPlayer != null && xrpgPlayer.getPlayer() != null){
            player.sendMessage("You are now " + XRPG.RPGPlayers.get(player.getUniqueId()).getPlayerClass().toString());
            Utils.GetRPG(player).onJoin(e);
        }
        else {
            player.kickPlayer("Something went wrong while loading XRPG data.");
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e)
    {
        UUID playerId = e.getPlayer().getUniqueId();
        XRPGPlayer xrpgPlayer = XRPG.RPGPlayers.get(playerId);
        new savePlayerDataTask(databaseManager, xrpgPlayer).runTaskAsynchronously(plugin);
        XRPG.RPGPlayers.remove(playerId);
    }


    @EventHandler
    public void onPlayerConsumeItem(PlayerItemConsumeEvent e)
    {
        Player player = e.getPlayer();
        Utils.GetRPG(player).onPlayerConsumeItem(e);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent e)
    {
        Player player = e.getPlayer();
        Utils.GetRPG(player).onUseItem(e);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e)
    {
        Player player = e.getPlayer();
        Utils.GetRPG(player).onInteractWithEntity(e);
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e)
    {
        if (e.getEntity().getShooter() instanceof Player)
        {
            Player player = (Player)e.getEntity().getShooter();
            Utils.GetRPG(player).onProjectileLaunch(e);
        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent e){
        if (e.getEntity() instanceof Player)
        {
            Player player = (Player) e.getEntity();
            Utils.GetRPG(player).onShootBow(e);
        }
    }
}
