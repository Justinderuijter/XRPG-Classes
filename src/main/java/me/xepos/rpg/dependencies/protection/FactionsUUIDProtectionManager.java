package me.xepos.rpg.dependencies.protection;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import org.bukkit.Location;

public class FactionsUUIDProtectionManager implements IProtectionManager{

    protected FactionsUUIDProtectionManager(){ }

    @SuppressWarnings("all")
    @Override
    public boolean isLocationValid(Location sourceLocation, Location targetLocation) {
        FLocation fSourceLocation = new FLocation(sourceLocation);
        Faction sourceFaction = Board.getInstance().getFactionAt(fSourceLocation);
        if (sourceFaction.isSafeZone() || sourceFaction.isPeaceful())
            return false;

        FLocation fTargetLocation = new FLocation(targetLocation);
        Faction targetFaction = Board.getInstance().getFactionAt(fTargetLocation);
        if (targetFaction.isSafeZone() || targetFaction.isPeaceful()) {
            return false;
        }

        return false;
    }
}
