package com.pixelsandmagic.dodgeball.modules.doublejump;

import com.pixelsandmagic.dodgeball.DodgeballGame;
import com.pixelsandmagic.dodgeball.core.module.Module;
import com.pixelsandmagic.dodgeball.modules.doublejump.listener.JumpListener;
import com.pixelsandmagic.dodgeball.modules.match.MatchModule;
import com.pixelsandmagic.dodgeball.modules.player.DodgeballPlayer;
import com.pixelsandmagic.dodgeball.util.Countdown;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

/**
 * Created by Giovanni on 5/5/2023
 */
public class DoubleJumpModule extends Module {

    private final JumpListener listener;

    public DoubleJumpModule(DodgeballGame game) {
        super(game);
        this.listener = new JumpListener(this);
    }

    public void executeDoubleJump(DodgeballPlayer player) {
        if (!isEnabled()) return;
        if (player.getPlayer().getGameMode() != GameMode.SURVIVAL) return;

        player.getPlayer().setAllowFlight(false);

        Vector impulseDirection = player.getPlayer().getLocation().getDirection();

        player.getPlayer().setFallDistance(0);
        impulseDirection.setY(0.75 + Math.abs(impulseDirection.getY()) * 0.5);
        impulseDirection.multiply(1.0f);
        player.getPlayer().setVelocity(impulseDirection);
        player.playSound(Sound.ENTITY_ZOMBIE_INFECT, 1F, 1.4F);
        player.getPlayer().setFallDistance(0);

        new Countdown(getGame().getPlugin(), 0, 4)
                .run(doNothing -> {

                }, () -> {
                    if (accessModule(MatchModule.class).hasActiveMatch())
                        player.getPlayer().setAllowFlight(true);
                });
    }

    @Override
    public void onEnable() {
        registerListener(listener);
    }

    @Override
    public void onDisable() {
        this.listener.unregister();
    }

    @Override
    public boolean usesConfiguration() {
        return false;
    }

    @Override
    public String getName() {
        return "doublejump";
    }
}
