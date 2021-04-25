package me.xepos.rpg.classes.skills.sorcerer;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.IEffectDuration;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.configuration.SorcererConfig;
import me.xepos.rpg.tasks.BloodCorruptionTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;

public class BloodCorruption extends XRPGSkill implements IEffectDuration {

    private int duration = 4;

    public BloodCorruption(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin) {
        super(xrpgPlayer, skillName, cooldown, plugin);

        xrpgPlayer.getLeftClickEventHandler().addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerInteractEvent)) return;
        PlayerInteractEvent e = (PlayerInteractEvent) event;
        if (e.getItem() == null || e.getItem().getType() != Material.ENCHANTED_BOOK) return;

        if (Utils.isItemNameMatching(e.getItem(), "Book of Blood")) {
            doBloodCorruption(e.getPlayer());
        }
    }

    @Override
    public void initialize() {

    }

    private void doBloodCorruption(Player caster) {
        if (!isSkillReady()) {
            caster.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }

        RayTraceResult result = Utils.rayTrace(caster, 16, FluidCollisionMode.NEVER);
        if (result.getHitEntity() != null) {
            SorcererConfig sorcererConfig = SorcererConfig.getInstance();

            caster.sendMessage("Hit " + result.getHitEntity().getName());
            LivingEntity target = (LivingEntity) result.getHitEntity();
            new BloodCorruptionTask(caster, target).runTaskLater(getPlugin(), duration * 20L);
            setRemainingCooldown(getCooldown());
        }
    }

    @Override
    public int getEffectDuration() {
        return duration;
    }

    @Override
    public void setEffectDuration(int duration) {
        this.duration = duration;
    }
}
