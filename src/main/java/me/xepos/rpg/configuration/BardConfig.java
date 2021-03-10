package me.xepos.rpg.configuration;

import me.xepos.rpg.classes.Bard;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class BardConfig extends XRPGConfigFile {
    private static BardConfig instance;

    //Class values
    public int maxCastRange;
    public double damageMultiplier;
    public int phoenixsBlessingCooldown;
    public int soundBarrierCooldown;
    public int soundBarrierDuration;
    public int balladCooldown;
    public int balledProcDelay;
    public double balledHealPerProc;
    public byte balledMaxProcs;
    public int eGoldenAppleCooldown;
    public int goldenAppleCooldown;
    public int potionCooldown;

    public static BardConfig getInstance()
    {
        if (instance == null)
            instance = new BardConfig();

        return instance;
    }

    public void loadValues()
    {
        maxCastRange = get().getInt("maxCastRange", 16);
        damageMultiplier = get().getDouble("damageMultiplier", 0.8);
        phoenixsBlessingCooldown = get().getInt("phoenixsBlessingCooldown", 40);
        soundBarrierCooldown = get().getInt("soundBarrierCooldown", 120);
        soundBarrierDuration = get().getInt("soundBarrierDuration", 4);
        balladCooldown = get().getInt("balladCooldown", 15);
        balledProcDelay = get().getInt("balledProcDelay", 1);
        balledHealPerProc = get().getDouble("balledHealPerProc", 1.0);
        balledMaxProcs = (byte) get().getInt("balledMaxProcs", 10);
        eGoldenAppleCooldown = get().getInt("eGoldenAppleCooldown", 20);
        goldenAppleCooldown = get().getInt("goldenAppleCooldown", 5);
        potionCooldown = get().getInt("potionHealCooldown", 0);
    }

    public void setDefaults()
    {
        get().addDefault("maxCastRange", 16);
        get().addDefault("damageMultiplier", 0.8);
        get().addDefault("phoenixsBlessingCooldown", 40);
        get().addDefault("soundBarrierCooldown", 120);
        get().addDefault("soundBarrierDuration", 4);
        get().addDefault("balladCooldown", 15);
        get().addDefault("balledProcDelay", 1);
        get().addDefault("balledHealPerProc", 1.0);
        get().addDefault("balledMaxProcs", 10);
        get().addDefault("eGoldenAppleCooldown", 20);
        get().addDefault("goldenAppleCooldown", 5);
        get().addDefault("potionHealCooldown", 0);
    }

}
