package com.molean.isletopia.utils;

import com.plotsquared.core.PlotSquared;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class UUIDUtils {

    @Nullable
    public static String get(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer.getName() != null) {
            return offlinePlayer.getName();
        } else {
            if (!Bukkit.isPrimaryThread()) {
                return PlotSquared.get().getImpromptuUUIDPipeline().getSingle(uuid, 200L);
            } else {
                return null;
            }
        }
    }

    @NotNull
    public static UUID get(String player) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + player).getBytes(StandardCharsets.UTF_8));
    }
}
