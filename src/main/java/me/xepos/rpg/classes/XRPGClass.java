package me.xepos.rpg.classes;

import me.xepos.rpg.Utils;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.dependencies.IPartyManager;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;

public abstract class XRPGClass {

    public transient IPartyManager partyManager;
    public transient XRPG plugin;

    public XRPGClass(XRPG plugin) {
        this.plugin = plugin;
        this.partyManager = plugin.getPartyManager();
    }

    public abstract void onHit(EntityDamageByEntityEvent e);

    public abstract void onHurt(EntityDamageByEntityEvent e);

    public abstract void onJoin(PlayerJoinEvent e);

    public abstract void onRespawn(PlayerRespawnEvent e);
    public abstract void onPlayerConsumeItem(PlayerItemConsumeEvent e);
    public abstract void onUseItem(PlayerInteractEvent e);
    public abstract void onProjectileLaunch(ProjectileLaunchEvent e);
    public abstract void onInteractWithEntity(PlayerInteractEntityEvent e);
    public abstract void onShootBow(EntityShootBowEvent e);

    public void applyEffects(Player player)
    {
        player.sendMessage("Applied join effects");
        Utils.onJoinEffect(player);
    }

    public String toString()
    {
        return this.getClass().getSimpleName();
    }


}
