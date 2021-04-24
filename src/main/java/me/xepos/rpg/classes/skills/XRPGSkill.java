package me.xepos.rpg.classes.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.dependencies.parties.IPartyManager;
import me.xepos.rpg.dependencies.protection.ProtectionSet;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

public abstract class XRPGSkill {

    private XRPGPlayer xrpgPlayer;
    private final XRPG plugin;
    private final ProtectionSet protectionSet;
    private final IPartyManager partyManager;

    //Stats
    private long remainingCooldown;
    private String skillName;
    private int cooldown;

    public XRPGSkill(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin) {
        this.xrpgPlayer = xrpgPlayer;
        this.plugin = plugin;
        this.skillName = skillName;
        this.cooldown = cooldown;
        this.protectionSet = plugin.getProtectionSet();
        this.partyManager = plugin.getPartyManager();
        this.remainingCooldown = System.currentTimeMillis();
    }


    public abstract void activate(Event event);

    public abstract void initialize();

    public long getRemainingCooldown() {
        return remainingCooldown;
    }

    public void setRemainingCooldown(int cooldownInSeconds) {
        this.remainingCooldown = System.currentTimeMillis() + (cooldownInSeconds * 1000L);
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public boolean isSkillReady() {
        return remainingCooldown <= System.currentTimeMillis();
    }

    public XRPG getPlugin() {
        return this.plugin;
    }

    public ProtectionSet getProtectionSet() {
        return protectionSet;
    }

    public IPartyManager getPartyManager() {
        return partyManager;
    }

    @SuppressWarnings("all")
    public List<Player> getNearbyAlliedPlayers(Player caster, int x, int y, int z) {
        return (List<Player>) new ArrayList(caster.getWorld().getNearbyEntities(caster.getLocation(), x, y, z, p -> p instanceof Player && p != caster && partyManager.isPlayerAllied(caster, (Player) p)));
    }

    public XRPGPlayer getXRPGPlayer() {
        return xrpgPlayer;
    }

    public void setXRPGPlayer(XRPGPlayer xrpgPlayer) {
        this.xrpgPlayer = xrpgPlayer;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }
}
