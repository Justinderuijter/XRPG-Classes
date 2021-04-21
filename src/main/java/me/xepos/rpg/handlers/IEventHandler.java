package me.xepos.rpg.handlers;

import me.xepos.rpg.classes.skills.XRPGSkill;

import java.util.List;

public interface IEventHandler {

    List<XRPGSkill> getSkills();

    void setSkills(List<XRPGSkill> skills);

    void addSkill(XRPGSkill skill);

    void removeSkill(XRPGSkill skill);

}
