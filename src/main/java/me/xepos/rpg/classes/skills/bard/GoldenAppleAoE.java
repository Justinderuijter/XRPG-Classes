package me.xepos.rpg.classes.skills.bard;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.configuration.BardConfig;
import me.xepos.rpg.utils.Utils;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class GoldenAppleAoE extends XRPGSkill {
    private final EnchantedGoldenAppleAoE GAppleAoE;

    public GoldenAppleAoE(XRPG plugin, String skillName, @Nullable EnchantedGoldenAppleAoE GAppleAoE) {
        super(plugin, skillName);

        this.GAppleAoE = GAppleAoE;
    }

    @Override
    public void activate(Event event) {
        if (event instanceof PlayerItemConsumeEvent) {
            PlayerItemConsumeEvent e = (PlayerItemConsumeEvent) event;
            if (GAppleAoE != null) {
                if (!isSkillReady() || !GAppleAoE.isSkillReady()) {
                    e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), Math.max(GAppleAoE.getCooldown(), getCooldown())));
                    e.setCancelled(true);
                    return;
                }
            } else {
                if (!isSkillReady()) {
                    e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getCooldown()));
                    e.setCancelled(true);
                    return;
                }
            }
            List<PotionEffect> potionEffects = new ArrayList<>();

            potionEffects.add(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
            potionEffects.add(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));

            Utils.addPotionEffects(getNearbyAlliedPlayers(e.getPlayer(), 10, 5, 10), potionEffects);

            setCooldown(BardConfig.getInstance().goldenAppleCooldown);
        }
    }

    @Override
    public void initialize() {

    }

    @Override
    public boolean isSkillReady() {
        if (GAppleAoE != null) {
            return getCooldown() > System.currentTimeMillis() && GAppleAoE.getCooldown() > System.currentTimeMillis();
        }
        return super.getCooldown() > System.currentTimeMillis();
    }
}
