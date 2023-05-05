package com.pixelsandmagic.dodgeball.modules.player.listener;

import com.pixelsandmagic.dodgeball.modules.match.MatchModule;
import com.pixelsandmagic.dodgeball.modules.player.PlayerModule;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Giovanni on 5/4/2023
 */
public class JoinQuitListener implements Listener {

    private final PlayerModule module;

    public JoinQuitListener(PlayerModule module) {
        this.module = module;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");

        Player player = event.getPlayer();
        module.handleQuit(player);
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        MatchModule matchModule = module.accessModule(MatchModule.class);
        if (matchModule.hasActiveMatch())
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "A match is currently active, please join back later!");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");

        Player player = event.getPlayer();
        module.handleJoin(player);
    }
}
