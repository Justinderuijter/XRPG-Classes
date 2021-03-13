package me.xepos.rpg.dependencies.protection;

import org.bukkit.Location;

public class DefaultProtectionManager implements IProtectionManager{

    protected DefaultProtectionManager(){ }

    @Override
    public boolean isLocationValid(Location sourceLocation, Location targetLocation) {
        return true;
    }
}
