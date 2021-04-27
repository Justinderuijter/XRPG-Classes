package me.xepos.rpg;

import me.xepos.rpg.classes.skills.IFollowerContainer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.enums.DamageTakenSource;
import me.xepos.rpg.handlers.EventHandler;
import me.xepos.rpg.handlers.ShootBowEventHandler;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class XRPGPlayer {
    private transient UUID playerId;
    private transient Player player;
    private String classId;
    private String classDisplay;
    private int freeChangeTickets = 2;

    //For convenience
    private List<IFollowerContainer> followerSkills = new ArrayList<>();

    private final HashMap<String, EventHandler> handlerList = new HashMap<String, EventHandler>() {{
        //Interact Handlers
        put("RIGHT_CLICK", new EventHandler());
        put("LEFT_CLICK", new EventHandler());
        put("SNEAK_RIGHT_CLICK", new EventHandler());
        put("SNEAK_LEFT_CLICK", new EventHandler());

        //Interact Handlers (Entity)
        put("RIGHT_CLICK_ENTITY", new EventHandler());
        put("SNEAK_RIGHT_CLICK_ENTITY", new EventHandler());

        //Damage Handlers
        put("DAMAGE_TAKEN", new EventHandler());
        put("DAMAGE_DEALT", new EventHandler());

        //Bow Handlers
        put("SHOOT_BOW", new ShootBowEventHandler());

        //Other Handlers
        put("CONSUME_ITEM", new EventHandler());
    }};

    //Status Effects
    public transient ConcurrentHashMap<DamageTakenSource, Double> dmgTakenMultipliers = new ConcurrentHashMap<>();
    private transient boolean isStunned = false;
    private transient long lastStunTime = 0;

    //Constructor for loading profiles
    public XRPGPlayer(UUID playerId, String classId) {
        this.player = null;
        this.playerId = playerId;
        this.classId = classId;
    }


    public XRPGPlayer(Player player, String classId) {
        this.player = player;
        this.playerId = player.getUniqueId();
        this.classId = classId;
    }

    //Constructor for new profiles
    public XRPGPlayer(UUID playerId) {
        this.player = null;
        this.playerId = playerId;
        this.classId = "assassin";
    }

    public void setPlayerClass(String classId) {
        this.classId = classId;
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

    public List<IFollowerContainer> getFollowerSkills() {
        return followerSkills;
    }

    public void setFollowerSkills(List<XRPGSkill> skills) {
        followerSkills.clear();
        for (XRPGSkill skill : skills) {
            if (skill instanceof IFollowerContainer) {
                followerSkills.add((IFollowerContainer) skill);
            }
        }
    }

    public String getClassDisplayName() {
        return classDisplay;
    }

    public void setClassDisplayName(String classDisplay) {
        this.classDisplay = classDisplay;
    }

    //////////////////////////////////
    //                              //
    //  Handlers getters & setters  //
    //                              //
    //////////////////////////////////

    public EventHandler getEventHandler(String handlerName) {
        return handlerList.get(handlerName.toUpperCase());
    }

    public void addEventHandler(String handlerName, EventHandler handler) {
        this.handlerList.put(handlerName.toUpperCase(), handler);
    }
}
