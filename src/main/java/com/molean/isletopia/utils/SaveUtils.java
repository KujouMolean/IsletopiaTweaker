

package com.molean.isletopia.utils;

import com.molean.isletopia.bungee.individual.ServerInfoUpdater;
import java.io.File;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class SaveUtils {
    public SaveUtils() {
    }

    public static File getPlayerDataFile(String name) {
        return getPlayerDataFile(ServerInfoUpdater.getUUID(name));
    }

    public static File getPlayerDataFile(UUID uuid) {
        World world = Bukkit.getWorlds().get(0);
        File worldFolder = world.getWorldFolder();
        return new File(worldFolder + String.format("/playerdata/%s.dat", uuid.toString()));
    }

    public static File getPlayerStatsFile(String name) {
        return getPlayerStatsFile(ServerInfoUpdater.getUUID(name));
    }

    public static File getPlayerStatsFile(UUID uuid) {
        World world = Bukkit.getWorlds().get(0);
        File worldFolder = world.getWorldFolder();
        return new File(worldFolder + String.format("/stats/%s.dat", uuid.toString()));
    }

    public static File getLevelFile() {
        World world = Bukkit.getWorlds().get(0);
        File worldFolder = world.getWorldFolder();
        return new File(worldFolder + "/level.dat");
    }
}
