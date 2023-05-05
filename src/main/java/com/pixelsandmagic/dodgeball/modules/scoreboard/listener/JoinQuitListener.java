package com.pixelsandmagic.dodgeball.modules.scoreboard.listener;

import com.pixelsandmagic.dodgeball.modules.match.MatchModule;
import com.pixelsandmagic.dodgeball.modules.player.DodgeballPlayer;
import com.pixelsandmagic.dodgeball.modules.player.PlayerModule;
import com.pixelsandmagic.dodgeball.modules.player.event.DodgeballPlayerJoinEvent;
import com.pixelsandmagic.dodgeball.modules.player.event.DodgeballPlayerQuitEvent;
import com.pixelsandmagic.dodgeball.modules.scoreboard.ScoreboardModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Giovanni on 5/4/2023
 */
public class JoinQuitListener implements Listener {

    private final ScoreboardModule module;

    public JoinQuitListener(ScoreboardModule module) {
        this.module = module;
    }

    @EventHandler
    public void onJoin(DodgeballPlayerJoinEvent event) {
        DodgeballPlayer player = event.getPlayer();
        module.applyDefaultSidebar(player);
    }

    @EventHandler
    public void onQuit(DodgeballPlayerQuitEvent event) {
        DodgeballPlayer player = event.getPlayer();
        player.removeSidebar();

        if (module.accessModule(PlayerModule.class).getOnlinePlayers().size() < 2) {
            module.accessModule(MatchModule.class).endMatch();
        }
    }
}
