package com.pixelsandmagic.dodgeball.modules.ball;

import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;

import java.util.UUID;

/**
 * Created by Giovanni on 5/4/2023
 */
class DroppedBall {

    private final UUID id;
    private final Item item;
    private final ArmorStand armorStand;

    public DroppedBall(Item item) {
        this.id = UUID.randomUUID();
        this.item = item;
        this.armorStand = (ArmorStand) item.getWorld().spawnEntity(item.getLocation().clone().add(0, 0.5, 0), EntityType.ARMOR_STAND);

        String displayName = item.getItemStack().getItemMeta().getDisplayName();
        armorStand.setCustomName(ChatColor.BOLD + displayName.toUpperCase());
        armorStand.setCustomNameVisible(true);
        armorStand.setInvisible(true);
        armorStand.setSmall(true);
        armorStand.setGravity(false);
        this.item.addPassenger(armorStand);
    }

    protected void removeArmorStand() {
        this.armorStand.remove();
    }

    protected void remove() {
        this.item.remove();
        removeArmorStand();
    }

    public UUID getId() {
        return id;
    }
}
