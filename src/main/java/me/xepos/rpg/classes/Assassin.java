package me.xepos.rpg.classes;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.configuration.AssassinConfig;
import me.xepos.rpg.tasks.EndInvisibilityTask;
import me.xepos.rpg.utils.Utils;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//This class should probably implement reflection to make it safe to use
//across multiple version of spigot.
//This is mainly due to the smokebomb item using nms.

public class Assassin extends XRPGClass {
    public Assassin(XRPG plugin) {
        super(plugin);
    }

    private final AssassinConfig assassinConfig = AssassinConfig.getInstance();

    private long smokeCooldown = Utils.setSkillCooldown(assassinConfig.smokeBombCooldown);
    private long cutThroatCooldown = Utils.setSkillCooldown(assassinConfig.cutThroatCooldown);
    private long shadowStepCooldown = Utils.setSkillCooldown(assassinConfig.shadowStepCooldown);
    private ArmorStand substitute = null;

    @Override
    public void onHit(EntityDamageByEntityEvent e) {
        LivingEntity entity = (LivingEntity) e.getEntity();
        if (e.getEntity() instanceof LivingEntity) {
            Vector attackerDirection = e.getDamager().getLocation().getDirection();
            Vector victimDirection = e.getEntity().getLocation().getDirection();
            //determine if the dot product between the vectors is greater than 0
            //If it is, we can conclude that the attack was a backstab
            if (attackerDirection.dot(victimDirection) > 0) {
                if (entity.getHealth() <= entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / (100 / assassinConfig.executeThreshold) && Utils.isSkillReady(cutThroatCooldown)) {
                    entity.setHealth(0.0);
                    if (entity instanceof Player) {
                        e.getDamager().getWorld().getNearbyEntities(e.getDamager().getLocation(), 10, 5, 10, p -> p instanceof Player).forEach(p -> p.sendMessage(entity.getName() + " was executed by " + e.getDamager().getName() + "!"));
                    }
                    cutThroatCooldown = Utils.setSkillCooldown(assassinConfig.cutThroatCooldown);

                } else {
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
            doSmokebomb(player);
        } else if (itemInMainHand.getType().toString().toLowerCase().contains("_axe") || itemInMainHand.getType().toString().toLowerCase().contains("_sword")) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
                doSubstitute(player);
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


    public void applyEffects(Player player) {
        super.applyEffects(player);
        Utils.addUniqueModifier(player, Attribute.GENERIC_MOVEMENT_SPEED, assassinConfig.moveSpeedModifier);
    }

    private void doSubstitute(Player player) {
        if (substitute == null) {
            if (!Utils.isSkillReady(shadowStepCooldown)) {
                player.sendMessage(Utils.getCooldownMessage("Substitute", shadowStepCooldown));
                return;
            }

            //Creating armorstand at player's location and setting the right properties
            ArmorStand armorStand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setCollidable(false);

            armorStand.setCustomName(player.getName());
            armorStand.setCustomNameVisible(true);


            setArmorStandArmor(player, armorStand);

            substitute = armorStand;
            shadowStepCooldown = Utils.setSkillCooldown(assassinConfig.shadowStepCooldown);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (substitute != null) {
                        substitute.remove();
                        substitute = null;
                    }
                }
            }.runTaskLater(plugin, assassinConfig.shadowStepDuration * 20L);
        } else {
            player.teleport(substitute.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            substitute.remove();
            substitute = null;
        }
    }

    @SuppressWarnings("all")
    private void setArmorStandArmor(Player player, ArmorStand armorStand) {
        HashMap<EquipmentSlot, org.bukkit.inventory.ItemStack> armor = new HashMap() {
            {
                put(EquipmentSlot.CHEST, new org.bukkit.inventory.ItemStack(Material.LEATHER_CHESTPLATE));
                put(EquipmentSlot.LEGS, new org.bukkit.inventory.ItemStack(Material.LEATHER_LEGGINGS));
                put(EquipmentSlot.FEET, new org.bukkit.inventory.ItemStack(Material.LEATHER_BOOTS));
            }
        };

        EntityEquipment armorStandEquipement = armorStand.getEquipment();

        //Setting up the player head
        org.bukkit.inventory.ItemStack playerHead = new org.bukkit.inventory.ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getPlayer(player.getUniqueId()));
        playerHead.setItemMeta(skullMeta);
        armorStandEquipement.setHelmet(playerHead);

        //Setting up the leather armor
        for (EquipmentSlot slot : armor.keySet()) {
            org.bukkit.inventory.ItemStack item = armor.get(slot);
            LeatherArmorMeta itemMeta = (LeatherArmorMeta) item.getItemMeta();
            itemMeta.setColor(Color.BLACK);
            item.setItemMeta(itemMeta);

            armorStandEquipement.setItem(slot, armor.get(slot), true);
            armorStand.addEquipmentLock(slot, ArmorStand.LockType.REMOVING_OR_CHANGING);
        }
    }

    private void doSmokebomb(Player player) {
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
        List<Player> affectedPlayers = new ArrayList(player.getWorld().getNearbyEntities(player.getLocation(), 20, 10, 20, p -> p instanceof Player && p != player));
        for (Player ent : affectedPlayers) {
            ((CraftPlayer) ent).getHandle().playerConnection.sendPacket(entityEquipmentPacket);//send affected players the packet
        }

        smokeCooldown = Utils.setSkillCooldown(assassinConfig.smokeBombCooldown);
        new EndInvisibilityTask(player, affectedPlayers, this).runTaskLater(plugin, assassinConfig.smokeBombDuration * 20L);
    }

}
