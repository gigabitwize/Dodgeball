package com.pixelsandmagic.dodgeball.modules.ball.listener;

import com.pixelsandmagic.dodgeball.modules.ball.BallModule;
import com.pixelsandmagic.dodgeball.modules.player.DodgeballPlayer;
import com.pixelsandmagic.dodgeball.modules.player.PlayerModule;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

import java.util.UUID;

/**
 * Created by Giovanni on 5/4/2023
 */
public class BallListener implements Listener {

    private final BallModule module;

    public BallListener(BallModule module) {
        this.module = module;
    }

    public void unregister() {
        EntityPickupItemEvent.getHandlerList().unregister(this);
        PlayerInteractEvent.getHandlerList().unregister(this);
        ProjectileHitEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            event.setCancelled(true);
            return;
        }

        if (!module.isEnabled()) {
            event.getItem().remove();
            return;
        }

        DodgeballPlayer dodgeballPlayer = module.accessModule(PlayerModule.class).getPlayer(player.getUniqueId());
        module.handlePickup(dodgeballPlayer, event.getItem());
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        DodgeballPlayer dodgeballPlayer = module.accessModule(PlayerModule.class).getPlayer(player.getUniqueId());

        if (!module.isEnabled()) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        if (event.getItem() == null) {
            return;
        }

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getItem().getType() == Material.SNOWBALL) {
            module.throwBall(dodgeballPlayer, event.getItem());
            dodgeballPlayer.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile.getType() != EntityType.SNOWBALL) {
            return;
        }
        if (!projectile.hasMetadata("ball-id")) {
            event.setCancelled(true);
            return;
        }

        if (!module.isEnabled()) {
            event.setCancelled(true);
            event.getEntity().remove();
            return;
        }

        MetadataValue value = projectile.getMetadata("ball-id").get(0);
        UUID ballId = UUID.fromString(value.asString());
        module.getActiveBall(ballId).ifPresentOrElse(ball -> {
            module.handleHit(event, ball);
        }, () -> {
            event.setCancelled(true);
        });
    }
}
