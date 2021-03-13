package me.xepos.rpg.dependencies.protection;

import org.bukkit.Location;

public interface IProtectionManager {

    boolean isLocationValid(Location sourceLocation, Location targetLocation);
}
