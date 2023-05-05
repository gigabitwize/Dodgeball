package com.pixelsandmagic.dodgeball.modules.ball.impl;

import com.pixelsandmagic.dodgeball.modules.ball.Ball;
import com.pixelsandmagic.dodgeball.modules.player.DodgeballPlayer;
import com.pixelsandmagic.dodgeball.modules.player.PlayerModule;
import com.pixelsandmagic.dodgeball.modules.team.Team;
import com.pixelsandmagic.dodgeball.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Giovanni on 5/4/2023
 */
public class ExplosiveBall extends Ball {

    public ExplosiveBall(DodgeballPlayer thrower) {
        super(thrower);
    }

    @Override
    public void onTravel() {
        Location location = getEntity().getLocation();
        location.getWorld().spawnParticle(Particle.FLAME, location, 2, 0, 0, 0, 0);
        location.getWorld().playSound(location, Sound.ITEM_FIRECHARGE_USE, 10F, 2F);
    }

    @Override
    public void onFire() {

    }

    @Override
    public void onHit(DodgeballPlayer target) {
        onLand(target.getPlayer().getLocation());
    }

    @Override
    public void onLand(Location location) {
        PlayerModule playerModule = getModule().accessModule(PlayerModule.class);

        location.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, location, 1, 0, 0, 0, 0);
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 10F, 1F);

        Team throwerTeam = getThrower().getTeam();

        ArrayList<UUID> nearbyPlayers = LocationUtil.getNearbyPlayers(location, 3);
        if (nearbyPlayers.isEmpty()) return;

        for (UUID nearbyPlayer : nearbyPlayers) {
            DodgeballPlayer player = playerModule.getPlayer(nearbyPlayer);
            if (nearbyPlayer.equals(getThrower().getUniqueId())) continue;
            if (throwerTeam.getMembers().contains(player)) continue;

            player.subtractScore(1, true);
            player.kill(getThrower());
        }
        getThrower().addScore(nearbyPlayers.size(), true);
    }
}
