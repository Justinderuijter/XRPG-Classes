package me.xepos.rpg.classes.skills.bard;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.configuration.BardConfig;
import me.xepos.rpg.utils.Utils;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class EnchantedGoldenAppleAoE extends XRPGSkill {
    public EnchantedGoldenAppleAoE(XRPG plugin, String skillName) {
        super(plugin, skillName);
    }

    @Override
    public void activate(Event event) {
        if (event instanceof PlayerItemConsumeEvent) {
            PlayerItemConsumeEvent e = (PlayerItemConsumeEvent) event;

            if (!Utils.isSkillReady(getCooldown())) {
                e.getPlayer().sendMessage(Utils.getCooldownMessage("Enchanted Golden Apple AoE", getCooldown()));
                e.setCancelled(true);
                return;
            }

            List<PotionEffect> potionEffects = new ArrayList<>();

            potionEffects.add(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 3));
            potionEffects.add(new PotionEffect(PotionEffectType.REGENERATION, 600, 1));
            potionEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 6000, 0));
            potionEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 6000, 0));

            Utils.addPotionEffects(getNearbyAlliedPlayers(e.getPlayer(), 10, 5, 10), potionEffects);

            setCooldown(BardConfig.getInstance().eGoldenAppleCooldown);
        }
    }

    @Override
    public void initialize() {

    }
}
