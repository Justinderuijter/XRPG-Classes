package me.xepos.rpg.configuration;


public final class WizardConfig extends XRPGConfigFile {
    private static WizardConfig instance;

    //Class values
    public byte maxCastRange;
    public byte maxFireballStacks;
    public int smallFireballCooldown;
    public double smallFireballDamage;
    public int smallFireballFireTicks;
    public int meteorCooldown;
    public double meteorExplosionStrength;
    public boolean meteorDamageBlocks;
    public boolean meteorSetFire;
    public int shatterCooldown;
    public int shatterDuration;
    public int zephyrCooldown;
    public int zephyrBaseDuration;
    public int forcefieldCooldown;

    public static WizardConfig getInstance()
    {
        if (instance == null)
            instance = new WizardConfig();

        return instance;
    }

    public void loadValues()
    {
        maxCastRange = (byte) get().getInt("maxCastRange", 32);
        maxFireballStacks = (byte) get().getInt("maxFireballStacks", 2);
        smallFireballCooldown = get().getInt("smallFireballCooldown", 3);
        smallFireballDamage = get().getDouble("smallFireballDamage");
        smallFireballFireTicks = get().getInt("smallFireballFireTicks", 60);
        meteorCooldown = get().getInt("meteorCooldown");
        meteorExplosionStrength = get().getDouble("meteorExplosionStrength");
        meteorDamageBlocks = get().getBoolean("meteorDamageBlocks", false);
        meteorSetFire = get().getBoolean("meteorSetFire", false);
        shatterCooldown = get().getInt("shatterCooldown");
        shatterDuration = get().getInt("shatterDuration", 4);
        zephyrCooldown = get().getInt("zephyrCooldown");
        zephyrBaseDuration = get().getInt("zephyrBaseDuration", 60);
        forcefieldCooldown = get().getInt("forcefieldCooldown", 10);

    }

    public void setDefaults() {
        get().addDefault("maxCastRange", 32);
        get().addDefault("maxFireballStacks", 2);
        get().addDefault("smallFireballCooldown", 3);
        get().addDefault("smallFireballDamage", 1.0);
        get().addDefault("smallFireballFireTicks", 60);
        get().addDefault("meteorCooldown", 15);
        get().addDefault("meteorExplosionStrength", 2.0);
        get().addDefault("meteorDamageBlocks", false);
        get().addDefault("meteorSetFire", false);
        get().addDefault("shatterCooldown", 10);
        get().addDefault("shatterDuration", 4);
        get().addDefault("zephyrCooldown", 10);
        get().addDefault("zephyrBaseDuration", 60);
        get().addDefault("forcefieldCooldown", 10);

    }
}
