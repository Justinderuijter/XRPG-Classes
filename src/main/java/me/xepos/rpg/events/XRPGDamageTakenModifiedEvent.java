package me.xepos.rpg.events;

import me.xepos.rpg.Utils;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.enums.DamageTakenSource;
import me.xepos.rpg.enums.MultiplierOperation;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class XRPGDamageTakenModifiedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final XRPGPlayer xrpgPlayer;
    private final DamageTakenSource source;
    private MultiplierOperation operation;
    private double amount;

    public XRPGDamageTakenModifiedEvent(Player player, MultiplierOperation operation, DamageTakenSource source, double amount){
        this.xrpgPlayer = Utils.GetRPG(player);
        this.source = source;
        this.operation = operation;
        this.amount = amount;
    }

    public MultiplierOperation getOperation() {
        return operation;
    }

    public void setOperation(MultiplierOperation operation) {
        this.operation = operation;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public XRPGPlayer getXRPGPlayer() {
        return xrpgPlayer;
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
