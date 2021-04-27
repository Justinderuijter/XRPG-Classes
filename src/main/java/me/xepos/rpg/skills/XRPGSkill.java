package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.dependencies.parties.IPartyManager;
import me.xepos.rpg.dependencies.protection.ProtectionSet;
import org.bukkit.configuration.ConfigurationSection;
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
    private final ConfigurationSection skillVariables;
    private long remainingCooldown;

    public XRPGSkill(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        this.xrpgPlayer = xrpgPlayer;
        this.plugin = plugin;
        //this.skillName = skillName;
        this.skillVariables = skillVariables;
        this.protectionSet = plugin.getProtectionSet();
        this.partyManager = plugin.getPartyManager();
        this.remainingCooldown = System.currentTimeMillis();

        if (this instanceof IFollowerContainer) {
            xrpgPlayer.getFollowerSkills().add((IFollowerContainer) this);
        }
    }


    public abstract void activate(Event event);

    public abstract void initialize();

    public long getRemainingCooldown() {
        return remainingCooldown;
    }

    public void setRemainingCooldown(double cooldownInSeconds) {
        this.remainingCooldown = System.currentTimeMillis() + ((long) cooldownInSeconds * 1000);
    }

    public String getSkillName() {
        return skillVariables.getString("name", "");
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
    public List<Player> getNearbyAlliedPlayers(Player caster, double x, double y, double z) {
        return (List<Player>) new ArrayList(caster.getWorld().getNearbyEntities(caster.getLocation(), x, y, z, p -> p instanceof Player && p != caster && partyManager.isPlayerAllied(caster, (Player) p)));
    }

    public XRPGPlayer getXRPGPlayer() {
        return xrpgPlayer;
    }

    public void setXRPGPlayer(XRPGPlayer xrpgPlayer) {
        this.xrpgPlayer = xrpgPlayer;
    }

    public double getCooldown() {
        return skillVariables.getDouble("cooldown", -1);
    }

    public double getDamage() {
        return skillVariables.getDouble("damage", 0);
    }

    public double getDamageMultiplier() {
        return skillVariables.getDouble("damage-multiplier");
    }

    public ConfigurationSection getSkillVariables() {
        return skillVariables;
    }
}
