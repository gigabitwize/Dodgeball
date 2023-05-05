package com.pixelsandmagic.dodgeball.modules.match;

/**
 * Created by Giovanni on 5/4/2023
 */
public enum MatchState {

    UNDEFINED("&cWaiting for players.."),
    PREPARING("&6Warming up.."),
    STARTED("&aMatch started!"),
    FINALIZING("&eMatch ending..");

    private final String text;

    MatchState(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
