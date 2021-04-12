package me.xepos.rpg.configuration;

import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.enums.ModifierType;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public final class RangerConfig extends XRPGConfigFile {
    private static RangerConfig instance;

    //Class values
    public AttributeModifier moveSpeedModifier;
    public int snipeShotCooldown;
    public double snipeShotDamageMultiplier;
    public byte snipeShotPierceBonus;
    public int explosiveShotCooldown;
    public float explosiveShotYield;
    public int lightningArrowCooldown;
    public int enderArrowCooldown;
    public int arrowOfHungerCooldown;
    public PotionEffect hungerEffect;
    public int arrowOfDarknessCooldown;
    public int soulShotCooldown;
    public double soulShotDamageMultiplier;

    public static RangerConfig getInstance()
    {
        if (instance == null)
            instance = new RangerConfig();

        return instance;
    }


    public void loadValues()
    {
        int duration = get().getInt("hungerShotDuration", 400);
        int amplifier = get().getInt("hungerShotAmplifier", 3);

        double moveSpeedMultiplier = get().getDouble("moveSpeedMultiplier", 1.25) - 1.0;
        moveSpeedModifier = new AttributeModifier(UUID.fromString("bd7bc1cf-b6e2-490d-b8ad-09cc8a328a91"), "moveSpeed", moveSpeedMultiplier, AttributeModifier.Operation.MULTIPLY_SCALAR_1);


        hungerEffect = new PotionEffect(PotionEffectType.HUNGER, duration, amplifier, false, false, true);
        snipeShotCooldown = get().getInt("snipeShotCooldown", 6);
        snipeShotDamageMultiplier = get().getDouble("snipeShotDamageMultiplier", 1.5);
        snipeShotPierceBonus = (byte) get().getInt("snipeShotPierceBonus", 1);
        explosiveShotCooldown = get().getInt("explosiveShotCooldown", 12);
        explosiveShotYield = (float) get().getDouble("explosiveShotYield", 2.0);
        lightningArrowCooldown = get().getInt("lightningArrowCooldown", 8);
        enderArrowCooldown = get().getInt("enderArrowCooldown", 18);
        arrowOfHungerCooldown = get().getInt("arrowOfHungerCooldown", 5);
        arrowOfDarknessCooldown = get().getInt("arrowOfDarknessCooldown", 11);
        soulShotCooldown = get().getInt("soulShotCooldown", 15);
        soulShotDamageMultiplier = 1 - get().getDouble("soulShotPercentHealthDamage") / 100;

        AttributeModifierManager.getInstance().put(ModifierType.POSITIVE, moveSpeedModifier, Attribute.GENERIC_MOVEMENT_SPEED);
    }

    public void setDefaults()
    {
        get().addDefault("moveSpeedMultiplier", 1.25);
        get().addDefault("snipeShotCooldown", 6);
        get().addDefault("snipeShotDamageMultiplier", 1.5);
        get().addDefault("snipeShotPierceBonus", 1);
        get().addDefault("explosiveShotCooldown", 12);
        get().addDefault("explosiveShotYield", 2.0);
        get().addDefault("lightningArrowCooldown", 8);
        get().addDefault("enderArrowCooldown", 18);
        get().addDefault("arrowOfHungerCooldown", 5);
        get().addDefault("hungerShotDuration", 400);
        get().addDefault("hungerShotAmplifier", 3);
        get().addDefault("arrowOfDarknessCooldown", 11);
        get().addDefault("soulShotCooldown", 15);
        get().addDefault("soulShotPercentHealthDamage", 25);
    }
}
