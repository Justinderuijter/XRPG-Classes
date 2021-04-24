package me.xepos.rpg.classes.skills.bard;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
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
    public EnchantedGoldenAppleAoE(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin) {
        super(xrpgPlayer, skillName, cooldown, plugin);

        xrpgPlayer.getConsumeItemEventHandler().addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemConsumeEvent)) return;
        PlayerItemConsumeEvent e = (PlayerItemConsumeEvent) event;

        if (!Utils.isSkillReady(getRemainingCooldown())) {
            e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            e.setCancelled(true);
            return;
        }

        List<PotionEffect> potionEffects = new ArrayList<>();

        potionEffects.add(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 3));
        potionEffects.add(new PotionEffect(PotionEffectType.REGENERATION, 600, 1));
        potionEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 6000, 0));
        potionEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 6000, 0));

        Utils.addPotionEffects(getNearbyAlliedPlayers(e.getPlayer(), 10, 5, 10), potionEffects);

        setRemainingCooldown(BardConfig.getInstance().eGoldenAppleCooldown);

    }

    @Override
    public void initialize() {

    }
}
