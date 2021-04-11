package me.xepos.rpg.tasks;

import net.minecraft.server.v1_16_R3.DamageSource;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class OverheatTask extends BukkitRunnable {

    private final LivingEntity target;

    public OverheatTask(LivingEntity target) {
        this.target = target;
    }

    @Override
    public void run() {

        double armorValue = target.getAttribute(Attribute.GENERIC_ARMOR).getValue();

        ArrayList<ItemStack> armor = new ArrayList<ItemStack>() {{
            if (target.getEquipment() != null) {
                add(target.getEquipment().getHelmet());
                add(target.getEquipment().getChestplate());
                add(target.getEquipment().getLeggings());
                add(target.getEquipment().getBoots());
            }
        }};


        int enchantLevel = 0;
        for (ItemStack armorPiece : armor) {
            if (armorPiece != null && armorPiece.containsEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL)) {
                enchantLevel += armorPiece.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
            }
        }
        target.sendMessage("Armor value: " + armorValue);
        target.sendMessage("Protection Level: " + enchantLevel);

        double dmg = 5;

        if (target.getLocation().getBlock().getType() == Material.WATER) {
            dmg = (5 + armorValue / 2) * 1 / (1 - enchantLevel * 0.04);
            target.sendMessage("Final damage: " + dmg);
            ((CraftLivingEntity) target).getHandle().damageEntity(DamageSource.FIRE, (float) dmg);
        } else {
            ((CraftLivingEntity) target).getHandle().damageEntity(DamageSource.FIRE, (float) dmg);
            target.sendMessage("Overheat damage was reduced by the water.");
        }
    }
}
