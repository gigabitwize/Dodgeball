package com.pixelsandmagic.dodgeball.util;

import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Giovanni on 5/4/2023
 */
public class LocationUtil {

    public static ArrayList<UUID> getNearbyPlayers(Location origin, int radius) {
        ArrayList<UUID> players = Lists.newArrayList();
        for (Entity entity : arrayEntitiesNearby(origin, Player.class, radius)) {
            players.add(entity.getUniqueId());
        }
        return players;
    }

    public static <E extends Entity> Entity[] arrayEntitiesNearby(Location origin, Class<E> entityClass, int par2, int par3, int par4) {
        List<Entity> entityList = Lists.newArrayList();

        origin.getWorld().getNearbyEntities(origin, par2, par3, par4).stream().filter(entityClass::isInstance).forEach(entityList::add);
        Entity[] entities = new Entity[entityList.size()];
        for (int i = 0; i < entities.length; i++) {
            entities[i] = entityList.get(i);
        }
        return entities;
    }

    public static <E extends Entity> Entity[] arrayEntitiesNearby(Location origin, Class<E> entityClass, int par2) {
        return arrayEntitiesNearby(origin, entityClass, par2, par2, par2);
    }
}
