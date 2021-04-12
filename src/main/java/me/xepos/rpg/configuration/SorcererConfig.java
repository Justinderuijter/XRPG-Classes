package me.xepos.rpg.configuration;


import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class SorcererConfig extends XRPGConfigFile {
    private static SorcererConfig instance;

    public List<PotionEffectType> negativeEffects;
    public int overheatCooldown;
    public int overheatDuration;
    public int souldrawCooldown;
    public int souldrawDamage;
    public int voidParadoxCooldown;
    public int voidParadoxDuration;
    public int trailOfFlamesCooldown;
    public int trailOfFlamesDamage;
    public boolean trailOfFlamesIgnoreVillagers;
    public int bloodCorruptionCooldown;
    public int bloodCorruptionDuration;
    public int bloodPurificationCooldown;

    public static SorcererConfig getInstance() {
        if (instance == null)
            instance = new SorcererConfig();

        return instance;
    }

    @Override
    void loadValues() {
        negativeEffects = new ArrayList<>();
        List<String> effects = get().getStringList("Cleansable Effects");
        for (String effect : effects) {
            PotionEffectType tempEffect = PotionEffectType.getByName(effect);
            if (tempEffect != null) {
                negativeEffects.add(tempEffect);
            }
        }

        overheatCooldown = get().getInt("overheatCooldown", 20);
        overheatDuration = get().getInt("overheatDuration", 5);
        souldrawCooldown = get().getInt("souldrawCooldown", 1);
        souldrawDamage = get().getInt("souldrawDamage", 4);
        voidParadoxCooldown = get().getInt("voidParadoxCooldown", 30);
        trailOfFlamesCooldown = get().getInt("trailOfFlamesCooldown", 12);
        trailOfFlamesDamage = get().getInt("trailOfFlamesDamage", 4);
        trailOfFlamesIgnoreVillagers = get().getBoolean("trailOfFlamesIgnoreVillagers", true);
        bloodCorruptionCooldown = get().getInt("bloodCorruptionCooldown", 20);
        bloodCorruptionDuration = get().getInt("bloodCorruptionDuration", 4);
        bloodPurificationCooldown = get().getInt("bloodPurificationCooldown", 15);
        voidParadoxDuration = get().getInt("voidParadoxDuration", 5);
    }

    @Override
    void setDefaults() {
        List<String> effectTypes = new ArrayList<String>() {
            {
                add(PotionEffectType.SLOW.getName());
                add(PotionEffectType.BLINDNESS.getName());
                add(PotionEffectType.CONFUSION.getName());
                add(PotionEffectType.GLOWING.getName());
                add(PotionEffectType.HARM.getName());
                add(PotionEffectType.HUNGER.getName());
                add(PotionEffectType.LEVITATION.getName());
                add(PotionEffectType.POISON.getName());
                add(PotionEffectType.SLOW_DIGGING.getName());
                add(PotionEffectType.WEAKNESS.getName());
                add(PotionEffectType.WITHER.getName());
            }
        };

        get().addDefault("cleansable Effects", effectTypes);
        get().addDefault("souldrawCooldown", 1);
        get().addDefault("souldrawDamage", 4);
        get().addDefault("voidParadoxCooldown", 30);
        get().addDefault("trailOfFlamesCooldown", 12);
        get().addDefault("trailOfFlamesDamage", 4);
        get().addDefault("trailOfFlamesIgnoreVillagers", true);
        get().addDefault("bloodCorruptionCooldown", 20);
        get().addDefault("bloodCorruptionDuration", 4);
        get().addDefault("bloodPurificationCooldown", 15);
        get().addDefault("overheatCooldown", 20);
        get().addDefault("overheatDuration", 5);
    }
}
