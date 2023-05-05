package com.pixelsandmagic.dodgeball.modules.motd;

import com.pixelsandmagic.dodgeball.DodgeballGame;
import com.pixelsandmagic.dodgeball.core.module.Module;
import com.pixelsandmagic.dodgeball.modules.match.MatchModule;
import com.pixelsandmagic.dodgeball.modules.match.MatchState;
import com.pixelsandmagic.dodgeball.modules.motd.listener.PingListener;
import com.pixelsandmagic.dodgeball.util.ChatColorUtil;

/**
 * Created by Giovanni on 5/5/2023
 */
public class MOTDModule extends Module {

    public MOTDModule(DodgeballGame game) {
        super(game);
    }

    @Override
    public void onEnable() {
        registerListener(new PingListener(this));
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
        return "motd";
    }

    public String constructMOTD() {
        MatchModule matchModule = accessModule(MatchModule.class);

        String firstLine = "&d&lSPACE DELTA &7| &bDodgeball";

        String secondLine;
        if (matchModule.hasActiveMatch()) {
            MatchState state = matchModule.getActiveMatch().getState();
            secondLine = state.getText();
        } else {
            secondLine = "&cWaiting for enough players..";
        }

        return ChatColorUtil.color(firstLine + "\n" + secondLine);
    }
}
