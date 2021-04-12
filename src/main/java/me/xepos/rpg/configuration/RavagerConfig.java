package me.xepos.rpg.configuration;

import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.enums.ModifierType;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public final class RavagerConfig extends XRPGConfigFile {
    private static RavagerConfig instance;

    //Class values
    public AttributeModifier attackSpeedModifier;
    public byte rageOnHit;
    public byte bonusRageOnKill;
    public byte rageReductionPer5s;
    public double rageTierOneMultiplier;
    public double rageTierTwoMultiplier;
    public double rageTierThreeMultiplier;
    public double rageTierFourMultiplier;
    public double rageAoERange;
    public double axeDamageMultiplier;
    public double otherDamageMultiplier;
    public boolean ignoreVillagers;
    public PotionEffect slowEffect;
    public int flameSlashCooldown;
    public int SoaringSlashCooldown;

    public static RavagerConfig getInstance()
    {
        if (instance == null)
            instance = new RavagerConfig();

        return instance;
    }

    public void loadValues()
    {
        int slowEffectDuration = get().getInt("slowEffectDuration", 100);
        int slowEffectAmplifier = get().getInt("slowEffectAmplifier", 4);
        double attackSpeedMultiplier = get().getDouble("rageAttackSpeedMultiplier", 1.65) - 1;


        attackSpeedModifier = new AttributeModifier(UUID.fromString("1d7a09c9-b6e2-4dc7-ab6f-8831dffcb111"), "Ravager", attackSpeedMultiplier, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
        slowEffect = new PotionEffect(PotionEffectType.SLOW, slowEffectDuration, slowEffectAmplifier, false, false, true);
        rageOnHit = (byte)get().getInt("rageOnHit", 5);
        bonusRageOnKill = (byte)get().getInt("bonusRageOnKill", 5);
        rageReductionPer5s = (byte)get().getInt("rageReductionPer5s", 5);
        rageTierOneMultiplier = get().getDouble("rageTierOneMultiplier", 1.1);
        rageTierTwoMultiplier = get().getDouble("rageTierTwoMultiplier", 1.2);
        rageTierThreeMultiplier = get().getDouble("rageTierThreeMultiplier", 1.3);
        rageTierFourMultiplier = get().getDouble("rageTierFourMultiplier", 1.3);
        rageAoERange = get().getDouble("rageAoERange", 4.0);
        axeDamageMultiplier = get().getDouble("axeDamageMultiplier", 1.1);
        otherDamageMultiplier = get().getDouble("otherDamageMultiplier", 0.7);
        ignoreVillagers = get().getBoolean("ignoreVillagers", true);
        flameSlashCooldown = get().getInt("flameSlashCooldown", 12);
        SoaringSlashCooldown = get().getInt("SoaringSlashCooldown", 15);

        AttributeModifierManager.getInstance().put(ModifierType.POSITIVE, attackSpeedModifier, Attribute.GENERIC_ATTACK_SPEED);
    }

    public void setDefaults()
    {
        get().addDefault("rageAttackSpeedMultiplier", 1.65);
        get().addDefault("rageOnHit", 5);
        get().addDefault("bonusRageOnKill", 5);
        get().addDefault("rageReductionPer5s", 5);
        get().addDefault("rageTierOneMultiplier", 1.1);
        get().addDefault("rageTierTwoMultiplier", 1.2);
        get().addDefault("rageTierThreeMultiplier", 1.3);
        get().addDefault("rageTierFourMultiplier", 1.3);
        get().addDefault("rageAoERange", 4.0);
        get().addDefault("axeDamageMultiplier", 1.1);
        get().addDefault("axeDamageMultiplier", 0.7);
        get().addDefault("ignoreVillagers", true);
        get().addDefault("slowEffectDuration", 100);
        get().addDefault("slowEffectAmplifier", 4);
        get().addDefault("flameSlashCooldown", 12);
        get().addDefault("SoaringSlashCooldown", 15);
    }

}
