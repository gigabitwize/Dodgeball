package com.pixelsandmagic.dodgeball.modules.ball;

import com.pixelsandmagic.dodgeball.modules.player.DodgeballPlayer;
import com.pixelsandmagic.dodgeball.util.ChatColorUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

/**
 * Created by Giovanni on 5/4/2023
 */
public abstract class Ball {

    private final UUID id;
    private final DodgeballPlayer thrower;

    private Snowball entity;
    private BallModule module;

    public Ball(DodgeballPlayer thrower) {
        this.id = UUID.randomUUID();
        this.thrower = thrower;
    }

    public DodgeballPlayer getThrower() {
        return thrower;
    }

    public UUID getId() {
        return id;
    }

    public Snowball getEntity() {
        return entity;
    }

    void setEntity(Snowball entity) {
        this.entity = entity;
    }

    public BallModule getModule() {
        return module;
    }

    void setModule(BallModule module) {
        this.module = module;
    }

    public abstract void onTravel();

    public abstract void onFire();

    public abstract void onHit(DodgeballPlayer target);

    public abstract void onLand(Location location);

    static class Item {

        public static final NamespacedKey KEY = new NamespacedKey("dodgeball", "ball-type");

        private final ItemStack itemStack;

        public Item(String keyValue, String name) {
            this.itemStack = new ItemStack(Material.SNOWBALL);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColorUtil.color(name));
            itemMeta.getPersistentDataContainer().set(KEY, PersistentDataType.STRING, keyValue);
            this.itemStack.setItemMeta(itemMeta);
        }

        public ItemStack getItemStack() {
            return itemStack;
        }
    }
}
