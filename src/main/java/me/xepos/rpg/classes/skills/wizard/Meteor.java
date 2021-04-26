package me.xepos.rpg.classes.skills.wizard;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.datatypes.fireballData;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Fireball;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class Meteor extends XRPGSkill {
    private FireballStackData fireballStackData;

    public Meteor(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, FireballStackData fireballStackData) {
        super(xrpgPlayer, skillVariables, plugin);

        this.fireballStackData = fireballStackData;
        xrpgPlayer.getEventHandler("LEFT_CLICK").addSkill(this);
    }

    public Meteor(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getEventHandler("LEFT_CLICK").addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerInteractEvent)) return;
        PlayerInteractEvent e = (PlayerInteractEvent) event;
        if (e.getItem() == null || e.getItem().getType() != Material.BLAZE_ROD) return;

        doMeteor(e);
    }

    @Override
    public void initialize() {
        for (XRPGSkill skill : getXRPGPlayer().getEventHandler("RIGHT_CLICK").getSkills()) {
            if (skill instanceof me.xepos.rpg.classes.skills.wizard.Fireball) {
                this.fireballStackData = ((me.xepos.rpg.classes.skills.wizard.Fireball) skill).getFireballStackData();
                return;
            }
        }
    }

    private void doMeteor(PlayerInteractEvent e) {
        if (!isSkillReady()) {
            e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }
        //WizardConfig wizardConfig = WizardConfig.getInstance();

        //Meteor Skill logic
        Location loc = Utils.getTargetBlock(e.getPlayer(), 32).getLocation();
        e.getPlayer().sendMessage("X: " + loc.getX() + " Y: " + loc.getY() + " Z: " + loc.getZ()); //debug message

        int stacks = 0;
        if (fireballStackData != null) {
            stacks = fireballStackData.getFireBallStacks();
        }

        loc.setY(loc.getY() + 15 - stacks * 2);
        org.bukkit.entity.Fireball fireball = loc.getWorld().spawn(loc, Fireball.class);
        fireball.setShooter(e.getPlayer());
        fireball.setDirection(new Vector(0, -1, 0));

        if (!getPlugin().fireBalls.containsKey(fireball.getEntityId()))
            getPlugin().fireBalls.put(fireball.getEntityId(), new fireballData(getDamage() * (stacks + 1), 10));

        setRemainingCooldown(getCooldown());
    }
}
