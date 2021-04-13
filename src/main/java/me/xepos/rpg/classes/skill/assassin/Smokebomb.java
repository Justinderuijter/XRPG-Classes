package me.xepos.rpg.classes.skill.assassin;

import com.mojang.datafixers.util.Pair;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.classes.skill.XRPGSkill;
import me.xepos.rpg.configuration.AssassinConfig;
import me.xepos.rpg.enums.SkillActivationType;
import me.xepos.rpg.tasks.EndInvisibilityTask;
import me.xepos.rpg.utils.Utils;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class Smokebomb extends XRPGSkill {
    public Smokebomb(XRPG plugin, SkillActivationType activationType, String skillName) {
        super(plugin, activationType, skillName);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void activate(Event event) {
        if (event instanceof PlayerInteractEvent) {
            PlayerInteractEvent e = (PlayerInteractEvent) event;
            if (!isSkillReady()) {
                e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getCooldown()));
                return;
            }
            AssassinConfig assassinConfig = AssassinConfig.getInstance();

            e.getPlayer().setInvisible(true);
            e.getPlayer().sendMessage("You're now invisible!");
            //Custom packet sending starts here, if something breaks between versions it's probably this
            final List<Pair<EnumItemSlot, ItemStack>> equipmentList = new ArrayList<>();
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR))));
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR))));
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR))));
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR))));
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR))));
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.OFFHAND, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR))));
            //Creating the packet we're going to send
            final PacketPlayOutEntityEquipment entityEquipmentPacket = new PacketPlayOutEntityEquipment(e.getPlayer().getEntityId(), equipmentList);

            //List all affected players so we can undo the packet later
            List<Player> affectedPlayers = new ArrayList(e.getPlayer().getWorld().getNearbyEntities(e.getPlayer().getLocation(), 20, 10, 20, p -> p instanceof Player && p != e.getPlayer()));
            for (Player ent : affectedPlayers) {
                ((CraftPlayer) ent).getHandle().playerConnection.sendPacket(entityEquipmentPacket);//send affected players the packet
            }

            setCooldown(assassinConfig.smokeBombCooldown);
            new EndInvisibilityTask(e.getPlayer(), affectedPlayers).runTaskLater(getPlugin(), assassinConfig.smokeBombDuration * 20L);
        }
    }
}
