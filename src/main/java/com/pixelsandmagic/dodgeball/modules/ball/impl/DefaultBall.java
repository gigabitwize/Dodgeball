package com.pixelsandmagic.dodgeball.modules.ball.impl;

import com.pixelsandmagic.dodgeball.modules.ball.Ball;
import com.pixelsandmagic.dodgeball.modules.player.DodgeballPlayer;
import org.bukkit.Location;

/**
 * Created by Giovanni on 5/4/2023
 */
public class DefaultBall extends Ball {

    public DefaultBall(DodgeballPlayer thrower) {
        super(thrower);
    }

    @Override
    public void onFire() {

    }

    @Override
    public void onTravel() {

    }

    @Override
    public void onHit(DodgeballPlayer target) {
        getThrower().addScore(1, true);
        target.subtractScore(1, true);
        target.kill(getThrower());
    }

    @Override
    public void onLand(Location location) {

    }
}
