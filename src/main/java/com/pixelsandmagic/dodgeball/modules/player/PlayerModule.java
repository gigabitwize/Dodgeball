package com.pixelsandmagic.dodgeball.modules.player;

import com.pixelsandmagic.dodgeball.DodgeballGame;
import com.pixelsandmagic.dodgeball.core.module.Module;
import com.pixelsandmagic.dodgeball.core.module.ModuleConfiguration;
import com.pixelsandmagic.dodgeball.modules.match.MatchModule;
import com.pixelsandmagic.dodgeball.modules.player.event.DodgeballPlayerJoinEvent;
import com.pixelsandmagic.dodgeball.modules.player.event.DodgeballPlayerQuitEvent;
import com.pixelsandmagic.dodgeball.modules.player.listener.*;
import com.pixelsandmagic.dodgeball.modules.team.Team;
import com.pixelsandmagic.dodgeball.modules.team.TeamModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Giovanni on 5/4/2023
 */
public class PlayerModule extends Module {

    private ConcurrentHashMap<UUID, DodgeballPlayer> onlinePlayers;

    public PlayerModule(DodgeballGame game) {
        super(game);
    }

    public void handleJoin(Player player) {
        player.getInventory().clear();
        DodgeballPlayer dodgeballPlayer = new DodgeballPlayer(getGame(), player);
        dodgeballPlayer.teleport(getGame().getWorld().getSpawn());

        onlinePlayers.put(player.getUniqueId(), dodgeballPlayer);

        getConfig().ifPresentOrElse(config -> {
            for (DodgeballPlayer onlinePlayer : getOnlinePlayers()) {
                onlinePlayer.sendMessage(config.getReplacePlayer("player.messages.join", player));
            }
        }, () -> {
            for (DodgeballPlayer onlinePlayer : getOnlinePlayers()) {
                onlinePlayer.sendMessage("&a&l" + player.getName() + " &ahas joined the game");
            }
        });
        Bukkit.getPluginManager().callEvent(new DodgeballPlayerJoinEvent(dodgeballPlayer));
    }

    public void handleQuit(Player player) {
        TeamModule teamModule = accessModule(TeamModule.class);
        DodgeballPlayer dodgeballPlayer = onlinePlayers.get(player.getUniqueId());

        Team team = dodgeballPlayer.getTeam();
        if (team != null) {
            team.removeMember(dodgeballPlayer);

            teamModule.getConfig().ifPresentOrElse(config -> {
                for (DodgeballPlayer member : team.getMembers()) {
                    member.sendMessage(config.getReplacePlayer("team.messages.member-quit", player));
                }
            }, () -> {
                for (DodgeballPlayer member : team.getMembers()) {
                    member.sendMessage("&c&l" + player.getName() + " &chas left the match");
                }
            });
        }
        onlinePlayers.remove(player.getUniqueId());
        Bukkit.getPluginManager().callEvent(new DodgeballPlayerQuitEvent(dodgeballPlayer));
    }

    @Override
    public void onEnable() {
        this.onlinePlayers = new ConcurrentHashMap<>();

        registerListener(new DamageListener(this));
        registerListener(new JoinQuitListener(this));
        registerListener(new ChatListener(this));
        registerListener(new InventoryListener());
        registerListener(new BlockListener());
    }

    @Override
    public void onDisable() {
        for (DodgeballPlayer onlinePlayer : getOnlinePlayers()) {
            onlinePlayer.kick("PlayerModule exit");
        }
        onlinePlayers.clear();
    }

    @Override
    public boolean usesConfiguration() {
        return true;
    }

    @Override
    public String getName() {
        return "players";
    }

    public DodgeballPlayer getPlayer(UUID uuid) {
        return onlinePlayers.get(uuid);
    }

    public Collection<DodgeballPlayer> getOnlinePlayers() {
        return onlinePlayers.values();
    }

    public boolean enoughPlayers() {
        MatchModule matchModule = accessModule(MatchModule.class);

        Optional<ModuleConfiguration> configuration = matchModule.getConfig();
        return configuration
                .map(moduleConfiguration -> onlinePlayers != null && onlinePlayers.size() >= moduleConfiguration.getInteger("match.minimum-players"))
                .orElseGet(() -> onlinePlayers != null && onlinePlayers.size() >= 2);
    }
}
