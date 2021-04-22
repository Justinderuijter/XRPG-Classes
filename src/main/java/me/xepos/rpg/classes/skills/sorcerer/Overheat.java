package me.xepos.rpg.classes.skills.sorcerer;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.configuration.SorcererConfig;
import me.xepos.rpg.tasks.OverheatTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;

public class Overheat extends XRPGSkill {
    public Overheat(XRPGPlayer xrpgPlayer, String skillName, XRPG plugin) {
        super(xrpgPlayer, skillName, plugin);

        xrpgPlayer.getLeftClickEventHandler().addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerInteractEvent)) return;
        PlayerInteractEvent e = (PlayerInteractEvent) event;
        if (e.getItem() == null || e.getItem().getType() != Material.ENCHANTED_BOOK) return;

        if (Utils.isItemNameMatching(e.getItem(), "Book of Flame")) {
            doOverheat(e.getPlayer());
        }
    }

    @Override
    public void initialize() {

    }

    private void doOverheat(Player caster) {
        if (!isSkillReady()) {
            caster.sendMessage(Utils.getCooldownMessage(getSkillName(), getCooldown()));
            return;
        }

        RayTraceResult result = Utils.rayTrace(caster, 16, FluidCollisionMode.NEVER);
        if (result != null && result.getHitEntity() != null) {
            SorcererConfig sorcererConfig = SorcererConfig.getInstance();
            //doRayTrace only returns livingEntities so no need to check
            new OverheatTask((LivingEntity) result.getHitEntity()).runTaskLater(getPlugin(), sorcererConfig.overheatDuration * 20L);
            setCooldown(sorcererConfig.overheatCooldown);
        }
    }
}
