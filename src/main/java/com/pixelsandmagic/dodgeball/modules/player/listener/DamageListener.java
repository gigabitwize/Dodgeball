package com.pixelsandmagic.dodgeball.modules.player.listener;

import com.pixelsandmagic.dodgeball.modules.match.MatchModule;
import com.pixelsandmagic.dodgeball.modules.player.PlayerModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * Created by Giovanni on 5/4/2023
 */
public class DamageListener implements Listener {

    private final PlayerModule module;

    public DamageListener(PlayerModule module) {
        this.module = module;
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            MatchModule matchModule = module.accessModule(MatchModule.class);
            if (!matchModule.hasActiveMatch()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            MatchModule matchModule = module.accessModule(MatchModule.class);
            if (!matchModule.hasActiveMatch()) {
                event.setCancelled(true);
                return;
            }
            if (event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
                event.setCancelled(true);
            }
        }
    }
}
