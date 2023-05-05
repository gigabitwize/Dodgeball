package com.pixelsandmagic.dodgeball.modules.ball;

import com.pixelsandmagic.dodgeball.modules.ball.impl.DefaultBall;
import com.pixelsandmagic.dodgeball.modules.ball.impl.ExplosiveBall;
import com.pixelsandmagic.dodgeball.modules.player.DodgeballPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Giovanni on 5/5/2023
 */
public enum BallType {

    DEFAULT(100, new Ball.Item("DEFAULT", "&aBall")),
    EXPLOSIVE(10, new Ball.Item("EXPLOSIVE", "&eExplosive Ball"));

    private final float dropChance;
    private final Ball.Item item;

    BallType(float dropChance, Ball.Item item) {
        this.item = item;
        this.dropChance = dropChance;
    }

    public static Ball derive(DodgeballPlayer thrower, ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        String value = itemMeta.getPersistentDataContainer().get(Ball.Item.KEY, PersistentDataType.STRING);

        BallType ballType = BallType.valueOf(value);

        // Could've used reflection to do class#newInstance to make it more dynamic, but also impacts performance
        // so switch
        Ball ball = null;
        switch (ballType) {
            case DEFAULT -> ball = new DefaultBall(thrower);
            case EXPLOSIVE -> ball = new ExplosiveBall(thrower);
        }
        return ball;
    }

    public static Ball.Item randomBallItem() {
        float random = ThreadLocalRandom.current().nextFloat(100);
        Ball.Item item = null;

        for (BallType ballType : BallType.values()) {
            float chance = ballType.getDropChance();
            if (random <= chance)
                item = ballType.getItem();
        }

        if (item == null) {
            item = BallType.DEFAULT.getItem();
        }
        return item;
    }

    public float getDropChance() {
        return dropChance;
    }

    public Ball.Item getItem() {
        return item;
    }
}
