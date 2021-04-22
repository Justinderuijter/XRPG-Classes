package me.xepos.rpg.classes.skills.sorcerer;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.configuration.SorcererConfig;
import me.xepos.rpg.tasks.ShowPlayerTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;

public class VoidParadox extends XRPGSkill {
    public VoidParadox(XRPGPlayer xrpgPlayer, String skillName, XRPG plugin) {
        super(xrpgPlayer, skillName, plugin);

        xrpgPlayer.getLeftClickEventHandler().addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerInteractEvent)) return;
        PlayerInteractEvent e = (PlayerInteractEvent) event;
        if (e.getItem() == null || e.getItem().getType() != Material.ENCHANTED_BOOK) return;

        if (Utils.isItemNameMatching(e.getItem(), "Book of Darkness")) {
            doVoidParadox(e.getPlayer());
        }
    }

    @Override
    public void initialize() {

    }

    private void doVoidParadox(Player caster) {
        if (!isSkillReady()) {
            caster.sendMessage(Utils.getCooldownMessage(getSkillName(), getCooldown()));
            return;
        }


        RayTraceResult result = Utils.rayTrace(caster, 16, FluidCollisionMode.NEVER);
        if (result.getHitEntity() != null) {
            SorcererConfig sorcererConfig = SorcererConfig.getInstance();
            LivingEntity target = (LivingEntity) result.getHitEntity();
            if (target instanceof Player) {
                Player targetPlayer = (Player) target;
                if (!getPartyManager().isPlayerAllied(caster, targetPlayer) && getProtectionSet().isLocationValid(caster.getLocation(), targetPlayer.getLocation())) {
                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        targetPlayer.hidePlayer(getPlugin(), player);
                    }
                    new ShowPlayerTask(getPlugin(), targetPlayer).runTaskLater(getPlugin(), (long) sorcererConfig.voidParadoxDuration * 20);
                }
            } else {
                target.damage(15, caster);
            }

            setCooldown(sorcererConfig.voidParadoxCooldown);
        }
    }
}
