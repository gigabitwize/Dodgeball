package com.pixelsandmagic.dodgeball.util;


import com.pixelsandmagic.dodgeball.DodgeballPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

/**
 * Created by Giovanni on 5/5/2023
 * <p>
 * Re-usable countdown util.
 */
public class Countdown {

    private final DodgeballPlugin plugin;
    private final long delay;
    private final long from;

    private long current;
    private BukkitTask currentTask;

    public Countdown(DodgeballPlugin plugin, long delay, long from) {
        this.plugin = plugin;
        this.delay = delay;
        this.from = from;
    }

    public void run(Consumer<Long> onTick, Runnable onComplete) {
        this.current = from;

        this.currentTask = new BukkitRunnable() {
            @Override
            public void run() {
                current--;
                if (current == 0) {
                    onComplete.run();
                    cancel();
                    return;
                }
                onTick.accept(current);
            }
        }.runTaskTimer(plugin, delay * 20, 20L);
    }

    public void interrupt() {
        this.currentTask.cancel();
    }
}
