package com.pixelsandmagic.dodgeball.modules.team;

import com.pixelsandmagic.dodgeball.modules.player.DodgeballPlayer;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Giovanni on 5/4/2023
 */
public class Team {

    private final ColorSet colorSet;
    private final HashSet<DodgeballPlayer> members;
    private final Vector arenaOrigin;
    private final ItemStack chestplate;

    public Team(ColorSet colorSet, Vector arenaOrigin) {
        this.colorSet = colorSet;
        this.members = new HashSet<>();
        this.arenaOrigin = arenaOrigin;

        this.chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta armorMeta = (LeatherArmorMeta) chestplate.getItemMeta();
        armorMeta.setColor(colorSet.getArmorColor());
        armorMeta.setUnbreakable(true);
        this.chestplate.setItemMeta(armorMeta);
    }

    public int getLivingMembers() {
        int amount = 0;
        for (DodgeballPlayer member : members) {
            if (!member.isDead()) amount++;
        }
        return amount;
    }

    public boolean hasLivingMemebers() {
        for (DodgeballPlayer member : members) {
            if (!member.isDead()) return true;
        }
        return false;
    }

    public void addMember(DodgeballPlayer player) {
        this.members.add(player);
    }

    public void removeMember(DodgeballPlayer player) {
        this.members.remove(player);
    }

    public HashSet<DodgeballPlayer> getMembers() {
        return members;
    }

    public int getTotalScore() {
        int score = 0;
        for (DodgeballPlayer member : members) {
            score += member.getMatchScore();
        }
        return score;
    }

    public ItemStack getChestplate() {
        return chestplate;
    }

    public ColorSet getColorSet() {
        return colorSet;
    }

    public String getName() {
        return colorSet.getChatColor() + "" + ChatColor.BOLD + colorSet.name();
    }

    public Location randomLocation(World world) {
        double x = ThreadLocalRandom.current().nextDouble(3);
        double z = ThreadLocalRandom.current().nextDouble(3);
        return arenaOrigin.toLocation(world).add(x, 0, z);
    }

    public enum ColorSet {

        BLUE(ChatColor.AQUA, Color.AQUA, Material.BLUE_WOOL),
        RED(ChatColor.RED, Color.RED, Material.RED_WOOL);

        private final ChatColor chatColor;
        private final Color armorColor;
        private final Material blockMaterial;

        ColorSet(ChatColor chatColor, Color armorColor, Material blockMaterial) {
            this.chatColor = chatColor;
            this.armorColor = armorColor;
            this.blockMaterial = blockMaterial;
        }

        public Color getArmorColor() {
            return armorColor;
        }

        public Material getBlockMaterial() {
            return blockMaterial;
        }

        public ChatColor getChatColor() {
            return chatColor;
        }
    }
}
