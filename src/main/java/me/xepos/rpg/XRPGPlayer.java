package me.xepos.rpg;

import me.xepos.rpg.classes.*;
import me.xepos.rpg.enums.DamageTakenSource;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class XRPGPlayer {
    private transient UUID playerId;
    private transient Player player;
    private transient XRPGClass playerClass;
    private String classId;
    private int freeChangeTickets = 2;

    public transient ConcurrentHashMap<DamageTakenSource, Double> dmgTakenMultipliers = new ConcurrentHashMap<>();
    private transient boolean isStunned = false;
    private transient long lastStunTime = 0;

    //Constructor for loading profiles
    public XRPGPlayer(UUID playerId, XRPGClass XRPGClass)
    {
        this.player = null;
        this.playerId = playerId;
        this.playerClass = XRPGClass;
        this.classId = XRPGClass.toString();
    }


    public XRPGPlayer(Player player, XRPGClass XRPGClass)
    {
        this.player = player;
        this.playerId = player.getUniqueId();
        this.playerClass = XRPGClass;
        this.classId = XRPGClass.toString();
    }

    //Constructor for new profiles
    public XRPGPlayer(UUID playerId, XRPG plugin)
    {
        this.player = null;
        this.playerId = playerId;
        this.playerClass = new Ranger(plugin);
        this.classId = playerClass.toString();
    }


    public void onHit(EntityDamageByEntityEvent e)
    {
        playerClass.onHit(e); // call class HitModifier/Effect
    }

    public void onHurt(EntityDamageByEntityEvent e)
    {
        playerClass.onHurt(e); // call class dmgTaken Modifier
    }

    public void onJoin(PlayerJoinEvent e)
    {
        playerClass.onJoin(e); //Calls class effect when player joins
    }

    public void onRespawn(PlayerRespawnEvent e)
    {
        playerClass.onRespawn(e); //Calls class effect when player joins
    }

    public void onPlayerConsumeItem(PlayerItemConsumeEvent e)
    {
        playerClass.onPlayerConsumeItem(e);
    }
    public void onUseItem(PlayerInteractEvent e)
    {
        playerClass.onUseItem(e);
    }
    public void onInteractWithEntity(PlayerInteractEntityEvent e)
    {
        playerClass.onInteractWithEntity(e);
    }
    public void onProjectileLaunch(ProjectileLaunchEvent e)
    {
        playerClass.onProjectileLaunch(e);
    }
    public void onShootBow(EntityShootBowEvent e){ playerClass.onShootBow(e);}

    public XRPGClass getPlayerClass() {
        return playerClass;
    }

    public void setPlayerClass(XRPGClass playerClass) {
        this.playerClass = playerClass;
        this.classId = playerClass.toString();
    }

    public int getFreeChangeTickets() {
        return freeChangeTickets;
    }

    public void setFreeChangeTickets(int freeChangeTickets) {
        this.freeChangeTickets = freeChangeTickets;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isStunned() {
        return isStunned;
    }

    public void setStunned(boolean stunned) {
        isStunned = stunned;
        if (stunned)
            lastStunTime = System.currentTimeMillis();
    }

    public boolean canBeStunned() {
        return System.currentTimeMillis() > lastStunTime + 10 * 1000L;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }
}
