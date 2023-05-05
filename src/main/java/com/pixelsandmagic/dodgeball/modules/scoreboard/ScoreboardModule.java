package com.pixelsandmagic.dodgeball.modules.scoreboard;

import com.pixelsandmagic.dodgeball.DodgeballGame;
import com.pixelsandmagic.dodgeball.core.module.Module;
import com.pixelsandmagic.dodgeball.modules.match.Match;
import com.pixelsandmagic.dodgeball.modules.match.MatchModule;
import com.pixelsandmagic.dodgeball.modules.player.DodgeballPlayer;
import com.pixelsandmagic.dodgeball.modules.player.PlayerModule;
import com.pixelsandmagic.dodgeball.modules.scoreboard.listener.JoinQuitListener;
import com.pixelsandmagic.dodgeball.modules.team.Team;
import me.catcoder.sidebar.ProtocolSidebar;
import me.catcoder.sidebar.Sidebar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Created by Giovanni on 5/4/2023
 */
public class ScoreboardModule extends Module {

    private Sidebar<Component> MAIN_WAITING_SIDEBAR;
    private Sidebar<Component> MAIN_MATCH_SIDEBAR;

    public ScoreboardModule(DodgeballGame game) {
        super(game);
    }

    public static Component component(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    @Override
    public void onEnable() {
        registerListener(new JoinQuitListener(this));

        this.MAIN_WAITING_SIDEBAR = ProtocolSidebar.newAdventureSidebar(component("&b&lDODGEBALL"), getGame().getPlugin());
        this.MAIN_WAITING_SIDEBAR.addBlankLine();
        this.MAIN_WAITING_SIDEBAR.addLine(component("&6▾ State"));
        this.MAIN_WAITING_SIDEBAR.addUpdatableLine(() -> {
            MatchModule matchModule = accessModule(MatchModule.class);
            if (matchModule.getActiveMatch() != null) {
                Match match = matchModule.getActiveMatch();
                return component("   " + match.getState().getText());
            }
            return component("   &cWaiting for players..");
        });
        this.MAIN_WAITING_SIDEBAR.updateLinesPeriodically(0, 10);

        this.MAIN_MATCH_SIDEBAR = ProtocolSidebar.newAdventureSidebar(component("&b&lDODGEBALL"), getGame().getPlugin());
        this.MAIN_MATCH_SIDEBAR.addBlankLine();
        this.MAIN_MATCH_SIDEBAR.addLine(component("&6▾ Team"));
        this.MAIN_MATCH_SIDEBAR.addUpdatableLine(player -> {
            PlayerModule playerModule = accessModule(PlayerModule.class);
            DodgeballPlayer dodgeballPlayer = playerModule.getPlayer(player.getUniqueId());

            Team team = dodgeballPlayer.getTeam();
            return component("   " + team.getName());
        });
        this.MAIN_MATCH_SIDEBAR.addBlankLine();
        this.MAIN_MATCH_SIDEBAR.addLine(component("&6▾ Team Score"));
        this.MAIN_MATCH_SIDEBAR.addUpdatableLine(player -> {
            PlayerModule playerModule = accessModule(PlayerModule.class);
            DodgeballPlayer dodgeballPlayer = playerModule.getPlayer(player.getUniqueId());

            int score = dodgeballPlayer.getTeam().getTotalScore();
            if (score <= 0) {
                return component("   &c" + score);
            }
            return component("   &a" + score);
        });
        this.MAIN_MATCH_SIDEBAR.addBlankLine();
        this.MAIN_MATCH_SIDEBAR.addLine(component("&6▾ Your Score"));
        this.MAIN_MATCH_SIDEBAR.addUpdatableLine(player -> {
            PlayerModule playerModule = accessModule(PlayerModule.class);
            DodgeballPlayer dodgeballPlayer = playerModule.getPlayer(player.getUniqueId());
            int score = dodgeballPlayer.getMatchScore();
            if (score <= 0) {
                return component("   &c" + score);
            }
            return component("   &a" + score);
        });
        this.MAIN_MATCH_SIDEBAR.updateLinesPeriodically(0, 10);
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
        return "scoreboard";
    }

    public void applyDefaultSidebar(DodgeballPlayer player) {
        player.setSidebar(MAIN_WAITING_SIDEBAR);
    }

    public void applyMatchSidebar(DodgeballPlayer player) {
        player.setSidebar(MAIN_MATCH_SIDEBAR);
    }
}
