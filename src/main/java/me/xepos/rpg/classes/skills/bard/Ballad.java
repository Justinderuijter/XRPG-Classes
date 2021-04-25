package me.xepos.rpg.classes.skills.bard;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.IRepeatingTrigger;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.tasks.HealOverTimeTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class Ballad extends XRPGSkill implements IRepeatingTrigger {

    private byte maxProcs = 10;
    private int procInterval = 1;

    public Ballad(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin) {
        super(xrpgPlayer, skillName, cooldown, plugin);

        xrpgPlayer.getRightClickEventHandler().addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerInteractEvent)) return;
        PlayerInteractEvent e = (PlayerInteractEvent) event;
        Player caster = e.getPlayer();

        if (!isSkillReady()) {
            caster.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }

        List<Player> nearbyPlayers = getNearbyAlliedPlayers(caster, 10, 5, 10);
        for (Player nearbyPlayer : nearbyPlayers) {
            new HealOverTimeTask(nearbyPlayer, getDamage(), getMaxProcs()).runTaskTimer(getPlugin(), 1L, procInterval * 20L);
        }

        setRemainingCooldown(getCooldown());
    }

    @Override
    public void initialize() {

    }

    @Override
    public int getInterval() {
        return procInterval;
    }

    @Override
    public void setInterval(int delay) {
        this.procInterval = delay;
    }

    @Override
    public byte getMaxProcs() {
        return maxProcs;
    }

    @Override
    public void setMaxProcs(byte maxProcs) {
        this.maxProcs = maxProcs;
    }
}
