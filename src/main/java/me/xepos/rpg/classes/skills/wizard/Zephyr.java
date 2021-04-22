package me.xepos.rpg.classes.skills.wizard;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.configuration.WizardConfig;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Zephyr extends XRPGSkill {
    final FireballStackData fireballStackData;

    public Zephyr(XRPGPlayer xrpgPlayer, String skillName, XRPG plugin, FireballStackData fireballStackData) {
        super(xrpgPlayer, skillName, plugin);

        this.fireballStackData = fireballStackData;
        xrpgPlayer.getRightClickEventHandler().addSkill(this);
    }

    @Override
    public void activate(Event event) {

    }

    @Override
    public void initialize() {

    }

    private void doZephyr(PlayerInteractEvent e) {
        if (!isSkillReady()) {
            e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getCooldown()));
            return;
        }
        WizardConfig wizardConfig = WizardConfig.getInstance();

        List<LivingEntity> entities = Utils.getLivingEntitiesInLine(e.getPlayer(), wizardConfig.maxCastRange);
        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PUFFER_FISH_BLOW_UP, 0.5F, 1F);

        int fireBallStacks = 0;
        if (fireballStackData != null) {
            fireBallStacks = fireballStackData.getFireBallStacks();
        }

        for (LivingEntity entity : entities) {
            if (entity != e.getPlayer()) {
                //Subtract 1 from the count to account for user
                if (entity instanceof Player) {
                    Player target = (Player) entity;
                    target.playSound(target.getLocation(), Sound.ENTITY_PUFFER_FISH_BLOW_UP, 0.5F, 1F);
                    if (getProtectionSet().isLocationValid(e.getPlayer().getLocation(), target.getLocation()))
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, wizardConfig.zephyrBaseDuration + (entities.size() - 1) * 10, fireBallStacks, false, false, false));
                } else
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, wizardConfig.zephyrBaseDuration + (entities.size() - 1) * 10, fireBallStacks, false, false, false));

            }
        }
        setCooldown(wizardConfig.zephyrCooldown - fireBallStacks);
    }
}
