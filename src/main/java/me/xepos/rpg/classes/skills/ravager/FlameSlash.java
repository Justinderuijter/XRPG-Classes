package me.xepos.rpg.classes.skills.ravager;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.configuration.RavagerConfig;
import me.xepos.rpg.datatypes.fireballData;
import me.xepos.rpg.utils.Utils;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class FlameSlash extends XRPGSkill {
    public FlameSlash(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin) {
        super(xrpgPlayer, skillName, cooldown, plugin);

        xrpgPlayer.getLeftClickEventHandler().addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerInteractEvent)) return;
        PlayerInteractEvent e = (PlayerInteractEvent) event;

        doFlameSlash(e);
    }

    @Override
    public void initialize() {

    }

    private void doFlameSlash(PlayerInteractEvent e) {

        if (!isSkillReady()) {
            e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }

        Fireball fireball = e.getPlayer().launchProjectile(SmallFireball.class);
        fireball.setCustomName(getSkillName());
        fireball.setCustomNameVisible(false);
        if (!getPlugin().fireBalls.containsKey(fireball.getEntityId()))
            getPlugin().fireBalls.put(fireball.getEntityId(), new fireballData(6.0, 10));

        setRemainingCooldown(RavagerConfig.getInstance().flameSlashCooldown);

    }
}
