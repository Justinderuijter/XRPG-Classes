package me.xepos.rpg.configuration;

import me.xepos.rpg.AttributeModifierManager;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public final class GuardianConfig extends XRPGConfigFile {
    private static GuardianConfig instance;

    //Class values
    public AttributeModifier healthModifier;
    public AttributeModifier stunEffectModifier;
    public double dmgTakenMultiplier;
    public int aegisCooldown;
    public int aegisDuration;
    public int aegisAmplifier;
    public double mobAggroRangeOnHit;
    public double aegisRangeHorizontal;
    public double aegisRangeVertical;
    public int shieldBashCooldown;

    public static GuardianConfig getInstance()
    {
        if (instance == null)
            instance = new GuardianConfig();

        return instance;
    }


    public void loadValues()
    {
        double maxHealth = get().getDouble("healthMultiplier", 2.0) - 1.0;
        healthModifier = new AttributeModifier(UUID.fromString("71ae50ba-b6e2-467c-9017-b83f837380dc"), "health", maxHealth, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
        stunEffectModifier = new AttributeModifier(UUID.fromString("076c8ed9-b6e2-4da1-a4c0-27c50c61725d"), "stun", -1, AttributeModifier.Operation.MULTIPLY_SCALAR_1);

        dmgTakenMultiplier = get().getDouble("dmgTakenMultiplier", 0.5);
        aegisCooldown = get().getInt("aegisCooldown", 20);
        aegisDuration = get().getInt("aegisDuration", 4);
        mobAggroRangeOnHit = get().getDouble("mobAggroRangeOnHit");
        aegisAmplifier = get().getInt("aegisAmplifier", 3);
        aegisRangeHorizontal = get().getDouble("aegisRangeHorizontal", 6);
        aegisRangeVertical = get().getDouble("aegisRangeVertical", 10);
        shieldBashCooldown = get().getInt("shieldBashCooldown", 10);

        AttributeModifierManager.put(healthModifier, Attribute.GENERIC_MAX_HEALTH);
        AttributeModifierManager.put(stunEffectModifier, Attribute.GENERIC_MOVEMENT_SPEED);
    }

    public void setDefaults()
    {
        get().addDefault("healthMultiplier", 2.0);
        get().addDefault("dmgTakenMultiplier", 0.5);
        get().addDefault("mobAggroRangeOnHit", 5.0);
        get().addDefault("aegisCooldown", 20);
        get().addDefault("aegisDuration", 4);
        get().addDefault("aegisAmplifier", 3);
        get().addDefault("aegisRangeHorizontal", 6);
        get().addDefault("aegisRangeVertical", 10);
        get().addDefault("shieldBashCooldown", 10);

    }

}
