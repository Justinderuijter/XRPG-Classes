package me.xepos.rpg.classes.skills.wizard;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.configuration.WizardConfig;
import org.bukkit.event.Event;

public class FireballStackData extends XRPGSkill {
    private byte fireBallStacks = 0;
    private final byte maxFireballStacks = WizardConfig.getInstance().maxFireballStacks;
    private long lastStackGained = System.currentTimeMillis();


    public FireballStackData(XRPGPlayer xrpgPlayer, String skillName, XRPG plugin) {
        super(xrpgPlayer, skillName, plugin);

        setCooldown(-1);
        xrpgPlayer.getRightClickEventHandler().addSkill(this);
        xrpgPlayer.getSneakRightClickEventHandler().addSkill(this);
    }

    @Override
    public void activate(Event event) {

    }

    @Override
    public void initialize() {

    }

    public byte getFireBallStacks() {
        return fireBallStacks;
    }

    public void setFireBallStacks(byte fireBallStacks) {
        this.fireBallStacks = fireBallStacks;
    }

    public byte getMaxFireballStacks() {
        return maxFireballStacks;
    }

    public long getLastStackGained() {
        return lastStackGained;
    }

    public void setLastStackGained(long lastStackGained) {
        this.lastStackGained = lastStackGained;
    }
}
