package com.pixelsandmagic.dodgeball.core.world;

import com.pixelsandmagic.dodgeball.DodgeballPlugin;
import com.pixelsandmagic.dodgeball.core.world.util.VoidChunkGenerator;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Giovanni on 5/4/2023
 */
public class DodgeballWorld {

    private final DodgeballPlugin plugin;

    private final File dataFolder;
    private final String worldName;
    private World world;
    private Location spawn;

    private DodgeballLobby lobby;
    private DodgeballArena arena;

    public DodgeballWorld(DodgeballPlugin plugin, File dataFolder, String worldName) {
        this.plugin = plugin;
        this.dataFolder = dataFolder;
        this.worldName = worldName;
    }

    public void setup() {
        if (Bukkit.getWorld(worldName) != null)
            Bukkit.unloadWorld(worldName, false);

        InputStream worldStream = plugin.getResource("sd-instance.zip");
        File instanceZip = new File(plugin.getServer().getWorldContainer(), "sd-instance.zip");

        try {
            FileUtils.copyToFile(worldStream, instanceZip);

            ZipFile zipFile = new ZipFile(instanceZip);
            zipFile.extractAll(plugin.getServer().getWorldContainer() + "/" + worldName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 1. Generate void world
        this.world = new WorldCreator(worldName).generator(new VoidChunkGenerator()).createWorld();
        this.world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        this.world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        for (Entity entity : this.world.getEntities()) {
            entity.remove();
        }
        this.spawn = new Location(world, -20, 19, -17);

        if (!dataFolder.exists())
            dataFolder.mkdir();

        // 2. Place lobby
        this.lobby = new DodgeballLobby(this, spawn.clone().subtract(0, 1, 0));
        lobby.place();

        // 3. Place dodgeball arena
        this.arena = new DodgeballArena(this, new Location(world, 255, 40, 255));
    }

    public void placeSchematic(File schematicFile, Vector root) throws IOException, WorldEditException {
        ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
        try (ClipboardReader reader = format.getReader(new FileInputStream(schematicFile))) {
            Clipboard clipboard = reader.read();
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(world), -1)) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(root.getX(), root.getY(), root.getZ()))
                        .ignoreAirBlocks(false)
                        .build();
                Operations.complete(operation);
            }
        }
    }

    public void delete() {
        Bukkit.unloadWorld(world, false);
    }

    public Location getSpawn() {
        return spawn;
    }

    public DodgeballLobby getLobby() {
        return lobby;
    }

    public DodgeballArena getArena() {
        return arena;
    }

    public World asBukkitWorld() {
        return world;
    }
}
