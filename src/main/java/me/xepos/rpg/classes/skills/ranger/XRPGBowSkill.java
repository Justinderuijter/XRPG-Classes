package me.xepos.rpg.classes.skills.ranger;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import org.bukkit.configuration.ConfigurationSection;

public abstract class XRPGBowSkill extends XRPGSkill {
    public XRPGBowSkill(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);
    }

    public void enable() {

    }

    public void disable() {

    }
}
