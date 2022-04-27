package com.molean.isletopia.utils;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.task.Tasks;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.LongTag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerSerializeUtils {
    public static void serialize(Player player, Consumer<byte[]> asyncConsumer) {
        Tasks.INSTANCE.sync( () -> {
            player.saveData();
            Tasks.INSTANCE.async(() -> {
                File dataFolder = Objects.requireNonNull(Bukkit.getWorld("SkyWorld")).getWorldFolder();
                String filename = dataFolder + "/playerdata/" + player.getUniqueId() + ".dat";
                File file = new File(filename);
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    asyncConsumer.accept(fileInputStream.readAllBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    asyncConsumer.accept(null);
                }
            });
        });
    }

    public static byte[] serializeSync(Player player) throws IOException {
        if (!Bukkit.isPrimaryThread()) {
            throw new RuntimeException("Must in primary thread");
        }
        player.saveData();
        File dataFolder =  Objects.requireNonNull(Bukkit.getWorld("SkyWorld")).getWorldFolder();
        String filename = dataFolder + "/playerdata/" + player.getUniqueId() + ".dat";
        File file = new File(filename);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return fileInputStream.readAllBytes();
        }
    }

    public static byte[] serializeOffline(UUID uuid) throws IOException {
        File dataFolder =  Objects.requireNonNull(Bukkit.getWorld("SkyWorld")).getWorldFolder();
        String filename = dataFolder + "/playerdata/" + uuid + ".dat";
        File file = new File(filename);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return fileInputStream.readAllBytes();
        }
    }


    public static Long getLong(File file, String field) throws IOException {
        NamedTag read = NBTUtil.read(file);
        CompoundTag compoundTag = (CompoundTag) read.getTag();
        return compoundTag.getLong(field);
    }

    public static void deserialize(Player player, byte[] data, Runnable whenCompleteSync) {
        Tasks.INSTANCE.sync(() -> {
            player.saveData();
            Tasks.INSTANCE.async( () -> {
                File dataFolder =  Objects.requireNonNull(Bukkit.getWorld("SkyWorld")).getWorldFolder();
                String filename = dataFolder + "/playerdata/" + player.getUniqueId() + ".dat";
                File file = new File(filename);
                Long worldUUIDLeast = null;
                Long worldUUIDMost = null;
                try {
                    worldUUIDLeast = getLong(file, "WorldUUIDLeast");
                    worldUUIDMost = getLong(file, "WorldUUIDMost");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    fileOutputStream.write(data);
                    updateFile(file, worldUUIDLeast, worldUUIDMost);
                } catch (IOException e) {
                    e.printStackTrace();
                }
               Tasks.INSTANCE.sync(() -> {
                    for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                        player.removePotionEffect(activePotionEffect.getType());
                    }
                    player.loadData();
                    whenCompleteSync.run();
                });
            });
        });
    }

    public static void modifySpawnLocation(UUID uuid, byte[] data, double x, double y, double z) throws IOException {
        File dataFolder =  Objects.requireNonNull(Bukkit.getWorld("SkyWorld")).getWorldFolder();
        String filename = dataFolder + "/playerdata/" + uuid + ".dat";
        File file = new File(filename);
        if (!file.exists()) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                fileOutputStream.write(data);
            }
        }
        NamedTag read = NBTUtil.read(file);
        CompoundTag compoundTag = (CompoundTag) read.getTag();
        ListTag<DoubleTag> pos = (ListTag<DoubleTag>) compoundTag.get("Pos");
        pos.clear();
        pos.add(new DoubleTag(x));
        pos.add(new DoubleTag(y));
        pos.add(new DoubleTag(z));
        compoundTag.put("Pos", pos);
        NBTUtil.write(read, file);
    }

    public static void deserializeOfflineWithModifier(UUID uuid, byte[] data) throws IOException {
        File dataFolder =  Objects.requireNonNull(Bukkit.getWorld("SkyWorld")).getWorldFolder();
        String filename = dataFolder + "/playerdata/" + uuid + ".dat";
        File file = new File(filename);
        if (file.exists()) {
            Long worldUUIDLeast = getLong(file, "WorldUUIDLeast");
            Long worldUUIDMost = getLong(file, "WorldUUIDMost");
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                fileOutputStream.write(data);
            }
            updateFile(file, worldUUIDLeast, worldUUIDMost);
        } else {
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                fileOutputStream.write(data);
            }
        }
    }

    private static void updateFile(File file, Long worldUUIDLeast, Long worldUUIDMost) throws IOException {
        NamedTag read = NBTUtil.read(file);
        CompoundTag compoundTag = (CompoundTag) read.getTag();
        compoundTag.remove("UUID");
        compoundTag.put("WorldUUIDLeast", new LongTag(worldUUIDLeast));
        compoundTag.put("WorldUUIDMost", new LongTag(worldUUIDMost));
        NBTUtil.write(read, file);
    }
}
