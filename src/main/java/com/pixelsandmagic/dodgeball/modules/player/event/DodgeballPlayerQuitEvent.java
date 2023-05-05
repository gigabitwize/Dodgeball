package com.pixelsandmagic.dodgeball.modules.player.event;

import com.pixelsandmagic.dodgeball.modules.player.DodgeballPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Giovanni on 5/4/2023
 */
public class DodgeballPlayerQuitEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final DodgeballPlayer player;

    public DodgeballPlayerQuitEvent(DodgeballPlayer player) {
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public DodgeballPlayer getPlayer() {
        return player;
    }

    public final HandlerList getHandlers() {
        return handlers;
    }
}
