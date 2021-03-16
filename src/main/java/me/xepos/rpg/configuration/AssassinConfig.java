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
    public int shadowStepCooldown;
    public int shadowStepDuration;
    public int cutThroatCooldown;
    public double executeThreshold;
    public double backStrikeMultiplier;

    public static AssassinConfig getInstance() {
        if (instance == null)
            instance = new AssassinConfig();

        return instance;
    }

    public void loadValues() {
        double moveSpeedMultiplier = (float) get().getDouble("moveSpeedMultiplier", 1.25) - 1.0;
        moveSpeedModifier = new AttributeModifier(UUID.fromString("39bb1b42-b6e2-4b88-8195-759f373e653a"), "moveSpeed", moveSpeedMultiplier, AttributeModifier.Operation.MULTIPLY_SCALAR_1);

        smokeBombCooldown = get().getInt("smokeBombCooldown", 40);
        smokeBombDuration = get().getInt("smokeBombDuration", 10);
        shadowStepCooldown = get().getInt("shadowStepCooldown", 20);
        shadowStepDuration = get().getInt("shadowStepDuration", 5);
        cutThroatCooldown = get().getInt("cutThroatCooldown", 10);
        executeThreshold = get().getDouble("executeThreshold%", 25);
        backStrikeMultiplier = get().getDouble("backStrikeMultiplier", 1.3);

        AttributeModifierManager.put(moveSpeedModifier, Attribute.GENERIC_MOVEMENT_SPEED);

    }

    public void setDefaults() {
        get().addDefault("moveSpeedMultiplier", 1.25);
        get().addDefault("smokeBombCooldown", 40);
        get().addDefault("smokeBombDuration", 10);
        get().addDefault("shadowStepCooldown", 20);
        get().addDefault("shadowStepDuration", 5);
        get().addDefault("cutThroatCooldown", 10);
        get().addDefault("executeThreshold%", 25);
        get().addDefault("backStrikeMultiplier", 1.3);
    }

}
