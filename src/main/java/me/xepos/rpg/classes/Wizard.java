package me.xepos.rpg.classes;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.utils.Utils;
import me.xepos.rpg.configuration.WizardConfig;
import me.xepos.rpg.enums.DamageTakenSource;
import me.xepos.rpg.enums.MultiplierOperation;
import me.xepos.rpg.events.XRPGDamageTakenModifiedEvent;
import me.xepos.rpg.tasks.RemoveDTModifierTask;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Wizard extends XRPGClass {
    public Wizard(XRPG plugin) {
        super(plugin);
    }
    
    private final WizardConfig wizardConfig = WizardConfig.getInstance();

    private byte fireBallStacks = 0;
    private final byte maxFireballStacks = wizardConfig.maxFireballStacks;
    private long lastStackGained = System.currentTimeMillis();
    private long smallFireballCooldown = Utils.setSkillCooldown(wizardConfig.smallFireballCooldown);
    private long meteorCooldown = Utils.setSkillCooldown(wizardConfig.meteorCooldown);
    private long shatterCooldown = Utils.setSkillCooldown(wizardConfig.shatterCooldown);
    private long zephyrCooldown = Utils.setSkillCooldown(wizardConfig.zephyrCooldown);
    private long forceFieldCooldown = Utils.setSkillCooldown(wizardConfig.forcefieldCooldown);


    @Override
    public void onHit(EntityDamageByEntityEvent e) {
        e.setDamage(e.getDamage() / 2);
    }

    @Override
    public void onHurt(EntityDamageByEntityEvent e) {
        Player player = (Player)e.getEntity();
        if (player.getHealth() <= Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue() / 2)
        {
            if (e.getDamager() instanceof Projectile || e.getDamager() instanceof Explosive)
            {
                if (Utils.isSkillReady(forceFieldCooldown))
                {
                    e.setCancelled(true);
                    this.forceFieldCooldown = Utils.setSkillCooldown(wizardConfig.forcefieldCooldown);
                    player.sendMessage(ChatColor.RED + "Wind Barrier is now on cooldown for 10 seconds!");
                }
            }
        }

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

        //Fireball stacks expire after x seconds
        if (lastStackGained + wizardConfig.smallFireballCooldown * 2000L < System.currentTimeMillis() && fireBallStacks != 0)
        {
            fireBallStacks = 0;
            TextComponent text = new TextComponent("Fireball stacks lost!");
            text.setColor(ChatColor.RED.asBungee());
            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, text);
        }

        if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD) {
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                doMeteor(e);
            } else if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                doFireball(e);
            }
        }
        else if(e.getPlayer().getInventory().getItemInMainHand().getType() == Material.STICK)
        {
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)
            {
                doShatter(e);
            }
            else if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
            {
                doZephyr(e);
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

    }

    private void incrementFireBallStacks(byte maxFireballStacks)
    {
        if (fireBallStacks < maxFireballStacks)
        {
            fireBallStacks++;
        }
    }

    private void doMeteor(PlayerInteractEvent e){
        //Cancel if skill is still on cooldown and send a message.
        if (!Utils.isSkillReady(meteorCooldown))
        {
            e.getPlayer().sendMessage(Utils.getCooldownMessage("Meteor", meteorCooldown));
            return;
        }
        //Meteor Skill logic
        Location loc = Utils.getTargetBlock(e.getPlayer(), wizardConfig.maxCastRange).getLocation();
        e.getPlayer().sendMessage("X: " + loc.getX() + " Y: " + loc.getY() + " Z: " + loc.getZ()); //debug message
        loc.setY(loc.getY() + 15 - fireBallStacks * 2);

        Fireball fireball = loc.getWorld().spawn(loc, Fireball.class);
        fireball.setShooter(e.getPlayer());
        fireball.setDirection(new Vector(0, -1, 0));

        if (!plugin.fireBalls.containsKey(fireball.getEntityId()))
            plugin.fireBalls.put(fireball.getEntityId(), wizardConfig.meteorExplosionStrength * (fireBallStacks + 1));

        meteorCooldown = Utils.setSkillCooldown(wizardConfig.meteorCooldown);
    }

    private void doFireball(PlayerInteractEvent e)
    {
        //Cancel if skill is still on cooldown and send a message.
        if (!Utils.isSkillReady(smallFireballCooldown))
        {
            e.getPlayer().sendMessage(Utils.getCooldownMessage("Fireball", smallFireballCooldown));
            return;
        }
        //Skill logic
        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1F, 1F);
        Fireball fireball = e.getPlayer().launchProjectile(SmallFireball.class);
        fireball.setShooter(e.getPlayer());

        if (!plugin.fireBalls.containsKey(fireball.getEntityId()))
            plugin.fireBalls.put(fireball.getEntityId(), wizardConfig.smallFireballDamage * 2); //For some reason damage is halved so doubling it to get proper value

        this.incrementFireBallStacks(this.maxFireballStacks);
        this.lastStackGained = System.currentTimeMillis();
        this.smallFireballCooldown = Utils.setSkillCooldown(wizardConfig.smallFireballCooldown);

        TextComponent text = new TextComponent("You now have " + fireBallStacks + " fireball stacks");
        text.setColor(ChatColor.DARK_GREEN.asBungee());
        e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR ,text);
    }

    @SuppressWarnings("all")
    private void doShatter(PlayerInteractEvent e){
        if (!Utils.isSkillReady(shatterCooldown))
        {
            e.getPlayer().sendMessage(Utils.getCooldownMessage("Shatter", shatterCooldown));
            return;
        }
        Location loc = Utils.getTargetBlock(e.getPlayer(), wizardConfig.maxCastRange).getLocation();
        List<LivingEntity> livingEntities = new ArrayList(loc.getWorld().getNearbyEntities(loc, 3, 3 , 3, p -> p instanceof LivingEntity && p != e.getPlayer()));

        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_GLASS_BREAK, 1F, 1F);
        for (LivingEntity livingEntity: livingEntities) {

            if (livingEntity instanceof Player) {
                if (ps.isLocationValid(e.getPlayer().getLocation(), livingEntity.getLocation())) {
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, wizardConfig.shatterDuration * 20, 1, false, false, false));
                    XRPGDamageTakenModifiedEvent event = new XRPGDamageTakenModifiedEvent((Player) livingEntity, MultiplierOperation.ADDED, DamageTakenSource.SHATTER, 1.2);
                    Bukkit.getServer().getPluginManager().callEvent(event);

                    new RemoveDTModifierTask((Player) livingEntity, DamageTakenSource.SHATTER).runTaskLater(plugin, wizardConfig.shatterDuration * 20L);
                }
            } else {
                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, wizardConfig.shatterDuration * 20, 1, false, false, false));
            }
        }
        this.shatterCooldown = Utils.setSkillCooldown(wizardConfig.shatterCooldown - fireBallStacks);
    }

    private void doZephyr(PlayerInteractEvent e)
    {
        if (!Utils.isSkillReady(zephyrCooldown))
        {
            e.getPlayer().sendMessage(Utils.getCooldownMessage("Zephyr", zephyrCooldown));
            return;
        }
        List<LivingEntity> entities = Utils.getLivingEntitiesInLine(e.getPlayer(), wizardConfig.maxCastRange);
        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PUFFER_FISH_BLOW_UP, 0.5F, 1F);
        for (LivingEntity entity: entities)
        {
            if (entity != e.getPlayer()) {
                //Subtract 1 from the count to account for user
                if (entity instanceof Player) {
                    Player target = (Player) entity;
                    target.playSound(target.getLocation(), Sound.ENTITY_PUFFER_FISH_BLOW_UP, 0.5F, 1F);
                    if (ps.isLocationValid(e.getPlayer().getLocation(), target.getLocation()))
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, wizardConfig.zephyrBaseDuration + (entities.size() - 1) * 10, fireBallStacks, false, false, false));
                } else
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, wizardConfig.zephyrBaseDuration + (entities.size() - 1) * 10, fireBallStacks, false, false, false));

            }
        }
        this.zephyrCooldown = Utils.setSkillCooldown(wizardConfig.zephyrCooldown - fireBallStacks);
    }
}
