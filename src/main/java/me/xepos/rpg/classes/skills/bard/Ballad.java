package me.xepos.rpg.classes.skills.bard;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.configuration.BardConfig;
import me.xepos.rpg.tasks.HealOverTimeTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class Ballad extends XRPGSkill {
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
        BardConfig bardConfig = BardConfig.getInstance();

        List<Player> nearbyPlayers = getNearbyAlliedPlayers(caster, 10, 5, 10);
        for (Player nearbyPlayer : nearbyPlayers) {
            new HealOverTimeTask(nearbyPlayer, bardConfig.balledHealPerProc, bardConfig.balledMaxProcs).runTaskTimer(getPlugin(), 1L, bardConfig.balledProcDelay * 20L);
        }

        setRemainingCooldown(bardConfig.balladCooldown);
    }

    @Override
    public void initialize() {

    }
}
