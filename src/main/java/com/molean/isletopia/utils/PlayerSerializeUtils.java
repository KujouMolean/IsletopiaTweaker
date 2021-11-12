package com.molean.isletopia.utils;

import com.molean.isletopia.IsletopiaTweakers;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.LongTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.io.*;
import java.util.UUID;

public class PlayerSerializeUtils {
    public static byte[] serialize(Player player) throws IOException {
        if (!Bukkit.isPrimaryThread()) {
            throw new RuntimeException("Can't serialize a player async!");
        }
        player.saveData();
        File dataFolder = IsletopiaTweakers.getWorld().getWorldFolder();
        String filename = dataFolder + "/playerdata/" + player.getUniqueId() + ".dat";
        File file = new File(filename);
        FileInputStream fileInputStream = new FileInputStream(file);
        return fileInputStream.readAllBytes();
    }

    public static byte[] serializeOffline(UUID uuid) throws IOException {
        File dataFolder = IsletopiaTweakers.getWorld().getWorldFolder();
        String filename = dataFolder + "/playerdata/" + uuid + ".dat";
        File file = new File(filename);
        FileInputStream fileInputStream = new FileInputStream(file);
        return fileInputStream.readAllBytes();
    }



    public static Long getLong(File file,String field) throws IOException {
        NamedTag read = NBTUtil.read(file);
        CompoundTag compoundTag = (CompoundTag) read.getTag();
        return compoundTag.getLong(field);
    }

    public static void deserialize(Player player, byte[] data) throws IOException {
        if (!Bukkit.isPrimaryThread()) {
            throw new RuntimeException("Can't deserialize a player async!");
        }
        player.saveData();
        File dataFolder = IsletopiaTweakers.getWorld().getWorldFolder();
        String filename = dataFolder + "/playerdata/" + player.getUniqueId() + ".dat";
        File file = new File(filename);
        Long worldUUIDLeast = getLong(file, "WorldUUIDLeast");
        Long worldUUIDMost = getLong(file, "WorldUUIDMost");

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(data);
        updateFile(file, worldUUIDLeast, worldUUIDMost);
        for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(activePotionEffect.getType());
        }
        player.loadData();
    }

    private static void updateFile(File file, Long worldUUIDLeast, Long worldUUIDMost) throws IOException {
        NamedTag read = NBTUtil.read(file);
        CompoundTag compoundTag = (CompoundTag) read.getTag();
        compoundTag.remove("UUID");
        compoundTag.put("WorldUUIDLeast",new LongTag(worldUUIDLeast));
        compoundTag.put("WorldUUIDMost", new LongTag(worldUUIDMost));
        NBTUtil.write(read, file);

    }
}
