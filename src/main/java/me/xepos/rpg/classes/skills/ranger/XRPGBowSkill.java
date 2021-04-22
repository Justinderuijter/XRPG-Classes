package me.xepos.rpg.classes.skills.ranger;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;

public abstract class XRPGBowSkill extends XRPGSkill {
    public XRPGBowSkill(XRPGPlayer xrpgPlayer, String skillName, XRPG plugin) {
        super(xrpgPlayer, skillName, plugin);
    }

    private boolean belongsToCollection = false;

    public boolean isBelongsToCollection() {
        return belongsToCollection;
    }

    public void setBelongsToCollection(boolean belongsToCollection) {
        if (belongsToCollection) {
            if (!getXRPGPlayer().getShootBowEventHandler().getSkills().contains(this)) {
                getXRPGPlayer().getShootBowEventHandler().getSkills().add(this);
            }
        } else {
            getXRPGPlayer().getShootBowEventHandler().getSkills().remove(this);
        }
        this.belongsToCollection = belongsToCollection;
    }
}
