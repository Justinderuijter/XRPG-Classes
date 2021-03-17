package me.xepos.rpg.configuration;

public final class NecromancerConfig extends XRPGConfigFile {
    private static NecromancerConfig instance;

    //Class values
    public byte hoeFlatDamageBonus;
    public byte maxFollowers;
    public int shadowSneakCooldown;
    public double shadowSneakDamage;
    public byte shadowSneakTicks;
    public double shadowSneakDamagePerTick;
    public byte shadowSneakBatCount;
    public byte batDespawnDelay;
    public int boneShieldCooldown;
    public byte shieldPerFollower;
    public int purgatoryBatCooldown;
    public byte purgatoryBatDuration;
    public double purgatoryBatDps;
    public double purgatoryBatDTAmount;
    public boolean isBatDmgSource;

    public static NecromancerConfig getInstance()
    {
        if (instance == null)
            instance = new NecromancerConfig();

        return instance;
    }


    public void loadValues()
    {
        shadowSneakCooldown = get().getInt("shadowSneakCooldown", 12);
        shadowSneakDamage = get().getDouble("shadowSneakDamage", 6.0);
        maxFollowers = (byte) get().getInt("maxFollowers", 3);
        hoeFlatDamageBonus = (byte) get().getInt("hoeFlatDamageBonus", 3);
        shadowSneakTicks = (byte) get().getInt("shadowSneakTicks", 5);
        shadowSneakDamagePerTick = get().getDouble("shadowSneakDamagePerTick", 2.0);
        shadowSneakBatCount = (byte)get().getInt("shadowSneakBatCount", 10);
        batDespawnDelay = (byte) get().getInt("batDespawnDelay", 3);
        boneShieldCooldown = get().getInt("boneShieldCooldown", 20);
        shieldPerFollower = (byte) get().getInt("shieldPerFollower", 2);
        purgatoryBatCooldown = get().getInt("purgatoryBatCooldown", 7);
        purgatoryBatDuration = (byte) get().getInt("purgatoryBatDuration", 5);
        purgatoryBatDps = get().getDouble("purgatoryBatDps", 4.0);
        purgatoryBatDTAmount = get().getDouble("purgatoryBatDamageTakenModifier", 1.2);
        isBatDmgSource = get().getBoolean("isBatDmgSource", false);
    }

    public void setDefaults()
    {
        get().addDefault("hoeFlatDamageBonus", 3);
        get().addDefault("maxFollowers", 3);
        get().addDefault("boneShieldCooldown", 20);
        get().addDefault("shieldPerFollower", 2);
        get().addDefault("shadowSneakCooldown", 12);
        get().addDefault("shadowSneakDamage", 6.0);
        get().addDefault("shadowSneakTicks", 5);
        get().addDefault("shadowSneakDamagePerTick", 2.0);
        get().addDefault("shadowSneakBatCount", 10);
        get().addDefault("batDespawnDelay", 3);
        get().addDefault("purgatoryBatCooldown", 7);
        get().addDefault("purgatoryBatDuration", 5);
        get().addDefault("purgatoryBatDps", 4.0);
        get().addDefault("purgatoryBatDamageTakenModifier", 1.2);
        get().addDefault("isBatDmgSource", false);
    }

}
