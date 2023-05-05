package com.pixelsandmagic.dodgeball.modules.team;

import com.pixelsandmagic.dodgeball.DodgeballGame;
import com.pixelsandmagic.dodgeball.core.module.Module;
import com.pixelsandmagic.dodgeball.core.world.DodgeballArena;
import com.pixelsandmagic.dodgeball.modules.player.DodgeballPlayer;
import com.pixelsandmagic.dodgeball.modules.player.PlayerModule;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Giovanni on 5/4/2023
 */
public class TeamModule extends Module {

    private Team teamA, teamB;

    public TeamModule(DodgeballGame game) {
        super(game);
    }

    public void reset() {
        this.teamA.getMembers().clear();
        this.teamB.getMembers().clear();

        this.teamA = null;
        this.teamB = null;
    }

    public Tuple<Team, Team> createTeams() {
        if (teamA != null && teamB != null) {
            return new Tuple<>(teamA, teamB);
        }

        PlayerModule playerModule = accessModule(PlayerModule.class);
        ArrayList<DodgeballPlayer> shuffled = new ArrayList<>(playerModule.getOnlinePlayers());
        Collections.shuffle(shuffled);

        // I should probably make this proper
        ArrayList<DodgeballPlayer> teamAMembers = new ArrayList<>();
        for (int i = 0; i < (shuffled.size() / 2); i++) {
            teamAMembers.add(shuffled.get(i));
        }

        ArrayList<DodgeballPlayer> teamBMembers = new ArrayList<>();
        for (DodgeballPlayer dodgeballPlayer : shuffled) {
            if (teamAMembers.contains(dodgeballPlayer)) continue;
            teamBMembers.add(dodgeballPlayer);
        }


        Tuple<Team.ColorSet, Team.ColorSet> colorSets = randomColorSets();
        this.teamA = new Team(colorSets.a(), DodgeballArena.TEAM_A_COORDINATES);
        for (DodgeballPlayer dodgeballPlayer : teamAMembers) {
            teamA.addMember(dodgeballPlayer);
            dodgeballPlayer.assignTeam(teamA);
        }

        this.teamB = new Team(colorSets.b(), DodgeballArena.TEAM_B_COORDINATES);
        for (DodgeballPlayer dodgeballPlayer : teamBMembers) {
            teamB.addMember(dodgeballPlayer);
            dodgeballPlayer.assignTeam(teamB);
        }

        return new Tuple<>(teamA, teamB);
    }

    public Tuple<Team.ColorSet, Team.ColorSet> randomColorSets() {
        Team.ColorSet teamA = randomColorSet(null);
        Team.ColorSet teamB = randomColorSet(teamA);
        return new Tuple<>(teamA, teamB);
    }

    private Team.ColorSet randomColorSet(Team.ColorSet exclude) {
        Team.ColorSet randomized = Team.ColorSet.values()[ThreadLocalRandom.current().nextInt(Team.ColorSet.values().length)];
        if (exclude == null) {
            return randomized;
        }
        if (randomized == exclude) {
            return randomColorSet(exclude);
        }
        return randomized;
    }

    public Team getTeamOf(DodgeballPlayer player) {
        for (DodgeballPlayer member : teamA.getMembers()) {
            if (member.equals(player)) return teamA;
        }
        for (DodgeballPlayer member : teamB.getMembers()) {
            if (member.equals(player)) return teamB;
        }
        return null;
    }

    public Team getHighestScoringTeam() {
        if (teamA.getLivingMembers() > teamB.getLivingMembers())
            return teamA;
        if (teamB.getLivingMembers() > teamA.getLivingMembers())
            return teamB;
        return null;
    }

    public Team getTeamA() {
        return teamA;
    }

    public Team getTeamB() {
        return teamB;
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean usesConfiguration() {
        return false;
    }

    @Override
    public String getName() {
        return "teams";
    }
}
