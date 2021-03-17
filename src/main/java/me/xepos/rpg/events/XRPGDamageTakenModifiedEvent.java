package me.xepos.rpg.events;

import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.enums.DamageTakenSource;
import me.xepos.rpg.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class XRPGDamageTakenModifiedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final XRPGPlayer xrpgPlayer;
    private final XRPGPlayer targetPlayer;
    private final DamageTakenSource source;

    public XRPGDamageTakenModifiedEvent(Player sourcePlayer, Player targetPlayer, DamageTakenSource source) {
        this.xrpgPlayer = Utils.GetRPG(sourcePlayer);
        this.targetPlayer = Utils.GetRPG(targetPlayer);
        this.source = source;
    }


    public XRPGPlayer getSourceXRPGPlayer() {
        return xrpgPlayer;
    }

    public XRPGPlayer getTargetXRPGPlayer() {
        return targetPlayer;
    }

    public DamageTakenSource getSource() {
        return source;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }


}
