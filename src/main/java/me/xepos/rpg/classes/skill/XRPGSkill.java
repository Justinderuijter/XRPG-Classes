package me.xepos.rpg.classes.skill;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.enums.SkillActivationType;
import org.bukkit.event.Event;

public abstract class XRPGSkill {
    private SkillActivationType activationType;
    private long cooldown;
    private String skillName;
    private final XRPG plugin;

    public XRPGSkill(XRPG plugin, SkillActivationType activationType, String skillName) {
        this.plugin = plugin;
        this.activationType = activationType;
        this.skillName = skillName;
        this.cooldown = System.currentTimeMillis();
    }

    public abstract void activate(Event event);

    public abstract void initialize();

    public SkillActivationType getActivationType() {
        return activationType;
    }

    public void setActivationType(SkillActivationType activationType) {
        this.activationType = activationType;
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldownInSeconds) {
        this.cooldown = System.currentTimeMillis() + (cooldownInSeconds * 1000L);
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public boolean isSkillReady() {
        return cooldown <= System.currentTimeMillis();
    }

    public XRPG getPlugin() {
        return this.plugin;
    }
}
