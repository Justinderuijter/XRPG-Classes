package me.xepos.rpg.configuration;

import me.xepos.rpg.AttributeModifierManager;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;

import java.util.UUID;

public final class AssassinConfig extends XRPGConfigFile {
    private static AssassinConfig instance;

    //Class values
    public AttributeModifier moveSpeedModifier;
    public int smokeBombCooldown;
    public int smokeBombDuration;
    public int cutThroatCooldown;
    public double executeThreshold;
    public int poisonDuration;
    public int poisonAmplifier;
    public double backStrikeMultiplier;

    public static AssassinConfig getInstance()
    {
        if (instance == null)
            instance = new AssassinConfig();

        return instance;
    }

    public void loadValues()
    {
        double moveSpeedMultiplier = (float)get().getDouble("moveSpeedMultiplier", 1.25) - 1.0;
        moveSpeedModifier = new AttributeModifier(UUID.fromString("39bb1b42-b6e2-4b88-8195-759f373e653a"), "moveSpeed", moveSpeedMultiplier, AttributeModifier.Operation.MULTIPLY_SCALAR_1);

        smokeBombCooldown = get().getInt("smokeBombCooldown", 40);
        smokeBombDuration = get().getInt("smokeBombDuration", 10);
        cutThroatCooldown = get().getInt("cutThroatCooldown", 10);
        executeThreshold = get().getDouble("executeThreshold%", 25);
        poisonDuration = get().getInt("poisonDuration");
        poisonAmplifier = get().getInt("poisonAmplifier");
        backStrikeMultiplier = get().getDouble("backStrikeMultiplier");

        AttributeModifierManager.put(moveSpeedModifier, Attribute.GENERIC_MOVEMENT_SPEED);

    }

    public void setDefaults()
    {
        get().addDefault("moveSpeedMultiplier", 1.25);
        get().addDefault("smokeBombCooldown", 40);
        get().addDefault("smokeBombDuration", 10);
        get().addDefault("cutThroatCooldown", 10);
        get().addDefault("executeThreshold%", 25);
        get().addDefault("poisonDuration", 2);
        get().addDefault("poisonAmplifier", 1);
        get().addDefault("backStrikeMultiplier", 1.3);
    }

}
