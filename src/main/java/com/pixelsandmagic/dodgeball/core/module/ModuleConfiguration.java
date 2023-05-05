package com.pixelsandmagic.dodgeball.core.module;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Giovanni on 5/4/2023
 */
public class ModuleConfiguration {

    private final Module module;
    private final File file;

    private YamlConfiguration configuration;

    public ModuleConfiguration(Module module, File file) {
        this.module = module;
        this.file = file;
    }

    protected void load() {
        if (!file.exists()) return;

        YamlConfiguration configuration = new YamlConfiguration();
        try {
            configuration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        this.configuration = configuration;
    }

    public YamlConfiguration get() {
        return configuration;
    }

    public String getReplacePlayer(String path, Player player) {
        return Placeholders.PLAYER.apply(getString(path), player);
    }

    public List<String> getStringList(String path) {
        return get().getStringList(path);
    }

    public String getString(String path) {
        return get().getString(path);
    }

    public int getInteger(String path) {
        return get().getInt(path);
    }

    public long getLong(String path) {
        return get().getLong(path);
    }

    abstract static class Placeholder<A> {

        public abstract String apply(String input, A replaceWith);
    }

    static class Placeholders {

        public static Placeholder<Player> PLAYER = new Placeholder<>() {
            @Override
            public String apply(String input, Player replaceWith) {
                return input.replaceAll("%player%", replaceWith.getName());
            }
        };
    }
}
