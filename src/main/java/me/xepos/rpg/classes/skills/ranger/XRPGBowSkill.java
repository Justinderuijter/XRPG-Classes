package me.xepos.rpg.classes.skills.ranger;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;

public abstract class XRPGBowSkill extends XRPGSkill {
    public XRPGBowSkill(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin) {
        super(xrpgPlayer, skillName, cooldown, plugin);
    }

    public void enable() {

    }

    public void disable() {

    }
}
