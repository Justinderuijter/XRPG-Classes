package me.xepos.rpg.classes;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.configuration.AssassinConfig;
import me.xepos.rpg.tasks.EndInvisibilityTask;
import me.xepos.rpg.Utils;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

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

    private final PotionEffect poisonEffect = new PotionEffect(PotionEffectType.POISON, assassinConfig.poisonDuration * 20, assassinConfig.poisonAmplifier, true, true, false);
    private long smokeCooldown = Utils.setSkillCooldown(assassinConfig.smokeBombCooldown);
    private long cutThroatCooldown = Utils.setSkillCooldown(assassinConfig.cutThroatCooldown);

    @Override
    public void onHit(EntityDamageByEntityEvent e) {
        LivingEntity entity = (LivingEntity) e.getEntity();
        entity.addPotionEffect(poisonEffect);
        if (e.getEntity() instanceof LivingEntity) {
            Vector attackerDirection = e.getDamager().getLocation().getDirection();
            Vector victimDirection = e.getEntity().getLocation().getDirection();
            //determine if the dot product between the vectors is greater than 0
            if (attackerDirection.dot(victimDirection) > 0) {
                if (entity.getHealth() <= entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / (100 / assassinConfig.executeThreshold) && Utils.isSkillReady(cutThroatCooldown))
                {
                    entity.setHealth(0.0);
                    if(entity instanceof Player) {
                        e.getDamager().getWorld().getNearbyEntities(e.getDamager().getLocation(), 10, 5, 10, p -> p instanceof Player).forEach(p -> p.sendMessage(entity.getName() + " was executed by " + e.getDamager().getName() + "!"));
                    }
                    cutThroatCooldown = Utils.setSkillCooldown(assassinConfig.cutThroatCooldown);

                }
                else {
                    double finalDmg = e.getDamage() * assassinConfig.backStrikeMultiplier;
                    e.setDamage(finalDmg);
                    e.getDamager().sendMessage("Backstrike dealt " + finalDmg + " damage!");
                }
            }
        }

    }

    @Override
    public void onHurt(EntityDamageByEntityEvent e) {
        Player player = (Player) e.getEntity();
        if (player.isInvisible()) {
            player.setInvisible(false);
            player.sendMessage("Invisibility ended by damage!");
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
        Player player = e.getPlayer();
        org.bukkit.inventory.ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getType() == Material.SNOWBALL && Utils.isItemNameMatching(itemInMainHand, "Smoke Bomb")) {
            if (smokeCooldown > System.currentTimeMillis()) {
                player.sendMessage(Color.RED + "Smokebomb is still on cooldown for " + (smokeCooldown - System.currentTimeMillis()) / 1000 + " seconds");
                return;
            }

            player.setInvisible(true);
            player.sendMessage("You're now invisible!");
            //Custom packet sending starts here, if something breaks between versions it's probably this
            final List<com.mojang.datafixers.util.Pair<EnumItemSlot, ItemStack>> equipmentList = new ArrayList<>();
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR))));
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR))));
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR))));
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR))));
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR))));
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.OFFHAND, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR))));
            //Creating the packet we're going to send
            final PacketPlayOutEntityEquipment entityEquipmentPacket = new PacketPlayOutEntityEquipment(player.getEntityId(), equipmentList);

            //List all affected players so we can undo the packet later
            List<Player> affectedPlayers = new ArrayList<>();
            for (Entity ent : player.getNearbyEntities(10, 10, 10)) {
                if (ent instanceof Player && ent != player) {
                    affectedPlayers.add((Player) ent); //Add to list for later reference
                    ((CraftPlayer) ent).getHandle().playerConnection.sendPacket(entityEquipmentPacket);//send affected players the packet
                }
            }
            smokeCooldown = Utils.setSkillCooldown(assassinConfig.smokeBombCooldown);
            new EndInvisibilityTask(player, affectedPlayers, this).runTaskLater(plugin, assassinConfig.smokeBombDuration * 20L);
        }
    }

    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        //Cancel throwing snowball if class specific item (Smoke Bomb)
        if (e.getEntity().getShooter() instanceof Player) {
            Player player = (Player) e.getEntity().getShooter();
            if (player.getInventory().getItemInMainHand().getType() == Material.SNOWBALL && Utils.isItemNameMatching(player.getInventory().getItemInMainHand(), "Smoke Bomb")) {
                if (e.getEntity() instanceof Snowball) {
                    e.setCancelled(true);
                }
            }
        }

    }

    @Override
    public void onInteractWithEntity(PlayerInteractEntityEvent e) {

    }

    @Override
    public void onShootBow(EntityShootBowEvent e) {

    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public void applyEffects(Player player) {
        super.applyEffects(player);
        Utils.addUniqueModifier(player, Attribute.GENERIC_MOVEMENT_SPEED, assassinConfig.moveSpeedModifier);
    }

}
