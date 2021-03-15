package me.xepos.rpg.datatypes;

public class fireballData implements IClearable {
    private final double damage;
    private final long despawnTime;

    public fireballData(double damage, int secondsToLive) {
        this.damage = damage;
        this.despawnTime = System.currentTimeMillis() + secondsToLive * 1000L;
    }

    public double getDamage() {
        return damage;
    }

    @Override
    public boolean shouldRemove() {
        return despawnTime < System.currentTimeMillis();
    }

}
