package com.pixelsandmagic.dodgeball.modules.entity;

import com.pixelsandmagic.dodgeball.DodgeballGame;
import com.pixelsandmagic.dodgeball.core.module.Module;
import com.pixelsandmagic.dodgeball.modules.entity.listener.EntitySpawnListener;

/**
 * Created by Giovanni on 5/4/2023
 */
public class EntityModule extends Module {

    public EntityModule(DodgeballGame game) {
        super(game);
    }

    @Override
    public void onEnable() {
        registerListener(new EntitySpawnListener(this));
    }

    @Override
    public void onDisable() {

    }


    @Override
    public String getName() {
        return "entities";
    }

    @Override
    public boolean usesConfiguration() {
        return false;
    }
}
