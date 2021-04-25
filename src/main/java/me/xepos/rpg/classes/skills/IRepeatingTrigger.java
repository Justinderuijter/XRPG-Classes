package me.xepos.rpg.classes.skills;

public interface IRepeatingTrigger {

    int getInterval();

    void setInterval(int delay);

    byte getMaxProcs();

    void setMaxProcs(byte maxProcs);
}
