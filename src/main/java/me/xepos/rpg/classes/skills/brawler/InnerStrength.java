package me.xepos.rpg.classes.skills.brawler;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.IEffectDuration;
import me.xepos.rpg.classes.skills.XRPGSkill;
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

public class InnerStrength extends XRPGSkill implements IEffectDuration {
    private LotusStrike lotusStrike;
    private int potionDuration = 6;

    private final List<PotionEffect> defEffects = new ArrayList<PotionEffect>() {{
        add(new PotionEffect(PotionEffectType.REGENERATION, potionDuration * 20, 1, false, false, true));
        add(new PotionEffect(PotionEffectType.SLOW_FALLING, potionDuration * 20, 0, false, false, true));
        add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, potionDuration * 20, 0, false, false, true));
        add(new PotionEffect(PotionEffectType.ABSORPTION, potionDuration * 20, 0, false, false, true));
    }};

    public InnerStrength(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin, LotusStrike lotusStrike) {
        super(xrpgPlayer, skillName, cooldown, plugin);

        this.lotusStrike = lotusStrike;
        xrpgPlayer.getRightClickEventHandler().addSkill(this);
    }

    public InnerStrength(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin) {
        super(xrpgPlayer, skillName, cooldown, plugin);

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
        for (XRPGSkill skill : getXRPGPlayer().getDamageDealtEventHandler().getSkills()) {
            if (skill instanceof LotusStrike) {
                this.lotusStrike = (LotusStrike) skill;
                return;
            }
        }
    }

    private void useInnerStrength(Player player) {
        applyTriggerEffect(player);
        Utils.healLivingEntity(player, getDamage());
        player.sendMessage("Inner Strength healed you for " + getDamage());
    }

    private void applyTriggerEffect(Player player) {
        Random rand = new Random();

        player.addPotionEffect(defEffects.get(rand.nextInt(defEffects.size())));

        lotusStrike.incrementHitCount();
    }

    @Override
    public int getEffectDuration() {
        return potionDuration;
    }

    @Override
    public void setEffectDuration(int duration) {
        this.potionDuration = duration;
    }

}
