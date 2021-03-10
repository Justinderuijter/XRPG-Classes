package me.xepos.rpg.events;

import me.xepos.rpg.classes.XRPGClass;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class XRPGClassChangedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final CommandSender commandSender;
    private final XRPGClass oldClass;
    private final XRPGClass newClass;

    public XRPGClassChangedEvent(Player player, XRPGClass oldClass, XRPGClass newClass)
    {
        this.player = player;
        this.commandSender = null;
        this.oldClass = oldClass;
        this.newClass = newClass;
    }

    public XRPGClassChangedEvent(CommandSender commandSender,Player player, XRPGClass oldClass, XRPGClass newClass)
    {
        this.player = player;
        this.commandSender = commandSender;
        this.oldClass = oldClass;
        this.newClass = newClass;
    }

    //Getters

    public Player getPlayer() {
        return this.player;
    }

    public CommandSender getCommandSender() {
        return this.commandSender;
    }

    public XRPGClass getOldClass() {
        return this.oldClass;
    }

    public XRPGClass getNewClass() {
        return this.newClass;
    }


    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}