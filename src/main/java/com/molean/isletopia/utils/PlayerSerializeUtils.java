package com.molean.isletopia.utils;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;

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

    public static void deserialize(Player player, byte[] data) throws IOException {
        if (!Bukkit.isPrimaryThread()) {
            throw new RuntimeException("Can't deserialize a player async!");
        }
        File dataFolder = IsletopiaTweakers.getWorld().getWorldFolder();
        String filename = dataFolder + "/playerdata/" + player.getUniqueId() + ".dat";
        File file = new File(filename);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(data);
        player.loadData();
    }
}
