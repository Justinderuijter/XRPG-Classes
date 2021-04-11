package me.xepos.rpg.configuration;


import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class SorcererConfig extends XRPGConfigFile {
    private static SorcererConfig instance;

    public List<PotionEffectType> negativeEffects;
    public int overheatCooldown;

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

        get().getInt("overheatCooldown", 20);
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

        get().addDefault("Cleansable Effects", effectTypes);
        get().addDefault("overheatCooldown", 20);
    }
}
