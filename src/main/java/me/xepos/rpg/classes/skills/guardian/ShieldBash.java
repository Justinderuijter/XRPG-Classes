package me.xepos.rpg.classes.skills.guardian;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.IDelayedTrigger;
import me.xepos.rpg.classes.skills.IEffectDuration;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.tasks.ApplyStunTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ShieldBash extends XRPGSkill implements IDelayedTrigger, IEffectDuration {

    private int castDelay = 5;
    private byte duration = 2;

    public ShieldBash(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin) {
        super(xrpgPlayer, skillName, cooldown, plugin);

        xrpgPlayer.getEventHandler("DAMAGE_DEALT").addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

        if (e.getDamager() instanceof Player) {
            doShieldBash(e, (Player) e.getDamager());
        }

    }

    @Override
    public void initialize() {

    }

    private void doShieldBash(EntityDamageByEntityEvent e, Player player) {
        if (e.getEntity() instanceof Player && player.getInventory().getItemInOffHand().getType() == Material.SHIELD) {
            Player target = (Player) e.getEntity();
            //Check if the location is valid and player isn't allied before casting shield Bash
            if (getProtectionSet().isLocationValid(player.getLocation(), target.getLocation()) && !getPartyManager().isPlayerAllied(player, target)) {
                if (!isSkillReady()) {
                    player.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
                    return;
                }

                XRPGPlayer xrpgPlayer = Utils.GetRPG(target);
                if (xrpgPlayer.canBeStunned())
                    new ApplyStunTask(xrpgPlayer, guardianConfig.stunEffectModifier, duration * 20, getPlugin()).runTaskLater(getPlugin(), 5);
                else
                    player.sendMessage(ChatColor.RED + target.getName() + " cannot be stunned for " + xrpgPlayer.getStunblockDuration() + " seconds!");

                setRemainingCooldown(getCooldown());
            }
        }
    }

    @Override
    public int getTriggerDelay() {
        return castDelay;
    }

    @Override
    public void setTriggerDelay(int delay) {
        this.castDelay = delay;
    }

    @Override
    public int getEffectDuration() {
        return duration;
    }

    @Override
    public void setEffectDuration(int duration) {
        this.duration = (byte) duration;
    }
}
