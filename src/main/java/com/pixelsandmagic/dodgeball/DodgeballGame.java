package com.pixelsandmagic.dodgeball;


import com.pixelsandmagic.dodgeball.core.module.Module;
import com.pixelsandmagic.dodgeball.core.world.DodgeballWorld;
import com.pixelsandmagic.dodgeball.modules.ball.BallModule;
import com.pixelsandmagic.dodgeball.modules.bossbar.BossBarModule;
import com.pixelsandmagic.dodgeball.modules.doublejump.DoubleJumpModule;
import com.pixelsandmagic.dodgeball.modules.match.MatchModule;
import com.pixelsandmagic.dodgeball.modules.motd.MOTDModule;
import com.pixelsandmagic.dodgeball.modules.player.PlayerModule;
import com.pixelsandmagic.dodgeball.modules.scoreboard.ScoreboardModule;
import com.pixelsandmagic.dodgeball.modules.team.TeamModule;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Giovanni on 5/4/2023
 */
public class DodgeballGame {

    private final DodgeballPlugin plugin;
    private final Set<Module> modules;

    private DodgeballWorld world;
    private File modulesDirectory;

    public DodgeballGame(DodgeballPlugin plugin) {
        this.plugin = plugin;
        this.modules = new HashSet<>();
    }

    public void start() {
        this.modulesDirectory = new File(getDataFolder(), "modules");
        if (!modulesDirectory.exists())
            modulesDirectory.mkdir();

        modules.add(new MatchModule(this));
        modules.add(new TeamModule(this));
        modules.add(new PlayerModule(this));
        modules.add(new ScoreboardModule(this));
        modules.add(new BossBarModule(this));
        modules.add(new MOTDModule(this));

        world = new DodgeballWorld(plugin, new File(plugin.getDataFolder(), "world_data"), "sd_dodgeball_instance");
        world.setup();

        modules.forEach(Module::enable);

        // Register after enable
        modules.add(new BallModule(this));
        modules.add(new DoubleJumpModule(this));
    }

    public void stop() {
        modules.forEach(Module::disable);
        modules.clear();

        world.delete();
    }

    public DodgeballPlugin getPlugin() {
        return plugin;
    }

    public File getDataFolder() {
        return getPlugin().getDataFolder();
    }

    public DodgeballWorld getWorld() {
        return world;
    }

    public File getModulesDirectory() {
        return modulesDirectory;
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> moduleClass) {
        for (Module module : modules) {
            if (module.getClass().equals(moduleClass))
                return (T) module;
        }
        return null;
    }
}
