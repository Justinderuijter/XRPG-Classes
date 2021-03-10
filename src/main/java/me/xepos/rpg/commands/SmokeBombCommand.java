package me.xepos.rpg.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SmokeBombCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings)
    {
        if (command.getName().equals("smoke"))
        {
            if (commandSender instanceof Player)
            {
                ItemStack itemStack = new ItemStack(Material.SNOWBALL);
                ItemMeta itemMeta = itemStack.getItemMeta();

                //Set itemMeta Name
                itemMeta.setDisplayName("Smoke Bomb");
                //Set Item Lore
                List<String> lore = new ArrayList<>();
                lore.add("Allows Assassins to go invisible!");
                itemMeta.setLore(lore);
                //apply meta to itemstack
                itemStack.setItemMeta(itemMeta);
                //give itemstack to player
                ((Player)commandSender).getInventory().addItem(itemStack);

                return true;
            }
        }

        return false;
    }
}
