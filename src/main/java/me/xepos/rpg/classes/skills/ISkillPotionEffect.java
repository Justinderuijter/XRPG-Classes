package me.xepos.rpg.classes.skills;

import org.bukkit.potion.PotionEffect;

public interface ISkillPotionEffect {

    int getAmplifier();

    void setAmplifier(int amplifier);

    int getPotionDuration();

    void setPotionDuration(int potionDuration);

    PotionEffect getPotionEffect();
}
