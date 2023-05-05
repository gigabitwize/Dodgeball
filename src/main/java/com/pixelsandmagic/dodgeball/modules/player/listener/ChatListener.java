package com.pixelsandmagic.dodgeball.modules.player.listener;

import com.pixelsandmagic.dodgeball.modules.player.DodgeballPlayer;
import com.pixelsandmagic.dodgeball.modules.player.PlayerModule;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Created by Giovanni on 5/4/2023
 */
public class ChatListener implements Listener {

    private final PlayerModule module;

    public ChatListener(PlayerModule module) {
        this.module = module;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        DodgeballPlayer sender = module.getPlayer(player.getUniqueId());

        ChatColor prefixColor = ChatColor.GRAY;

        if (sender.getTeam() != null) prefixColor = sender.getTeam().getColorSet().getChatColor();

        event.setFormat(prefixColor + player.getName() + ": " + event.getMessage());
    }
}
