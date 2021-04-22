package me.xepos.rpg;

import me.xepos.rpg.classes.Ranger;
import me.xepos.rpg.classes.XRPGClass;
import me.xepos.rpg.enums.DamageTakenSource;
import me.xepos.rpg.handlers.*;
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

    //Interact Handlers
    private RightClickEventHandler rightClickEventHandler = new RightClickEventHandler();
    private LeftClickEventHandler leftClickEventHandler = new LeftClickEventHandler();
    private SneakRightClickEventHandler sneakRightClickEventHandler = new SneakRightClickEventHandler();
    private SneakLeftClickEventHandler sneakLeftClickEventHandler = new SneakLeftClickEventHandler();

    //Interact Handlers (Entity)
    private RightClickEntityEventHandler rightClickEntityEventHandler = new RightClickEntityEventHandler();
    private SneakRightClickEntityEventHandler sneakRightClickEntityEventHandler = new SneakRightClickEntityEventHandler();
    //Damage Handlers
    private DamageTakenEventHandler damageTakenEventHandler = new DamageTakenEventHandler();
    private DamageDealtEventHandler damageDealtEventHandler = new DamageDealtEventHandler();
    //Bow Handlers
    private ShootBowEventHandler shootBowEventHandler = new ShootBowEventHandler();

    //Status Effects
    public transient ConcurrentHashMap<DamageTakenSource, Double> dmgTakenMultipliers = new ConcurrentHashMap<>();
    private transient boolean isStunned = false;
    private transient long lastStunTime = 0;

    //Constructor for loading profiles
    public XRPGPlayer(UUID playerId, XRPGClass XRPGClass) {
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

    public int getStunblockDuration() {
        return (int) ((lastStunTime - System.currentTimeMillis()) / 1000);
    }

    public boolean canBeStunned() {
        return System.currentTimeMillis() > lastStunTime + 20 * 1000L;
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

    //////////////////////////////////
    //                              //
    //  Handlers getters & setters  //
    //                              //
    //////////////////////////////////

    public RightClickEventHandler getRightClickEventHandler() {
        return rightClickEventHandler;
    }

    public LeftClickEventHandler getLeftClickEventHandler() {
        return leftClickEventHandler;
    }

    public SneakRightClickEventHandler getSneakRightClickEventHandler() {
        return sneakRightClickEventHandler;
    }

    public SneakLeftClickEventHandler getSneakLeftClickEventHandler() {
        return sneakLeftClickEventHandler;
    }

    public DamageTakenEventHandler getDamageTakenEventHandler() {
        return damageTakenEventHandler;
    }

    public DamageDealtEventHandler getDamageDealtEventHandler() {
        return damageDealtEventHandler;
    }

    public RightClickEntityEventHandler getRightClickEntityEventHandler() {
        return rightClickEntityEventHandler;
    }

    public SneakRightClickEntityEventHandler getSneakRightClickEntityEventHandler() {
        return sneakRightClickEntityEventHandler;
    }

    public ShootBowEventHandler getShootBowEventHandler() {
        return shootBowEventHandler;
    }
}
