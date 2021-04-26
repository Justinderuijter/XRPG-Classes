package me.xepos.rpg.classes.skills.guardian;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.IAura;
import me.xepos.rpg.classes.skills.IEffectDuration;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.enums.DamageTakenSource;
import me.xepos.rpg.events.XRPGDamageTakenAddedEvent;
import me.xepos.rpg.tasks.RemoveDTModifierTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class Aegis extends XRPGSkill implements IEffectDuration, IAura {

    private byte duration = 4;
    private double xRange = 8;
    private double yRange = 5;
    private double zRange = 8;

    public Aegis(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin) {
        super(xrpgPlayer, skillName, cooldown, plugin);

        xrpgPlayer.getEventHandler("RIGHT_CLICK").addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerInteractEvent)) return;

        doAegis((PlayerInteractEvent) event);
    }

    @Override
    public void initialize() {

    }

    @SuppressWarnings("unchecked")
    private void doAegis(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (player.getInventory().getItemInOffHand().getType() == Material.SHIELD) {
            if (!isSkillReady()) {
                player.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
                return;
            }

            List<Player> nearbyPlayers = new ArrayList(player.getWorld().getNearbyEntities(player.getLocation(), xRange, yRange, zRange, p -> p instanceof Player && getPartyManager().isPlayerAllied(player, (Player) p)));
            for (Player target : nearbyPlayers) {
                //Applying the DTModifier if event is cancelled
                XRPGDamageTakenAddedEvent event = new XRPGDamageTakenAddedEvent(player, target, DamageTakenSource.AEGIS, getDamageMultiplier());
                Bukkit.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    Utils.addDTModifier(target, DamageTakenSource.AEGIS, getDamageMultiplier());
                    target.sendMessage(player.getDisplayName() + " Granted you " + getSkillName() + "!");

                    new RemoveDTModifierTask(player, target, DamageTakenSource.AEGIS).runTaskLater(getPlugin(), duration);
                }
            }
            if (nearbyPlayers.size() > 0) {
                player.sendMessage(ChatColor.GREEN + "Applied " + getSkillName() + " to " + nearbyPlayers.size() + " player(s)!");
                setRemainingCooldown(getCooldown());
            }
        }

    }

    @Override
    public int getEffectDuration() {
        return duration;
    }

    @Override
    public void setEffectDuration(int duration) {
        this.duration = (byte) duration;
    }

    @Override
    public double getXRange() {
        return xRange;
    }

    @Override
    public void setXRange(double x) {
        this.xRange = x;
    }

    @Override
    public double getYRange() {
        return yRange;
    }

    @Override
    public void setYRange(double y) {
        this.yRange = y;
    }

    @Override
    public double getZRange() {
        return zRange;
    }

    @Override
    public void setZRange(double z) {
        this.zRange = z;
    }


}
