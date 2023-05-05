package com.pixelsandmagic.dodgeball.core.module;

import com.pixelsandmagic.dodgeball.DodgeballGame;
import org.apache.commons.io.FileUtils;
import org.bukkit.event.Listener;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * Created by Giovanni on 5/4/2023
 */
public abstract class Module {

    private final DodgeballGame game;
    private boolean enabled;

    @Nullable
    private ModuleConfiguration configuration;

    public Module(DodgeballGame game) {
        this.game = game;
    }

    public final void enable() {
        if (!usesConfiguration()) {
            onEnable();
            this.enabled = true;
            return;
        }

        File configurationFile = new File(game.getModulesDirectory(), getName() + ".yml");
        if (!configurationFile.exists()) {
            InputStream arenaSchemStream = game.getPlugin().getResource(getName() + ".yml");
            if (arenaSchemStream != null) {
                try {
                    FileUtils.copyToFile(arenaSchemStream, configurationFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        this.configuration = new ModuleConfiguration(this, configurationFile);
        this.configuration.load();
        onEnable();
        this.enabled = true;
    }

    public final void disable() {
        onDisable();
        this.enabled = false;
    }

    public <T extends Module> T accessModule(Class<T> moduleClass) {
        return game.getModule(moduleClass);
    }

    public DodgeballGame getGame() {
        return game;
    }

    public Optional<ModuleConfiguration> getConfig() {
        return Optional.ofNullable(configuration);
    }

    protected void registerListener(Listener listener) {
        game.getPlugin().getServer().getPluginManager().registerEvents(listener, game.getPlugin());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public abstract boolean usesConfiguration();

    public abstract String getName();

    public abstract void onEnable();

    public abstract void onDisable();
}
