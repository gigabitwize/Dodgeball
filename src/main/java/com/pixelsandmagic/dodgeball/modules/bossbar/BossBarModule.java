package com.pixelsandmagic.dodgeball.modules.bossbar;

import com.pixelsandmagic.dodgeball.DodgeballGame;
import com.pixelsandmagic.dodgeball.core.module.Module;
import com.pixelsandmagic.dodgeball.modules.player.DodgeballPlayer;
import com.pixelsandmagic.dodgeball.modules.player.PlayerModule;
import com.pixelsandmagic.dodgeball.util.ChatColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

/**
 * Created by Giovanni on 5/5/2023
 */
public class BossBarModule extends Module {

    private BossBar bossBar;

    public BossBarModule(DodgeballGame game) {
        super(game);
    }

    @Override
    public void onEnable() {
        this.bossBar = Bukkit.createBossBar("&e&lMATCH HAS BEGUN", BarColor.GREEN, BarStyle.SOLID);
        this.bossBar.setProgress(1);
    }

    @Override
    public void onDisable() {
        this.bossBar = null;
    }

    @Override
    public boolean usesConfiguration() {
        return false;
    }

    @Override
    public String getName() {
        return "bossbar";
    }

    public void tick(long matchDuration, long current) {
        if (!isEnabled()) return;

        double left = matchDuration - current;
        bossBar.setTitle(ChatColorUtil.color("&e&lTIME LEFT: &a" + (int) left + "s.."));
        bossBar.setProgress(left / matchDuration);

        PlayerModule playerModule = accessModule(PlayerModule.class);
        for (DodgeballPlayer onlinePlayer : playerModule.getOnlinePlayers()) {
            onlinePlayer.sendBossBar(bossBar);
        }
    }

    public void reset() {
        if (!isEnabled()) return;

        PlayerModule playerModule = accessModule(PlayerModule.class);
        for (DodgeballPlayer onlinePlayer : playerModule.getOnlinePlayers()) {
            onlinePlayer.removeBossBar();
        }
    }
}
