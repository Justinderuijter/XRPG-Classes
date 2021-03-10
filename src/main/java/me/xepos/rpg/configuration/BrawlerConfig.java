package me.xepos.rpg.configuration;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class BrawlerConfig extends XRPGConfigFile {
    private static BrawlerConfig instance;

    //Class values
    public int triggerAmount;
    public int effectDuration;
    public double innerStrengthHealAmount;
    public double lotusModifier;
    public double vitalModifier;
    public int fistBaseDamage;
    public int nonFistDamage;
    public double enchantToCritRatio;
    public double enchantToLotusHasteRatio;
    public int fireTicksPerEnchantLevel;
    public double armorToDamageRatio;
    public double toughnessToDamageRatio;
    public double protectionToDamageRatio;

    public static BrawlerConfig getInstance()
    {
        if (instance == null)
            instance = new BrawlerConfig();

        return instance;
    }


    public void loadValues()
    {
        fistBaseDamage = get().getInt("fistBaseDamage", 2);
        nonFistDamage = get().getInt("nonFistDamage", 0);
        triggerAmount = get().getInt("triggerAmount", 6);
        effectDuration = get().getInt("effectDuration");
        innerStrengthHealAmount = get().getDouble("innerStrengthHealAmount");
        lotusModifier = get().getDouble("lotusModifier");
        vitalModifier = get().getDouble("vitalModifier");
        enchantToCritRatio = get().getDouble("enchantToCritRatio");
        enchantToLotusHasteRatio = get().getDouble("enchantToLotusHasteRatio");
        fireTicksPerEnchantLevel = get().getInt("fireTicksPerEnchantLevel");
        armorToDamageRatio = get().getDouble("armorToDamageRatio", 0.333);
        toughnessToDamageRatio = get().getDouble("toughnessToDamageRatio", 0.25);
        protectionToDamageRatio = get().getDouble("protectionToDamageRatio", 0.25);

    }

    public void setDefaults()
    {
        get().addDefault("fistBaseDamage", 2);
        get().addDefault("nonFistDamage", 0);
        get().addDefault("triggerAmount", 6);
        get().addDefault("effectDuration", 6);
        get().addDefault("innerStrengthHealAmount", 4.0);
        get().addDefault("lotusModifier", 1.5);
        get().addDefault("vitalModifier", 1.5);
        get().addDefault("poisonAmplifier", 1);
        get().addDefault("backStrikeMultiplier", 1.3);
        get().addDefault("enchantToCritRatio", 3.125);
        get().addDefault("enchantToLotusHasteRatio", 2.0);
        get().addDefault("fireTicksPerEnchantLevel", 15);
        get().addDefault("armorToDamageRatio", 0.333);
        get().addDefault("toughnessToDamageRatio", 0.25);
        get().addDefault("protectionToDamageRatio", 0.25);

    }

}
