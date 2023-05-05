package com.pixelsandmagic.dodgeball;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Giovanni on 5/4/2023
 */
public class DodgeballPlugin extends JavaPlugin {

    private DodgeballGame game;

    @Override
    public void onEnable() {
        this.game = new DodgeballGame(this);
        this.game.start();
    }

    @Override
    public void onDisable() {
        this.game.stop();
    }
}
