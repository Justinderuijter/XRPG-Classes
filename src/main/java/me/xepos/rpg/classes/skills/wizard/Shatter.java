package me.xepos.rpg.classes.skills.wizard;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.configuration.WizardConfig;
import me.xepos.rpg.enums.DamageTakenSource;
import me.xepos.rpg.events.XRPGDamageTakenAddedEvent;
import me.xepos.rpg.tasks.RemoveDTModifierTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Shatter extends XRPGSkill {
    final FireballStackData fireballStackData;

    public Shatter(XRPGPlayer xrpgPlayer, String skillName, XRPG plugin, @Nullable FireballStackData fireballStackData) {
        super(xrpgPlayer, skillName, plugin);

        this.fireballStackData = fireballStackData;
        xrpgPlayer.getLeftClickEventHandler().addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerInteractEvent)) return;
        PlayerInteractEvent e = (PlayerInteractEvent) event;
        if (e.getItem() == null || e.getItem().getType() != Material.STICK) return;

        doShatter(e);
    }

    @Override
    public void initialize() {

    }

    private void doShatter(PlayerInteractEvent e) {
        if (!isSkillReady()) {
            e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getCooldown()));
            return;
        }
        WizardConfig wizardConfig = WizardConfig.getInstance();
        Location loc = Utils.getTargetBlock(e.getPlayer(), wizardConfig.maxCastRange).getLocation();
        List<LivingEntity> livingEntities = new ArrayList(loc.getWorld().getNearbyEntities(loc, 3, 3, 3, p -> p instanceof LivingEntity && p != e.getPlayer()));

        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_GLASS_BREAK, 1F, 1F);
        for (LivingEntity livingEntity : livingEntities) {
            shatterLogic(e, livingEntity);
        }
        int fireBallStacks = 0;
        if (fireballStackData != null) {
            fireBallStacks = fireballStackData.getFireBallStacks();
        }

        setCooldown(wizardConfig.shatterCooldown - fireBallStacks);
    }

    public void shatterLogic(PlayerInteractEvent e, LivingEntity livingEntity) {
        WizardConfig wizardConfig = WizardConfig.getInstance();

        DamageTakenSource damageTakenSource = DamageTakenSource.SHATTER;
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.SLOW, wizardConfig.shatterDuration * 20, 1, false, false, false);

        if (livingEntity instanceof Player) {
            Player targetPlayer = (Player) livingEntity;
            //Check if the target is valid
            if (getProtectionSet().isLocationValid(e.getPlayer().getLocation(), targetPlayer.getLocation())) {
                //Add potion effect and fire event
                targetPlayer.addPotionEffect(potionEffect);
                XRPGDamageTakenAddedEvent event = new XRPGDamageTakenAddedEvent(e.getPlayer(), targetPlayer, damageTakenSource, wizardConfig.shatterDTAmount);
                Bukkit.getServer().getPluginManager().callEvent(event);

                //Apply DTModifier if the event isn't cancelled
                if (!event.isCancelled()) {
                    Utils.addDTModifier(targetPlayer, damageTakenSource, wizardConfig.shatterDTAmount);
                    new RemoveDTModifierTask(e.getPlayer(), (Player) livingEntity, damageTakenSource).runTaskLater(getPlugin(), wizardConfig.shatterDuration * 20L);
                }
            }
        } else {
            livingEntity.addPotionEffect(potionEffect);
        }
    }
}
