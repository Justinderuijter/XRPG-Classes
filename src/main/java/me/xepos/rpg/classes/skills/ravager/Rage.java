package me.xepos.rpg.classes.skills.ravager;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import me.xepos.rpg.tasks.RavagerRageTask;
import me.xepos.rpg.utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class Rage extends XRPGSkill {
    private byte currentRage = 0;
    private byte lastRage = 0;
    private final byte maxRage = 100;
    private byte rageLevel = 0;
    private boolean isLocked = false;
    BukkitTask rageTask = null;

    public Rage(XRPGPlayer xrpgPlayer, String skillName, int cooldown, XRPG plugin) {
        super(xrpgPlayer, skillName, cooldown, plugin);

        setRemainingCooldown(-1);
        xrpgPlayer.getEventHandler("DAMAGE_DEALT").addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

        Player player = (Player) e.getDamager();
        rageLevel = getRageLevel(currentRage);

        if (!isLocked) //Prevent infinite looping
            applyDamageRageEffect(e);

        if (player.getInventory().getItemInMainHand().getType().toString().toLowerCase().contains("_axe")) {
            e.setDamage(e.getDamage() * ravagerConfig.axeDamageMultiplier);

            //increase rage count
            if (((LivingEntity) e.getEntity()).getHealth() <= e.getFinalDamage()) {
                incrementRage((byte) 5);
            }

            incrementRage((byte) 5);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Current Rage: " + currentRage + " (+)", ChatColor.RED.asBungee()));
            if (rageTask == null || rageTask.isCancelled())
                rageTask = new RavagerRageTask(player, (byte) 5).runTaskTimerAsynchronously(getPlugin(), 100L, 100L);


        } else
            e.setDamage(e.getDamage() * ravagerConfig.otherDamageMultiplier);
    }

    @Override
    public void initialize() {

    }

    private void applyDamageRageEffect(EntityDamageByEntityEvent e) {
        Player player = (Player) e.getDamager();
        RavagerConfig ravagerConfig = RavagerConfig.getInstance();
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
}
