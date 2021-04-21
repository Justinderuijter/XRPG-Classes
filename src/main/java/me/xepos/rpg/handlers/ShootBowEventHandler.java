package me.xepos.rpg.handlers;

import me.xepos.rpg.classes.skills.XRPGSkill;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.ArrayList;
import java.util.List;

public class ShootBowEventHandler implements IEventHandler {
    private List<XRPGSkill> skills = new ArrayList<>();

    public List<XRPGSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<XRPGSkill> skills) {
        this.skills = skills;
    }

    public void addSkill(XRPGSkill skill) {
        if (!skills.contains(skill))
            skills.add(skill);
    }

    public void removeSkill(XRPGSkill skill) {
        skills.remove(skill);
    }

    public void invoke(EntityShootBowEvent e) {
        for (XRPGSkill skill : skills) {
            skill.activate(e);
        }
    }
}
