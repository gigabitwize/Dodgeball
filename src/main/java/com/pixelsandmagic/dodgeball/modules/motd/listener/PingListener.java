package com.pixelsandmagic.dodgeball.modules.motd.listener;

import com.pixelsandmagic.dodgeball.modules.motd.MOTDModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

/**
 * Created by Giovanni on 5/5/2023
 */
public class PingListener implements Listener {

    private final MOTDModule module;

    public PingListener(MOTDModule module) {
        this.module = module;
    }

    @EventHandler
    public void onPing(ServerListPingEvent event) {
        event.setMaxPlayers(20);
        event.setMotd(module.constructMOTD());
    }
}
