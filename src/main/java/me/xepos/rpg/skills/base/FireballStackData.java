package me.xepos.rpg.skills.base;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

public class FireballStackData extends XRPGSkill {
    private byte fireBallStacks = 0;
    private final byte maxFireballStacks = (byte) getSkillVariables().getInt("max-stacks", 2);
    private long lastStackGained = System.currentTimeMillis();


    public FireballStackData(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        setRemainingCooldown(-1);
        xrpgPlayer.getEventHandler("RIGHT_CLICK").addSkill(this);
        xrpgPlayer.getEventHandler("SNEAK_RIGHT_CLICK").addSkill(this);
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
