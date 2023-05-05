package com.pixelsandmagic.dodgeball.core.world;

import com.pixelsandmagic.dodgeball.modules.team.Team;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.ArrayList;

/**
 * Created by Giovanni on 5/4/2023
 */
public class DodgeballLobby {

    private final DodgeballWorld world;
    private final Location origin;

    private Floor floor;

    public DodgeballLobby(DodgeballWorld world, Location origin) {
        this.world = world;
        this.origin = origin;
    }

    protected void place() {
        this.floor = new Floor(origin);
        this.floor.generate();
    }

    public Floor getFloor() {
        return floor;
    }

    public class Floor {

        private final Location origin;

        private ArrayList<Vector> teamA, teamB;

        public Floor(Location origin) {
            this.origin = origin;
        }

        public void generate() {
            this.teamA = new ArrayList<>();
            this.teamB = new ArrayList<>();
            for (int radius = 0; radius < 12; radius++) {
                for (int c = 0; c < 360; c++) {
                    double angle = c * ((Math.PI * 2) / 360);

                    double x = origin.getX() + ((radius + 1) * Math.cos(angle));
                    double z = origin.getZ() + ((radius + 1) * Math.sin(angle));
                    Vector point = new Vector(x, origin.getY(), z);
                    origin.getWorld().getBlockAt(point.getBlockX(), point.getBlockY(), point.getBlockZ()).setType(Material.WHITE_WOOL);

                    if (c < 180) {
                        teamA.add(point);
                        continue;
                    }
                    teamB.add(point);
                }
            }
        }

        public void reset() {
            for (Vector vector : teamA) {
                Location location = vector.toLocation(origin.getWorld());
                origin.getWorld().getBlockAt(location).setType(Material.WHITE_WOOL);
            }

            for (Vector vector : teamB) {
                Location location = vector.toLocation(origin.getWorld());
                origin.getWorld().getBlockAt(location).setType(Material.WHITE_WOOL);
            }
        }

        @SuppressWarnings("all")
        public void colorize(Team.ColorSet setA, Team.ColorSet setB) {
            for (Vector vector : teamA) {
                Location location = vector.toLocation(origin.getWorld());
                origin.getWorld().getBlockAt(location).setType(setA.getBlockMaterial());
            }

            for (Vector vector : teamB) {
                Location location = vector.toLocation(origin.getWorld());
                origin.getWorld().getBlockAt(location).setType(setB.getBlockMaterial());
            }
        }


        public ArrayList<Vector> getTeamA() {
            return teamA;
        }

        public ArrayList<Vector> getTeamB() {
            return teamB;
        }
    }
}
