package me.xepos.rpg.listeners;

import me.xepos.rpg.utils.Utils;
import me.xepos.rpg.enums.MultiplierOperation;
import me.xepos.rpg.events.XRPGClassChangedEvent;
import me.xepos.rpg.events.XRPGDamageTakenModifiedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class XRPGListener implements Listener {

    @SuppressWarnings("unchecked")
    @EventHandler
    public void onClassChange(XRPGClassChangedEvent e)
    {
        List<Player> onlinePlayers = (List<Player>) Bukkit.getServer().getOnlinePlayers();
        for (Player onlinePlayer : onlinePlayers) {
            onlinePlayer.sendMessage(e.getPlayer().getName() + " changed their class from " + e.getOldClass() + " to " + e.getNewClass());
        }
        Utils.GetRPG(e.getPlayer()).getPlayerClass().applyEffects(e.getPlayer());
    }

    @EventHandler
    public void onDTModifierChange(XRPGDamageTakenModifiedEvent e)
    {
        if (e.getOperation() == MultiplierOperation.ADDED){
            if(!e.getXRPGPlayer().dmgTakenMultipliers.containsKey(e.getSource()))
                e.getXRPGPlayer().dmgTakenMultipliers.put(e.getSource(), e.getAmount());
        }
        else {
            if(!e.getXRPGPlayer().dmgTakenMultipliers.containsKey(e.getSource()))
                e.getXRPGPlayer().dmgTakenMultipliers.remove(e.getSource());
        }
    }
}
