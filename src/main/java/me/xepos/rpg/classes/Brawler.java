package me.xepos.rpg.classes;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.classes.skills.brawler.InnerStrength;
import me.xepos.rpg.classes.skills.brawler.LotusStrike;
import me.xepos.rpg.configuration.BrawlerConfig;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;

@SuppressWarnings("unused")
public class Brawler extends XRPGClass {

    private final BrawlerConfig brawlerConfig = BrawlerConfig.getInstance();

    private final LotusStrike lotusStrike = new LotusStrike(plugin, "Lotus Strike");
    private final InnerStrength innerStrength = new InnerStrength(plugin, "Inner Strength", lotusStrike);


    public Brawler(XRPG plugin) {
        super(plugin);
    }

    @Override
    public void onHit(EntityDamageByEntityEvent e) {
        lotusStrike.activate(e);
    }

    @Override
    public void onHurt(EntityDamageByEntityEvent e) {

    }

    @Override
    public void onJoin(PlayerJoinEvent e) {
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
        innerStrength.activate(e);
    }

    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent e) {

    }

    @Override
    public void onInteractWithEntity(PlayerInteractEntityEvent e) {
        innerStrength.activate(e);
    }

    @Override
    public void onShootBow(EntityShootBowEvent e) {

    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}

