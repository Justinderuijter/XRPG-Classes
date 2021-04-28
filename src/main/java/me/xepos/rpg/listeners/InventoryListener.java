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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

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

            ItemMeta meta = e.getCurrentItem().getItemMeta();
            if (meta != null) {
                String classId = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "classId"), PersistentDataType.STRING);
                String classDisplayName = e.getCurrentItem().getItemMeta().getDisplayName();

                if (classId == null) {
                    e.setCancelled(true);
                    return;
                }

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
