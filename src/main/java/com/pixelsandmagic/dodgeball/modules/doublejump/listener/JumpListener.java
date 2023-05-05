package com.pixelsandmagic.dodgeball.modules.doublejump.listener;

import com.pixelsandmagic.dodgeball.modules.doublejump.DoubleJumpModule;
import com.pixelsandmagic.dodgeball.modules.player.DodgeballPlayer;
import com.pixelsandmagic.dodgeball.modules.player.PlayerModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

/**
 * Created by Giovanni on 5/5/2023
 */
public class JumpListener implements Listener {

    private final DoubleJumpModule module;

    public JumpListener(DoubleJumpModule module) {
        this.module = module;
    }

    public void unregister() {
        PlayerToggleFlightEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        PlayerModule playerModule = module.accessModule(PlayerModule.class);
        DodgeballPlayer player = playerModule.getPlayer(event.getPlayer().getUniqueId());

        if (event.isFlying()) {
            module.executeDoubleJump(player);
        }
    }
}
