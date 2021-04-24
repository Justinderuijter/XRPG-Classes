package me.xepos.rpg.classes.skills.bard;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.configuration.BardConfig;
import me.xepos.rpg.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;

import java.util.ArrayList;
import java.util.List;

public class PhoenixBlessing extends XRPGSkill {
    public PhoenixBlessing(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin) {
        super(xrpgPlayer, skillName, cooldown, plugin);

        xrpgPlayer.getSneakRightClickEventHandler().addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerInteractEvent)) return;
        PlayerInteractEvent e = (PlayerInteractEvent) event;
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = e.getPlayer();

            if (!isSkillReady()) {
                player.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
                return;
            }
            BardConfig bardConfig = BardConfig.getInstance();

            RayTraceResult result = player.getLocation().getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), bardConfig.maxCastRange, FluidCollisionMode.NEVER, true, 0.3, p -> p instanceof LivingEntity && p != player);
            if (result != null && result.getHitEntity() != null) {
                final int noDamageTickAmount = 100;
                LivingEntity entity = (LivingEntity) result.getHitEntity();

                if (entity instanceof Player) {
                    if (getProtectionSet().isLocationValid(player.getLocation(), null)) {
                        entity.setNoDamageTicks(noDamageTickAmount);
                    }
                } else {
                    entity.setNoDamageTicks(noDamageTickAmount);
                }

                player.sendMessage(ChatColor.DARK_GREEN + "You applied " + getSkillName() + " to " + entity.getName() + "!");

                List<Player> nearbyPlayers = new ArrayList(entity.getLocation().getWorld().getNearbyEntities(entity.getLocation(), 16, 16, 16, p -> p instanceof Player && p != player));
                for (Player nearbyPlayer : nearbyPlayers) {
                    nearbyPlayer.sendMessage(ChatColor.RED + player.getName() + " applied Phoenix's Blessing to " + entity.getName() + " for 5 seconds!");
                }

                setRemainingCooldown(bardConfig.phoenixsBlessingCooldown);
            }

        }

    }

    @Override
    public void initialize() {

    }
}
