package me.xepos.rpg.classes.skills.bard;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.configuration.BardConfig;
import me.xepos.rpg.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class PotionAoE extends XRPGSkill {

    public PotionAoE(XRPG plugin, String skillName) {
        super(plugin, skillName);

    }

    @Override
    public void activate(Event event) {
        if (event instanceof PlayerItemConsumeEvent) {
            PlayerItemConsumeEvent e = (PlayerItemConsumeEvent) event;
            Player player = e.getPlayer();
            if (!isSkillReady()) {
                player.sendMessage(Utils.getCooldownMessage("Potion AoE Heal", getCooldown()));
                return;
            }

            List<PotionEffect> potionEffects = new ArrayList<>();

            potionEffects.add(new PotionEffect(PotionEffectType.HEAL, 1, 1));

            Utils.addPotionEffects(getNearbyAlliedPlayers(player, 10, 5, 10), potionEffects);

            setCooldown(BardConfig.getInstance().potionCooldown);
        }
    }

    @Override
    public void initialize() {

    }
}
