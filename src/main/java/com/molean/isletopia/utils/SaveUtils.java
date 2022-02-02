

package com.molean.isletopia.utils;

import com.molean.isletopia.shared.utils.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.util.UUID;
import java.util.function.Consumer;

public class SaveUtils {

    public static File getPlayerDataFile(String name, Consumer<File> consumer) {
        UUID uuid = UUIDUtils.get(name);
        if (uuid == null) {
            return null;
        }
        return getPlayerDataFile(uuid);
    }

    public static File getPlayerDataFile(UUID uuid) {
        World world = Bukkit.getWorlds().get(0);
        File worldFolder = world.getWorldFolder();
        return new File(worldFolder + String.format("/playerdata/%s.dat", uuid.toString()));
    }

    public static File getPlayerStatsFile(String name, Consumer<File> consumer) {
        UUID uuid = UUIDUtils.get(name);
        return getPlayerStatsFile(uuid);

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
