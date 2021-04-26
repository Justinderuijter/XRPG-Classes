package me.xepos.rpg.classes.skills.bard;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.tasks.HealOverTimeTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class Ballad extends XRPGSkill {

    private byte maxProcs = 10;
    private int procInterval = 1;

    public Ballad(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getEventHandler("RIGHT_CLICK").addSkill(this);
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

}
