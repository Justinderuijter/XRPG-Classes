package me.xepos.rpg.classes;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.classes.skill.XRPGSkill;
import me.xepos.rpg.classes.skill.assassin.CutThroat;
import me.xepos.rpg.classes.skill.assassin.ShadowStep;
import me.xepos.rpg.classes.skill.assassin.Smokebomb;
import me.xepos.rpg.configuration.AssassinConfig;
import me.xepos.rpg.enums.SkillActivationType;
import me.xepos.rpg.utils.Utils;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.List;

//This class should probably implement reflection to make it safe to use
//across multiple version of spigot.
//This is mainly due to the smokebomb item using nms.

public class Assassin extends XRPGClass {
    public Assassin(XRPG plugin) {
        super(plugin);
    }

    private final AssassinConfig assassinConfig = AssassinConfig.getInstance();

    private List<XRPGSkill> skills = new ArrayList<XRPGSkill>() {{
        add(new CutThroat(plugin, SkillActivationType.HIT_ENTITY, "Cut-Throat"));
        add(new ShadowStep(plugin, SkillActivationType.USE_ITEM_RIGHT, "Shadowstep"));
        List<SkillActivationType> types = new ArrayList<>();
        types.add(SkillActivationType.USE_ITEM_RIGHT);
        types.add(SkillActivationType.HIT_BY_ENTITY);
        types.add(SkillActivationType.PROJECTILE_LAUNCH);
        add(new Smokebomb(plugin, types, "Smokebomb"));
    }};

    @Override
    public void onHit(EntityDamageByEntityEvent e) {
        for (XRPGSkill skill : skills) {
            if (skill.getActivationTypes().contains(SkillActivationType.HIT_ENTITY)) {
                skill.activate(e);
            }
        }
    }

    @Override
    public void onHurt(EntityDamageByEntityEvent e) {
        for (XRPGSkill skill : skills) {
            if (skill.getActivationTypes().contains(SkillActivationType.HIT_BY_ENTITY)) {
                skill.activate(e);
            }
        }
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
        for (XRPGSkill skill : skills) {
            if (skill.getActivationTypes().contains(SkillActivationType.USE_ITEM_RIGHT)) {
                skill.activate(e);
            }
        }
    }

    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        for (XRPGSkill skill : skills) {
            if (skill.getActivationTypes().contains(SkillActivationType.PROJECTILE_LAUNCH)) {
                skill.activate(e);
            }
        }

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
