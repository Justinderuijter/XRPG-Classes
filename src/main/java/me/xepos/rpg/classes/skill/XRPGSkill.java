package me.xepos.rpg.classes.skill;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.enums.SkillActivationType;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

public abstract class XRPGSkill {
    private List<SkillActivationType> activationTypes = new ArrayList<>();
    private long cooldown;
    private String skillName;
    private final XRPG plugin;

    public XRPGSkill(XRPG plugin, SkillActivationType activationType, String skillName) {
        this.plugin = plugin;
        this.activationTypes.add(activationType);
        this.skillName = skillName;
        this.cooldown = System.currentTimeMillis();
    }

    public XRPGSkill(XRPG plugin, List<SkillActivationType> activationTypes, String skillName) {
        this.plugin = plugin;
        this.activationTypes.addAll(activationTypes);
        this.skillName = skillName;
        this.cooldown = System.currentTimeMillis();
    }

    public abstract void activate(Event event);

    public abstract void initialize();

    public List<SkillActivationType> getActivationTypes() {
        return activationTypes;
    }

    public void setActivationTypes(List<SkillActivationType> activationTypes) {
        this.activationTypes = activationTypes;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
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
