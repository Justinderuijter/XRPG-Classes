package me.xepos.rpg.classes;

import me.xepos.rpg.utils.Utils;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.configuration.RangerConfig;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings("all")
public class Ranger extends XRPGClass{

    private RangerConfig rangerConfig = RangerConfig.getInstance();

    private long snipeShotCooldown;
    private long explosiveShotCooldown;
    private long lightningArrowCooldown;
    private long enderArrowCooldown;
    private long arrowOfHungerCooldown;
    private long arrowOfDarknessCooldown;
    private long soulShotCooldown;



    private ShotType currentShotType = ShotType.NORMAL;

    public Ranger(XRPG plugin) {
        super(plugin);
    }

    @Override
    public void onHit(EntityDamageByEntityEvent e) {

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
        PlayerInventory inventory = e.getPlayer().getInventory();
        if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (inventory.getItemInMainHand().getType() == Material.BOW)
            {
                currentShotType = currentShotType.next();
                e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Current shot type: " + Utils.enumTypeFormatter(currentShotType.toString().toLowerCase())));
            }
        }
    }

    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent e) {

    }

    @Override
    public void onInteractWithEntity(PlayerInteractEntityEvent e) {

    }

    @Override
    public void onShootBow(EntityShootBowEvent e) {
        if(e.getBow().getType() == Material.BOW && e.getProjectile() instanceof Arrow)
        {
            Arrow arrow = (Arrow) e.getProjectile();
            arrow.setCritical(true);
            switch(currentShotType){
                case SNIPE_SHOT:
                    if (!Utils.isSkillReady(snipeShotCooldown))
                    {
                        e.getEntity().sendMessage(Utils.getCooldownMessage("Snipe Shot", snipeShotCooldown));
                        return;
                    }
                    doSnipeShot(e, arrow);
                    snipeShotCooldown = Utils.setSkillCooldown(rangerConfig.snipeShotCooldown);
                    break;
                case ARROW_OF_HUNGER:
                    if (!Utils.isSkillReady(arrowOfHungerCooldown))
                    {
                        e.getEntity().sendMessage(Utils.getCooldownMessage("Arrow of Hunger", arrowOfHungerCooldown));
                        return;
                    }
                    arrow.addCustomEffect(rangerConfig.hungerEffect, false);
                    arrowOfHungerCooldown = Utils.setSkillCooldown(rangerConfig.arrowOfHungerCooldown);
                    break;
                case LIGHTNING_ARROW:
                    if (!Utils.isSkillReady(lightningArrowCooldown))
                    {
                        e.getEntity().sendMessage(Utils.getCooldownMessage("Lightning Arrow", lightningArrowCooldown));
                        return;
                    }
                    arrow.setCustomName("Lightning");
                    arrow.setCustomNameVisible(false);
                    lightningArrowCooldown = Utils.setSkillCooldown(rangerConfig.lightningArrowCooldown);
                    break;
                case EXPLOSIVE_SHOT:
                    if (!Utils.isSkillReady(explosiveShotCooldown))
                    {
                        e.getEntity().sendMessage(Utils.getCooldownMessage("Explosive Shot", explosiveShotCooldown));
                        return;
                    }
                    arrow.setCustomName("Explosion");
                    arrow.setCustomNameVisible(false);
                    explosiveShotCooldown = Utils.setSkillCooldown(rangerConfig.explosiveShotCooldown);
                    break;
                case ENDER_ARROW:
                    if (!Utils.isSkillReady(enderArrowCooldown))
                    {
                        e.getEntity().sendMessage(Utils.getCooldownMessage("Ender Arrow", enderArrowCooldown));
                        return;
                    }
                    arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                    arrow.setCustomName("Ender");
                    arrow.setCustomNameVisible(false);
                    enderArrowCooldown = Utils.setSkillCooldown(rangerConfig.enderArrowCooldown);
                    break;
                case ARROW_OF_DARKNESS:
                    if (!Utils.isSkillReady(arrowOfDarknessCooldown))
                    {
                        e.getEntity().sendMessage(Utils.getCooldownMessage("Arrow of Darkness", arrowOfDarknessCooldown));
                        return;
                    }
                    arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                    arrow.setCustomName("Darkness");
                    arrow.setCustomNameVisible(false);
                    arrowOfDarknessCooldown = Utils.setSkillCooldown(rangerConfig.arrowOfDarknessCooldown);
                    break;
                case SOUL_SHOT:
                    if (!Utils.isSkillReady(soulShotCooldown))
                    {
                        e.getEntity().sendMessage(Utils.getCooldownMessage("Soul Shot", soulShotCooldown));
                        return;
                    }
                    arrow.setDamage(0);
                    arrow.setCustomName("Soul");
                    arrow.setCustomNameVisible(false);
                    break;
                default:
                    break;
            }
        }
    }


    private void doSnipeShot(EntityShootBowEvent e, Arrow arrow)
    {
        float force = e.getForce();
        arrow.setGravity(false);
        arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
        arrow.setPierceLevel(Math.round(force) + 1);
        arrow.setDamage(arrow.getDamage() * rangerConfig.snipeShotDamageMultiplier * force);

        new BukkitRunnable() {
            @Override
            public void run() {
                arrow.remove();
            }
        }.runTaskLater(plugin, (int)(force * 300));
    }


    private enum ShotType {
        NORMAL,
        SNIPE_SHOT,
        EXPLOSIVE_SHOT,
        LIGHTNING_ARROW,
        ARROW_OF_DARKNESS,
        SOUL_SHOT,
        ARROW_OF_HUNGER,
        ENDER_ARROW;

        private static ShotType[] vals = values();
        public ShotType next()
        {
            return vals[(this.ordinal()+1) % vals.length];
        }
    }

    @Override
    public void applyEffects(Player player) {
        super.applyEffects(player);
        Utils.addUniqueModifier(player, Attribute.GENERIC_MOVEMENT_SPEED, rangerConfig.moveSpeedModifier);
    }
}
