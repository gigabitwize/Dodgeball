package com.pixelsandmagic.dodgeball.modules.ball;

import com.pixelsandmagic.dodgeball.DodgeballGame;
import com.pixelsandmagic.dodgeball.core.module.Module;
import com.pixelsandmagic.dodgeball.modules.ball.listener.BallListener;
import com.pixelsandmagic.dodgeball.modules.player.DodgeballPlayer;
import com.pixelsandmagic.dodgeball.modules.player.PlayerModule;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Giovanni on 5/4/2023
 */
@SuppressWarnings("all")
public class BallModule extends Module {

    private final BallListener listener;
    private int travelTask;

    private ConcurrentHashMap<UUID, Ball> activeBalls;
    private ConcurrentHashMap<UUID, DroppedBall> droppedBalls;

    public BallModule(DodgeballGame game) {
        super(game);
        this.listener = new BallListener(this);
    }

    public void giveBalls() {
        if (!isEnabled()) return;
        for (DodgeballPlayer onlinePlayer : accessModule(PlayerModule.class).getOnlinePlayers()) {
            Player player = onlinePlayer.getPlayer();
            player.getInventory().addItem(BallType.DEFAULT.getItem().getItemStack());
        }
    }

    public void handleHit(ProjectileHitEvent event, Ball ball) {
        if (activeBalls == null) {
            System.err.println("Can't register ballhit while BallModule is disabled");
            return;
        }

        if (event.getHitEntity() != null) {
            Entity entity = event.getHitEntity();
            if (!(entity instanceof Player player)) {
                event.setCancelled(true);
                return;
            }
            DodgeballPlayer dodgeballPlayer = accessModule(PlayerModule.class).getPlayer(player.getUniqueId());

            // For knockback
            player.damage(0.1);
            player.setHealth(player.getMaxHealth());

            ball.onHit(dodgeballPlayer);
            activeBalls.remove(ball.getId());

            event.getEntity().remove();
            dropBallAt(player.getLocation());
            return;
        }
        if (event.getHitBlock() != null && event.getHitBlock().getType() != Material.AIR) {
            ball.onLand(event.getHitBlock().getLocation());
            activeBalls.remove(ball.getId());

            event.getEntity().remove();
            dropBallAt(event.getHitBlock().getLocation());
        }
    }

    public void throwBall(DodgeballPlayer thrower, ItemStack itemStack) {
        thrower.playSound(Sound.ENTITY_SNOWBALL_THROW, 10F, 0.75F);
        throwBall(BallType.derive(thrower, itemStack));
    }

    public void throwBall(Ball ball) {
        if (activeBalls == null) {
            System.err.println("Can't throw ball while BallModule is disabled");
            return;
        }

        ball.setModule(this);

        DodgeballPlayer thrower = ball.getThrower();
        Snowball snowball = thrower.launchProjectile();

        snowball.setMetadata("ball-id", new FixedMetadataValue(getGame().getPlugin(), ball.getId().toString()));

        ball.setEntity(snowball);
        activeBalls.put(ball.getId(), ball);

        ball.onFire();
    }

    public void handlePickup(DodgeballPlayer player, Item item) {
        if (!item.hasMetadata("dropped-ball-id")) {
            return;
        }
        MetadataValue value = item.getMetadata("dropped-ball-id").get(0);
        UUID id = UUID.fromString(value.asString());

        DroppedBall droppedBall = droppedBalls.get(id);
        if (droppedBall == null) return;
        droppedBall.removeArmorStand();
        droppedBalls.remove(id);
    }

    public void dropBallAt(Location location) {
        // This prevents merging of the snowballs on the ground
        ItemStack antiMerge = BallType.randomBallItem().getItemStack();
        ItemMeta itemMeta = antiMerge.getItemMeta();
        itemMeta.setCustomModelData(ThreadLocalRandom.current().nextInt());
        antiMerge.setItemMeta(itemMeta);

        Item item = location.getWorld().dropItem(location.clone().add(0, 0.3, 0), antiMerge);
        DroppedBall droppedBall = new DroppedBall(item);

        item.setMetadata("dropped-ball-id", new FixedMetadataValue(getGame().getPlugin(), droppedBall.getId().toString()));

        droppedBalls.put(droppedBall.getId(), droppedBall);
    }

    public Optional<Ball> getActiveBall(UUID uuid) {
        return Optional.ofNullable(activeBalls.get(uuid));
    }

    @Override
    public void onEnable() {
        this.droppedBalls = new ConcurrentHashMap<>();
        this.activeBalls = new ConcurrentHashMap<>();
        registerListener(listener);

        this.travelTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(getGame().getPlugin(), () -> {
            for (Ball ball : activeBalls.values()) {
                ball.onTravel();
            }
        }, 0L, 2L);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTask(travelTask);

        this.listener.unregister();

        this.activeBalls = null;

        if (this.droppedBalls != null)
            for (DroppedBall value : this.droppedBalls.values()) {
                value.remove();
            }

        this.droppedBalls = null;
    }

    @Override
    public boolean usesConfiguration() {
        return false;
    }

    @Override
    public String getName() {
        return "ball";
    }
}
