package me.xepos.rpg.classes.skills.sorcerer;

import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.configuration.SorcererConfig;
import me.xepos.rpg.enums.ModifierType;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class BloodPurification extends XRPGSkill {
    public BloodPurification(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin) {
        super(xrpgPlayer, skillName, cooldown, plugin);

        xrpgPlayer.getRightClickEventHandler().addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerInteractEvent)) return;
        PlayerInteractEvent e = (PlayerInteractEvent) event;
        if (e.getItem() == null || e.getItem().getType() != Material.ENCHANTED_BOOK) return;

        if (Utils.isItemNameMatching(e.getItem(), "Book of Blood")) {
            doBloodPurification(e.getPlayer());
        }
    }

    @Override
    public void initialize() {

    }

    private void doBloodPurification(Player caster) {
        if (!isSkillReady()) {
            caster.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }

        List<LivingEntity> livingEntities = new ArrayList(caster.getWorld().getNearbyEntities(caster.getLocation(), 10, 10, 10, p -> p instanceof LivingEntity));

        for (LivingEntity livingEntity : livingEntities) {
            if (livingEntity instanceof Player) {
                Player target = (Player) livingEntity;
                if (getProtectionSet().isPvPTypeSame(caster.getLocation(), target.getLocation()) && getPartyManager().isPlayerAllied(caster, target)) {
                    for (AttributeModifier mod : AttributeModifierManager.getInstance().getModifiers(ModifierType.NEGATIVE).keySet()) {
                        mod.getUniqueId();
                    }
                    cleanseBadPotionEffects(livingEntity);
                }
            } else {
                cleanseBadPotionEffects(livingEntity);
            }
        }

        setRemainingCooldown(getCooldown());
    }

    private void cleanseBadPotionEffects(LivingEntity livingTarget) {

        for (PotionEffectType potionEffectType : SorcererConfig.getInstance().negativeEffects) {
            if (livingTarget.hasPotionEffect(potionEffectType)) {
                livingTarget.removePotionEffect(potionEffectType);
            }
        }
    }
}
