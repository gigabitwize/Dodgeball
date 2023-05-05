package com.pixelsandmagic.dodgeball.modules.entity.listener;

import com.pixelsandmagic.dodgeball.modules.entity.EntityModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Created by Giovanni on 5/4/2023
 */
public class EntitySpawnListener implements Listener {

    private final EntityModule module;

    public EntitySpawnListener(EntityModule module) {
        this.module = module;
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM)
            event.setCancelled(true);
    }
}
