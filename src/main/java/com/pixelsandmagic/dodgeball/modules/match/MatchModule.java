package com.pixelsandmagic.dodgeball.modules.match;

import com.pixelsandmagic.dodgeball.DodgeballGame;
import com.pixelsandmagic.dodgeball.core.module.Module;
import com.pixelsandmagic.dodgeball.core.module.ModuleConfiguration;
import com.pixelsandmagic.dodgeball.modules.ball.BallModule;
import com.pixelsandmagic.dodgeball.modules.bossbar.BossBarModule;
import com.pixelsandmagic.dodgeball.modules.doublejump.DoubleJumpModule;
import com.pixelsandmagic.dodgeball.modules.player.DodgeballPlayer;
import com.pixelsandmagic.dodgeball.modules.player.PlayerModule;
import com.pixelsandmagic.dodgeball.modules.scoreboard.ScoreboardModule;
import com.pixelsandmagic.dodgeball.modules.team.TeamModule;
import com.pixelsandmagic.dodgeball.util.Countdown;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Created by Giovanni on 5/4/2023
 */
public class MatchModule extends Module {

    private Match match;
    private boolean matchSchedulerAvailable;

    public MatchModule(DodgeballGame game) {
        super(game);
    }

    public void startMatch(boolean overrideCurrent) {
        long duration = 120;

        Optional<ModuleConfiguration> configurationOptional = getConfig();
        if (configurationOptional.isPresent())
            duration = configurationOptional.get().getLong("match.duration");

        if (match == null) {
            this.match = new Match(this, 50, duration);
            match.prepare();
            return;
        }
        if (hasActiveMatch() && overrideCurrent) {
            this.match = new Match(this, 50, duration);
            match.prepare();
        }
    }

    public void endMatch() {
        if (!hasActiveMatch()) return;
        match.signalModuleEnd();

        for (Entity entity : getGame().getWorld().asBukkitWorld().getEntities()) {
            if (entity instanceof Player) continue;
            entity.remove();
        }

        getGame().getWorld().getLobby().getFloor().reset();

        accessModule(BossBarModule.class).reset();
        accessModule(TeamModule.class).reset();
        accessModule(BallModule.class).disable();
        accessModule(DoubleJumpModule.class).disable();

        ScoreboardModule scoreboardModule = accessModule(ScoreboardModule.class);

        for (DodgeballPlayer onlinePlayer : accessModule(PlayerModule.class).getOnlinePlayers()) {
            onlinePlayer.resetTeam();
            onlinePlayer.getPlayer().getInventory().clear();
            onlinePlayer.teleport(getGame().getWorld().getSpawn());
            scoreboardModule.applyDefaultSidebar(onlinePlayer);
        }
        this.matchSchedulerAvailable = false;
        this.match = null;

        // Re-enable match scheduler after 10 seconds
        new Countdown(getGame().getPlugin(), 0L, 10L)
                .run(doNothing -> {

                }, () -> this.matchSchedulerAvailable = true);
    }

    public boolean hasActiveMatch() {
        return match != null && match.getState() != MatchState.UNDEFINED;
    }

    public Match getActiveMatch() {
        return match;
    }

    @Override
    public void onEnable() {
        this.matchSchedulerAvailable = true;

        PlayerModule playerModule = accessModule(PlayerModule.class);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(getGame().getPlugin(), () -> {
            if (!matchSchedulerAvailable) return;
            if (playerModule.enoughPlayers()) {
                if (!hasActiveMatch()) startMatch(false);
            }
        }, 0L, 10L);
    }

    @Override
    public void onDisable() {
        accessModule(BallModule.class).disable();
    }

    @Override
    public boolean usesConfiguration() {
        return true;
    }

    @Override
    public String getName() {
        return "match";
    }
}
