package me.xepos.rpg.classes.skills.brawler;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.configuration.BrawlerConfig;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InnerStrength extends XRPGSkill {
    private final LotusStrike lotusStrike;
    BrawlerConfig brawlerConfig = BrawlerConfig.getInstance();

    private List<PotionEffect> defEffects = new ArrayList<PotionEffect>() {{
        add(new PotionEffect(PotionEffectType.REGENERATION, brawlerConfig.effectDuration * 20, 1, false, false, true));
        add(new PotionEffect(PotionEffectType.SLOW_FALLING, brawlerConfig.effectDuration * 20, 0, false, false, true));
        add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, brawlerConfig.effectDuration * 20, 0, false, false, true));
        add(new PotionEffect(PotionEffectType.ABSORPTION, brawlerConfig.effectDuration * 20, 0, false, false, true));
    }};

    public InnerStrength(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin, LotusStrike lotusStrike) {
        super(xrpgPlayer, skillName, cooldown, plugin);

        this.lotusStrike = lotusStrike;
        xrpgPlayer.getRightClickEventHandler().addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (event instanceof PlayerInteractEntityEvent) {
            PlayerInteractEntityEvent e = (PlayerInteractEntityEvent) event;
            if (e.getRightClicked() instanceof LivingEntity && !(e.getRightClicked() instanceof Villager)) {
                if (lotusStrike.canUseLotus(e.getPlayer())) {
                    useInnerStrength(e.getPlayer());
                }
            }
        } else if (event instanceof PlayerInteractEvent) {
            PlayerInteractEvent e = (PlayerInteractEvent) event;


            if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {

                if (lotusStrike.canUseLotus(e.getPlayer())) {
                    useInnerStrength(e.getPlayer());
                }
            }

        }
    }

    @Override
    public void initialize() {

    }

    private void useInnerStrength(Player player) {
        applyTriggerEffect(player);
        Utils.healLivingEntity(player, brawlerConfig.innerStrengthHealAmount);
        player.sendMessage("Inner Strength healed you for " + brawlerConfig.innerStrengthHealAmount);
    }

    private void applyTriggerEffect(Player player) {
        Random rand = new Random();

        player.addPotionEffect(defEffects.get(rand.nextInt(defEffects.size())));

        lotusStrike.incrementHitCount();
    }
}
