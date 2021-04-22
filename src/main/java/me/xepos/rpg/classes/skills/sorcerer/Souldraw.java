package me.xepos.rpg.classes.skills.sorcerer;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.configuration.SorcererConfig;
import me.xepos.rpg.utils.Utils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;

public class Souldraw extends XRPGSkill {
    public Souldraw(XRPGPlayer xrpgPlayer, String skillName, XRPG plugin) {
        super(xrpgPlayer, skillName, plugin);

        xrpgPlayer.getRightClickEventHandler().addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerInteractEvent)) return;
        PlayerInteractEvent e = (PlayerInteractEvent) event;
        if (e.getItem() == null || e.getItem().getType() != Material.ENCHANTED_BOOK) return;

        if (Utils.isItemNameMatching(e.getItem(), "Book of Darkness")) {
            doSouldraw(e.getPlayer());
        }
    }

    @Override
    public void initialize() {

    }

    private void doSouldraw(Player caster) {
        if (isSkillReady()) {
            RayTraceResult result = Utils.rayTrace(caster, 16, FluidCollisionMode.NEVER);
            if (result.getHitEntity() != null) {
                SorcererConfig sorcererConfig = SorcererConfig.getInstance();

                LivingEntity target = (LivingEntity) result.getHitEntity();
                target.damage(sorcererConfig.souldrawDamage, caster);
                //Heal the attacker for half of the damage dealt
                Utils.healLivingEntity(caster, target.getLastDamage() / 2);
                setCooldown(sorcererConfig.souldrawCooldown);
            }
        }
    }
}
