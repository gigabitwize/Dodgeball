package com.pixelsandmagic.dodgeball.modules.match;

import com.pixelsandmagic.dodgeball.modules.ball.BallModule;
import com.pixelsandmagic.dodgeball.modules.bossbar.BossBarModule;
import com.pixelsandmagic.dodgeball.modules.doublejump.DoubleJumpModule;
import com.pixelsandmagic.dodgeball.modules.player.DodgeballPlayer;
import com.pixelsandmagic.dodgeball.modules.player.PlayerModule;
import com.pixelsandmagic.dodgeball.modules.scoreboard.ScoreboardModule;
import com.pixelsandmagic.dodgeball.modules.team.Team;
import com.pixelsandmagic.dodgeball.modules.team.TeamModule;
import com.pixelsandmagic.dodgeball.util.Countdown;
import net.minecraft.util.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Giovanni on 5/4/2023
 */
public class Match {

    private final MatchModule module;
    private final PlayerModule playerModule;
    private final int scoreGoal;
    private final long matchDuration;

    private MatchState matchState = MatchState.UNDEFINED;
    private int tickingTask;
    private long currentTick;
    private Countdown startCountdown;

    public Match(MatchModule module, int scoreGoal, long matchDuration) {
        this.module = module;
        this.playerModule = module.accessModule(PlayerModule.class);

        this.scoreGoal = scoreGoal;
        this.matchDuration = matchDuration;
    }

    /**
     * Preparation for teams etc.
     */
    protected void prepare() {
        this.matchState = MatchState.PREPARING;

        module.getConfig().ifPresentOrElse(config -> {
            for (DodgeballPlayer onlinePlayer : playerModule.getOnlinePlayers()) {
                onlinePlayer.clearChat();
                onlinePlayer.sendMessage(config.getString("match.messages.preparing"));
            }
        }, () -> {
            for (DodgeballPlayer onlinePlayer : playerModule.getOnlinePlayers()) {
                onlinePlayer.clearChat();
                onlinePlayer.sendMessage("&9Preparing..");
            }
        });

        TeamModule teamModule = module.accessModule(TeamModule.class);

        Tuple<Team, Team> teams = teamModule.createTeams();
        Team teamA = teams.a();
        Team teamB = teams.b();

        module.getGame().getWorld().getLobby().getFloor().colorize(teamA.getColorSet(), teamB.getColorSet());
        ArrayList<Vector> aLocations = module.getGame().getWorld().getLobby().getFloor().getTeamA();
        for (DodgeballPlayer member : teamA.getMembers()) {
            Vector lobbySpawn = aLocations.get(ThreadLocalRandom.current().nextInt(aLocations.size()));
            member.teleport(lobbySpawn.clone().add(new Vector(0, 3, 0)));

            member.playSound(Sound.ENTITY_FOX_TELEPORT, 10F, 0.5F);
        }

        ArrayList<Vector> bLocations = module.getGame().getWorld().getLobby().getFloor().getTeamB();
        for (DodgeballPlayer member : teamB.getMembers()) {
            Vector lobbySpawn = bLocations.get(ThreadLocalRandom.current().nextInt(bLocations.size()));
            member.teleport(lobbySpawn.clone().add(new Vector(0, 3, 0)));

            member.playSound(Sound.ENTITY_FOX_TELEPORT, 10F, 0.5F);
        }
        handleStart();
    }

    private void handleStart() {
        for (DodgeballPlayer onlinePlayer : playerModule.getOnlinePlayers()) {
            onlinePlayer.sendMessage("&aThe match is starting in &e&l10 &aseconds!");
            onlinePlayer.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 10F, 1F);
        }
        startCountdown = new Countdown(module.getGame().getPlugin(), 0, 10);
        startCountdown.run(tick -> {
            if (tick == 5) {
                for (DodgeballPlayer onlinePlayer : playerModule.getOnlinePlayers()) {
                    Team team = onlinePlayer.getTeam();
                    // Lobby & Arena are in the same world
                    onlinePlayer.teleport(team.randomLocation(onlinePlayer.getPlayer().getWorld()));
                }
            }

            if (tick <= 5 && tick > 0) {
                for (DodgeballPlayer onlinePlayer : playerModule.getOnlinePlayers()) {
                    onlinePlayer.sendMessage("&a...&l" + tick);
                    onlinePlayer.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 10F, 1F);
                }
            }
        }, () -> {
            this.matchState = MatchState.STARTED;
            BallModule ballModule = module.accessModule(BallModule.class);
            ballModule.enable();
            ballModule.giveBalls();

            module.accessModule(DoubleJumpModule.class).enable();

            ScoreboardModule scoreboardModule = module.accessModule(ScoreboardModule.class);
            module.accessModule(BossBarModule.class).tick(matchDuration, 0);

            for (DodgeballPlayer onlinePlayer : playerModule.getOnlinePlayers()) {
                onlinePlayer.clearChat();
                onlinePlayer.sendMessage("&eThe match has begun.. &6&lFIGHT!");
                onlinePlayer.playSound(Sound.EVENT_RAID_HORN, 10F, 1F);
                onlinePlayer.getPlayer().setAllowFlight(true);

                scoreboardModule.applyMatchSidebar(onlinePlayer);
            }

            handleFinish();
        });
    }

    private void handleFinish() {
        // Finish is based on two conditions, if either one is met then the match ends.
        // Won't use Countdown here, but a repeating task that manages itself.
        TeamModule teamModule = module.accessModule(TeamModule.class);
        BossBarModule bossBarModule = module.accessModule(BossBarModule.class);
        tickingTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(module.getGame().getPlugin(), () -> {
            currentTick++;
            bossBarModule.tick(matchDuration, currentTick);
            if (currentTick >= matchDuration) {
                Team bestTeam = teamModule.getHighestScoringTeam();
                if (bestTeam == null) {
                    // DRAW
                } else {
                    handleWin(bestTeam);
                }
                return;
            }

            Team teamA = teamModule.getTeamA();
            Team teamB = teamModule.getTeamB();

            if (!teamA.hasLivingMemebers()) {
                handleWin(teamB);
            } else if (!teamB.hasLivingMemebers()) {
                handleWin(teamA);
            }
        }, 0L, 20L);
    }

    private void handleWin(Team team) {
        matchState = MatchState.FINALIZING;
        Bukkit.getScheduler().cancelTask(tickingTask);

        for (DodgeballPlayer onlinePlayer : playerModule.getOnlinePlayers()) {
            onlinePlayer.getPlayer().setAllowFlight(false);
            onlinePlayer.clearChat();
            onlinePlayer.sendMessage(team.getName() + " &ahas won the match!");
        }
        module.getConfig().ifPresent(config -> {
            for (String command : config.getStringList("match.victory-commands")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        });

        module.endMatch();
    }

    protected void signalModuleEnd() {
        startCountdown.interrupt();
        startCountdown = null;
        Bukkit.getScheduler().cancelTask(tickingTask);
    }

    public MatchState getState() {
        return matchState;
    }

}
