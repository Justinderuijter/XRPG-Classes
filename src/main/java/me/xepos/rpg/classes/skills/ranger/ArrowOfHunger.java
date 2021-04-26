package me.xepos.rpg.classes.skills.ranger;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.ISkillPotionEffect;
import me.xepos.rpg.handlers.ShootBowEventHandler;
import me.xepos.rpg.utils.Utils;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArrowOfHunger extends XRPGBowSkill implements ISkillPotionEffect {

    private int amplifier = 3;
    private int potionDuration = 400;

    private final PotionEffect hungerEffect = new PotionEffect(PotionEffectType.HUNGER, potionDuration, amplifier, false, false, true);

    public ArrowOfHunger(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin) {
        super(xrpgPlayer, skillName, cooldown, plugin);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityShootBowEvent)) return;
        EntityShootBowEvent e = (EntityShootBowEvent) event;
        if (!(e.getProjectile() instanceof Arrow)) return;
        if (((ShootBowEventHandler) getXRPGPlayer().getEventHandler("SHOOT_BOW")).getCurrentSkill() != this) return;

        if (!isSkillReady()) {
            e.getEntity().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }
        Arrow arrow = (Arrow) e.getProjectile();

        arrow.addCustomEffect(hungerEffect, false);
        setRemainingCooldown(getCooldown());
    }

    @Override
    public void initialize() {

    }

    @Override
    public int getAmplifier() {
        return amplifier;
    }

    @Override
    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }

    @Override
    public int getPotionDuration() {
        return potionDuration;
    }

    @Override
    public void setPotionDuration(int potionDuration) {
        this.potionDuration = potionDuration;
    }

    @Override
    public PotionEffect getPotionEffect() {
        return hungerEffect;
    }
}
