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

public class GoldenAppleAoE extends XRPGSkill {
    private EnchantedGoldenAppleAoE GAppleAoE;

    public GoldenAppleAoE(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin, EnchantedGoldenAppleAoE GAppleAoE) {
        super(xrpgPlayer, skillName, cooldown, plugin);

        this.GAppleAoE = GAppleAoE;
        xrpgPlayer.getConsumeItemEventHandler().addSkill(this);
    }

    public GoldenAppleAoE(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin) {
        super(xrpgPlayer, skillName, cooldown, plugin);

        xrpgPlayer.getConsumeItemEventHandler().addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemConsumeEvent)) return;
        PlayerItemConsumeEvent e = (PlayerItemConsumeEvent) event;
        if (GAppleAoE != null) {
            if (!isSkillReady() || !GAppleAoE.isSkillReady()) {
                e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), Math.max(GAppleAoE.getRemainingCooldown(), getRemainingCooldown())));
                e.setCancelled(true);
                return;
            }
        } else {
            if (!isSkillReady()) {
                e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
                e.setCancelled(true);
                return;
            }
        }
        List<PotionEffect> potionEffects = new ArrayList<>();

        potionEffects.add(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
        potionEffects.add(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));

        Utils.addPotionEffects(getNearbyAlliedPlayers(e.getPlayer(), 10, 5, 10), potionEffects);

        setRemainingCooldown(BardConfig.getInstance().goldenAppleCooldown);

    }

    @Override
    public void initialize() {
        for (XRPGSkill skill : getXRPGPlayer().getConsumeItemEventHandler().getSkills()) {
            if (skill instanceof EnchantedGoldenAppleAoE) {
                this.GAppleAoE = (EnchantedGoldenAppleAoE) skill;
                return;
            }
        }
    }

    @Override
    public boolean isSkillReady() {
        if (GAppleAoE != null) {
            return getRemainingCooldown() > System.currentTimeMillis() && GAppleAoE.getRemainingCooldown() > System.currentTimeMillis();
        }
        return super.getRemainingCooldown() > System.currentTimeMillis();
    }
}
