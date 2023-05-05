package com.pixelsandmagic.dodgeball.modules.player;

import com.pixelsandmagic.dodgeball.DodgeballGame;
import com.pixelsandmagic.dodgeball.modules.team.Team;
import com.pixelsandmagic.dodgeball.util.ChatColorUtil;
import me.catcoder.sidebar.Sidebar;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.util.Vector;

import java.util.UUID;


/**
 * Created by Giovanni on 5/4/2023
 */
public class DodgeballPlayer {

    private final DodgeballGame game;
    private final Player player;

    private Sidebar<Component> sidebar;

    // Weakly referenced
    private Team team;
    private int matchScore;
    private BossBar bossBar;
    private boolean isDead;

    public DodgeballPlayer(DodgeballGame game, Player player) {
        this.game = game;
        this.player = player;
    }

    public void sendBossBar(BossBar bossBar) {
        if (this.bossBar != null && this.bossBar == bossBar) return;
        this.bossBar = bossBar;
        this.bossBar.addPlayer(getPlayer());
    }

    public void removeBossBar() {
        if (this.bossBar == null) return;
        this.bossBar.removePlayer(getPlayer());
        this.bossBar = null;
    }

    public void clearChat() {
        for (int i = 0; i < 50; i++) {
            player.sendMessage("");
        }
    }

    public void setSidebar(Sidebar<Component> sidebar) {
        removeSidebar();
        this.sidebar = sidebar;
        this.sidebar.addViewer(player);
    }

    public void removeSidebar() {
        if (this.sidebar != null)
            this.sidebar.removeViewer(player);
        this.sidebar = null;
    }

    public Snowball launchProjectile() {
        return getPlayer().launchProjectile(Snowball.class);
    }

    public Snowball launchProjectile(Vector force) {
        return getPlayer().launchProjectile(Snowball.class, force);
    }

    public void sendActionbarMessage(String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColorUtil.color(message)));
    }

    public void sendMessage(String message) {
        player.sendMessage(ChatColorUtil.color(message));
    }

    public void kick(String reason) {
        player.kickPlayer(reason);
    }

    public void playSound(Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public void resetTeam() {
        this.matchScore = 0;
        this.isDead = false;
        this.team = null;
        getPlayer().setGameMode(GameMode.SURVIVAL);
        getPlayer().setFoodLevel(20);
    }

    public void assignTeam(Team team) {
        this.team = team;
        getPlayer().getInventory().setChestplate(team.getChestplate());
    }

    public Team getTeam() {
        return team;
    }

    public void teleport(Vector vector) {
        player.teleport(vector.toLocation(game.getWorld().asBukkitWorld()));
    }

    public void teleport(Location location) {
        player.teleport(location);
    }

    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    public Player getPlayer() {
        return player;
    }

    public void addScore(int amount, boolean message) {
        this.matchScore += amount;
        if (message)
            sendActionbarMessage("&a&l+" + amount + "&a score");
    }

    public void subtractScore(int amount, boolean message) {
        this.matchScore -= amount;
        if (matchScore < 0) this.matchScore = 0;
        if (message)
            sendActionbarMessage("&c&l-" + amount + "&c score");
    }

    public void kill(DodgeballPlayer killer) {
        this.isDead = true;
        clearChat();
        sendMessage("&cYou were hit by &l" + killer.getPlayer().getName());
        playSound(Sound.ENTITY_VILLAGER_DEATH, 10F, 1.5F);
        getPlayer().getInventory().clear();
        getPlayer().setGameMode(GameMode.SPECTATOR);
    }

    public boolean isDead() {
        return isDead;
    }

    public int getMatchScore() {
        return matchScore;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DodgeballPlayer other)) return false;
        return other.getUniqueId().equals(getUniqueId());
    }
}
