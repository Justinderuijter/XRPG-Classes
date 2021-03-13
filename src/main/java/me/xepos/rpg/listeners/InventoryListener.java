package me.xepos.rpg.listeners;

import me.xepos.rpg.utils.Utils;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.XRPGClass;
import me.xepos.rpg.events.XRPGClassChangedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.lang.reflect.Constructor;

public class InventoryListener implements Listener {

    private final XRPG plugin;

    public InventoryListener(XRPG plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent e)
    {
        if(e.getView().getTitle().equalsIgnoreCase("Pick A Class"))
        {
            if (e.getCurrentItem() == null)
                return;

            String className = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
            Player player = (Player) e.getWhoClicked();
            XRPGPlayer xrpgPlayer = Utils.GetRPG(player);

            if (xrpgPlayer.getFreeChangeTickets() <= 0)
            {
                player.sendMessage(ChatColor.RED + "You don't have enough tickets!");
                e.setCancelled(true);
                return;
            }


            XRPGClass oldClass = xrpgPlayer.getPlayerClass();
            try {
                Class<?> clazz = Class.forName("me.xepos.rpg.classes." + className);
                Constructor<?> constructor = clazz.getConstructor(XRPG.class);
                Object instance = constructor.newInstance(plugin);
                xrpgPlayer.setPlayerClass((XRPGClass) instance);
                if (!oldClass.toString().equalsIgnoreCase(xrpgPlayer.getPlayerClass().toString()))
                {
                    xrpgPlayer.setFreeChangeTickets(xrpgPlayer.getFreeChangeTickets() - 1);
                    XRPGClassChangedEvent event = new XRPGClassChangedEvent(player, oldClass, xrpgPlayer.getPlayerClass());
                    Bukkit.getServer().getPluginManager().callEvent(event);
                    player.sendMessage("You are now "+ xrpgPlayer.getPlayerClass().toString() + "!");
                }
            }catch (ClassNotFoundException cnfException)
            {
                player.sendMessage("You have " + xrpgPlayer.getFreeChangeTickets() + " tickets.");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrag(final InventoryDragEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase("Pick A Class")) {
            e.setCancelled(true);
        }
    }
}
