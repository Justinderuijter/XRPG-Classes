package me.xepos.rpg.classes;

import me.xepos.rpg.utils.Utils;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.configuration.RavagerConfig;
import me.xepos.rpg.tasks.RavagerLandTask;
import me.xepos.rpg.tasks.RavagerRageTask;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.List;

public class Ravager extends XRPGClass {

    private final RavagerConfig ravagerConfig = RavagerConfig.getInstance();

    private byte currentRage = 0;
    private byte lastRage = 0;
    private final byte maxRage = 100;
    private byte rageLevel = 0;
    private long flameSlashCooldown = Utils.setSkillCooldown(ravagerConfig.flameSlashCooldown);
    private long soaringSlashCooldown = Utils.setSkillCooldown(ravagerConfig.SoaringSlashCooldown);
    private boolean isLocked = false;
    BukkitTask rageTask = null;
    BukkitTask landTask = null;


    public Ravager(XRPG plugin) {
        super(plugin);
    }

    @Override
    public void onHit(EntityDamageByEntityEvent e) {
        Player player = (Player) e.getDamager();
        rageLevel = getRageLevel(currentRage);

        if (!isLocked) //Prevent infinite looping
            applyDamageRageEffect(e);

        if (player.getInventory().getItemInMainHand().getType().toString().toLowerCase().contains("_axe")) {
            e.setDamage(e.getDamage() * ravagerConfig.axeDamageMultiplier);

            //increase rage count
            if (((LivingEntity) e.getEntity()).getHealth() <= e.getFinalDamage()) {
                incrementRage(ravagerConfig.bonusRageOnKill);
            }

            incrementRage(ravagerConfig.rageOnHit);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Current Rage: " + currentRage + " (+)", ChatColor.RED.asBungee()));
            if (rageTask == null || rageTask.isCancelled())
                rageTask = new RavagerRageTask(player, ravagerConfig.rageReductionPer5s).runTaskTimerAsynchronously(super.plugin, 100L, 100L);


        } else
            e.setDamage(e.getDamage() * ravagerConfig.otherDamageMultiplier);

    }

    @Override
    public void onHurt(EntityDamageByEntityEvent e) {
        rageLevel = getRageLevel(currentRage);

        switch (rageLevel) {
            case 1:
                e.setDamage(e.getDamage() * 0.95);
                break;
            case 2:
                e.setDamage(e.getDamage() * 0.90);
                break;
            case 3:
                e.setDamage(e.getDamage() * 0.85);
                break;
            case 4:
                e.setDamage(e.getDamage() * 0.80);
                break;
        }
    }

    @Override
    public void onJoin(PlayerJoinEvent e) {
        applyEffects(e.getPlayer());
    }

    @Override
    public void onRespawn(PlayerRespawnEvent e) {
        applyEffects(e.getPlayer());
        this.currentRage = 0;
    }

    @Override
    public void onPlayerConsumeItem(PlayerItemConsumeEvent e) {
    }

    @Override
    public void onUseItem(PlayerInteractEvent e) {

        if (e.getPlayer().getInventory().getItemInMainHand().getType().toString().toLowerCase().contains("_axe")) {
            if (!e.getPlayer().isSneaking()) {
                doFlameSlash(e);
            } else {
                doSoaringSlash(e);
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

    private void applyDamageRageEffect(EntityDamageByEntityEvent e) {
        Player player = (Player) e.getDamager();
        switch (rageLevel) {
            case 0:
                Utils.removeUniqueModifier(player, Attribute.GENERIC_ATTACK_SPEED, ravagerConfig.attackSpeedModifier);
            case 1:
                Utils.removeUniqueModifier(player, Attribute.GENERIC_ATTACK_SPEED, ravagerConfig.attackSpeedModifier);
                e.setDamage(e.getDamage() * ravagerConfig.rageTierOneMultiplier);
                break;
            case 2:
                Utils.removeUniqueModifier(player, Attribute.GENERIC_ATTACK_SPEED, ravagerConfig.attackSpeedModifier);
                e.setDamage(e.getDamage() * ravagerConfig.rageTierTwoMultiplier);
                break;
            case 3:
                Utils.addUniqueModifier(player, Attribute.GENERIC_ATTACK_SPEED, ravagerConfig.attackSpeedModifier);
                e.setDamage(e.getDamage() * ravagerConfig.rageTierThreeMultiplier);
                break;
            case 4:
                Location loc = player.getLocation();
                loc.getWorld().playSound(loc, Sound.BLOCK_FIRE_AMBIENT, 2F, 1F);
                Utils.addUniqueModifier(player, Attribute.GENERIC_ATTACK_SPEED, ravagerConfig.attackSpeedModifier);

                isLocked = true;

                if (!player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100, 0, false, false, true));
                }

                e.setDamage(e.getDamage() * ravagerConfig.rageTierFourMultiplier);

                if (e.getEntity() instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) e.getEntity();
                    List<Entity> entities = livingEntity.getNearbyEntities(ravagerConfig.rageAoERange, 3, ravagerConfig.rageAoERange);
                    for (Entity entity : entities) {
                        if (entity instanceof LivingEntity && entity != livingEntity && entity != player && !(entity instanceof Villager)) {
                            ((LivingEntity) entity).damage(e.getDamage(), player);
                            if (entity instanceof Player && lastRage <= 80)
                                entity.sendMessage(ChatColor.RED + player.getName() + " gave in to their rage!");
                        }
                    }
                }
                isLocked = false;
                break;
        }
    }

    private void incrementRage(byte count) {
        if (currentRage + count <= 100)
            currentRage = (byte) (currentRage + 5);
        else
            currentRage = maxRage;
    }

    public void decreaseCurrentRage(byte count) {
        if (currentRage >= count)
            currentRage = (byte) (currentRage - count);

        else
            currentRage = 0;
    }

    public byte getCurrentRage() {
        return currentRage;
    }

    private byte getRageLevel(byte currentRage) {
        lastRage = currentRage;
        if (currentRage > 80)
            return 4;
        else if (currentRage > 60)
            return 3;
        else if (currentRage > 40)
            return 2;
        else if (currentRage > 20)
            return 1;
        else
            return 0;

    }

    private void doFlameSlash(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!Utils.isSkillReady(flameSlashCooldown)) {
                e.getPlayer().sendMessage(Utils.getCooldownMessage("Flame Slash", flameSlashCooldown));
                return;
            }

            Fireball fireball = e.getPlayer().launchProjectile(SmallFireball.class);
            fireball.setCustomName("Flame Slash");
            fireball.setCustomNameVisible(false);
            if (!plugin.fireBalls.containsKey(fireball.getEntityId()))
                plugin.fireBalls.put(fireball.getEntityId(), 6.0);

            flameSlashCooldown = Utils.setSkillCooldown(ravagerConfig.flameSlashCooldown);
        }
    }

    private void doSoaringSlash(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!Utils.isSkillReady(soaringSlashCooldown)) {
                e.getPlayer().sendMessage(Utils.getCooldownMessage("Soaring Slash", soaringSlashCooldown));
                return;
            }

            e.getPlayer().setVelocity(e.getPlayer().getEyeLocation().getDirection().multiply(new Vector(2, 0, 2)).add(new Vector(0, 1, 0)));
            soaringSlashCooldown = Utils.setSkillCooldown(ravagerConfig.SoaringSlashCooldown);
            if (landTask == null || landTask.isCancelled())
                landTask = new RavagerLandTask(e.getPlayer(), ps, partyManager).runTaskTimer(plugin, 5L, 3L);
        }
    }

}
