package com.pixelsandmagic.dodgeball.core.world;

import org.bukkit.Location;
import org.bukkit.util.Vector;


/**
 * Created by Giovanni on 5/4/2023
 */
public class DodgeballArena {

    public static final Vector TEAM_A_COORDINATES = new Vector(253.928, 40, 269.029);
    public static final Vector TEAM_B_COORDINATES = new Vector(253.928, 40, 289.029);

    private final DodgeballWorld world;
    private final Location origin;

    public DodgeballArena(DodgeballWorld world, Location origin) {
        this.world = world;
        this.origin = origin;
    }

    public DodgeballWorld getWorld() {
        return world;
    }

    public Location getOrigin() {
        return origin;
    }
}
