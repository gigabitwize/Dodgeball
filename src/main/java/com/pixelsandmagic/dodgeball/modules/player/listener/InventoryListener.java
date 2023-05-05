package com.pixelsandmagic.dodgeball.modules.player.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Created by Giovanni on 5/4/2023
 */
public class InventoryListener implements Listener {

    @EventHandler
    public void onArmorClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        if (slot == 6) {
            event.setCancelled(true);
        }
    }
}
