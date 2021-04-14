package me.xepos.rpg.classes;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.classes.skills.assassin.CutThroat;
import me.xepos.rpg.classes.skills.assassin.ShadowStep;
import me.xepos.rpg.classes.skills.assassin.Smokebomb;
import me.xepos.rpg.configuration.AssassinConfig;
import me.xepos.rpg.utils.Utils;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;

//This class should probably implement reflection to make it safe to use
//across multiple version of spigot.
//This is mainly due to the smokebomb item using nms.

public class Assassin extends XRPGClass {
    public Assassin(XRPG plugin) {
        super(plugin);
    }

    private final AssassinConfig assassinConfig = AssassinConfig.getInstance();

    private final CutThroat cutThroat = new CutThroat(plugin, "Cut-Throat");
    private final ShadowStep shadowStep = new ShadowStep(plugin, "Shadowstep");
    private final Smokebomb smokebomb = new Smokebomb(plugin, "Smokebomb");


    @Override
    public void onHit(EntityDamageByEntityEvent e) {
        cutThroat.activate(e);
    }

    @Override
    public void onHurt(EntityDamageByEntityEvent e) {
        smokebomb.activate(e);
    }

    @Override
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().setInvisible(false);
        applyEffects(e.getPlayer());
    }

    @Override
    public void onRespawn(PlayerRespawnEvent e) {
        applyEffects(e.getPlayer());
    }

    @Override
    public void onPlayerConsumeItem(PlayerItemConsumeEvent e) {
    }

    @Override
    public void onUseItem(PlayerInteractEvent e) {
        shadowStep.activate(e);
        smokebomb.activate(e);
    }

    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        smokebomb.activate(e);

    }

    @Override
    public void onInteractWithEntity(PlayerInteractEntityEvent e) {

    }

    @Override
    public void onShootBow(EntityShootBowEvent e) {

    }


    public void applyEffects(Player player) {
        super.applyEffects(player);
        Utils.addUniqueModifier(player, Attribute.GENERIC_MOVEMENT_SPEED, assassinConfig.moveSpeedModifier);
    }


}
