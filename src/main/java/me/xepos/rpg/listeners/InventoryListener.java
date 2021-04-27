package me.xepos.rpg.listeners;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.events.XRPGClassChangedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.Set;

public class InventoryListener implements Listener {

    private final XRPG plugin;

    public InventoryListener(XRPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent e)
    {
        if(e.getView().getTitle().equalsIgnoreCase("Pick A Class")) {
            if (e.getCurrentItem() == null)
                return;

            Set<NamespacedKey> keys = e.getCurrentItem().getItemMeta().getPersistentDataContainer().getKeys();
            String classId = null;
            String classDisplayName = e.getCurrentItem().getItemMeta().getDisplayName();
            for (NamespacedKey key : keys) {
                if (key.getNamespace().equals("classId")) {
                    classId = key.getKey();
                    break;
                }
            }
            if (classId == null) return;

            Player player = (Player) e.getWhoClicked();
            XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(player);

            if (xrpgPlayer.getFreeChangeTickets() <= 0) {
                player.sendMessage(ChatColor.RED + "You don't have enough tickets!");
                e.setCancelled(true);
                return;
            }

            if (xrpgPlayer.getClassId().equals(classId)) {
                player.sendMessage(ChatColor.RED + "Can't change to this class, it's already your current class!");
                e.setCancelled(true);
                return;
            }

            XRPGClassChangedEvent event = new XRPGClassChangedEvent(player, xrpgPlayer.getClassId(), xrpgPlayer.getClassDisplayName(), classId, classDisplayName);
            Bukkit.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                Bukkit.broadcastMessage(xrpgPlayer.getPlayer() + "changed their class from " + xrpgPlayer.getClassDisplayName() + " to " + classDisplayName + "!");
                xrpgPlayer.changeClass(classId, classDisplayName);

            }





/*            XRPGClass oldClass = xrpgPlayer.getPlayerClass();
            try {
                Class<?> clazz = Class.forName("me.xepos.rpg.classes." + className);
                Constructor<?> constructor = clazz.getConstructor(XRPG.class);
                Object instance = constructor.newInstance(plugin);
                xrpgPlayer.setPlayerClass((XRPGClass) instance);

                //Creating event
                XRPGClassChangedEvent event = new XRPGClassChangedEvent(player, oldClass, xrpgPlayer.getPlayerClass());
                Bukkit.getServer().getPluginManager().callEvent(event);
                //Checking if change ticket needs to be reduced
                if (!oldClass.toString().equalsIgnoreCase(xrpgPlayer.getPlayerClass().toString()) && !event.isCancelled()) {
                    //TODO: Save XRPGPlayer after changing class, rather than just saving it on logout.
                    xrpgPlayer.setFreeChangeTickets(xrpgPlayer.getFreeChangeTickets() - 1);
                    player.sendMessage("You are now " + xrpgPlayer.getPlayerClass().toString() + "!");
                }
            }catch (ClassNotFoundException cnfException)
            {
                player.sendMessage("You have " + xrpgPlayer.getFreeChangeTickets() + " tickets.");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            e.setCancelled(true);*/
        }
    }

    @EventHandler
    public void onItemDrag(final InventoryDragEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase("Pick A Class")) {
            e.setCancelled(true);
        }
    }
}
