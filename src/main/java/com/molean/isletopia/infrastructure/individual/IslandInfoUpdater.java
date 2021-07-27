package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.PlotDao;
import com.molean.isletopia.protect.individual.BeaconIslandOption;
import com.molean.isletopia.shared.utils.RedisUtils;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class IslandInfoUpdater {
    private static World world;

    public IslandInfoUpdater() {
        world = Bukkit.getWorld("SkyWorld");

        Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Plot currentPlot = PlotUtils.getCurrentPlot(onlinePlayer);
                if (currentPlot == null) {
                    continue;
                }

                cacheStatus(currentPlot);
                cacheCreation(currentPlot);
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                    cacheOptions(currentPlot);
                });

            }

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Plot currentPlot = PlotUtils.getCurrentPlot(onlinePlayer);
                if (cacheArea(currentPlot)) {
                    break;
                }
            }

        }, 60, 60);

    }

    public static void cacheCreation(Plot currentPlot) {
        if (currentPlot == null) {
            return;
        }
        UUID owner = currentPlot.getOwner();
        if (owner == null) {
            return;
        }
        try (Jedis jedis = RedisUtils.getJedis()) {
            long creationDate = currentPlot.getTimestamp();
            Timestamp timestamp = new Timestamp(creationDate);
            LocalDateTime localDateTime = timestamp.toLocalDateTime();
            String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            jedis.set("Creation-" + owner, format);

        }

    }

    public static void cacheOptions(Plot currentPlot) {
        if (currentPlot == null) {
            return;
        }
        UUID owner = currentPlot.getOwner();
        if (owner == null) {
            return;
        }
        try (Jedis jedis = RedisUtils.getJedis()) {
            if (BeaconIslandOption.isAntiFire(currentPlot)) {
                jedis.set("AntiFire-" + owner, "true");
            } else {
                jedis.set("AntiFire-" + owner, "false");
            }

            if (BeaconIslandOption.isEnablePvP(currentPlot)) {
                jedis.set("EnablePvP-" + owner, "true");
            } else {
                jedis.set("EnablePvP-" + owner, "false");
            }
        }
    }


    public static void cacheStatus(Plot currentPlot) {
        if (currentPlot == null) {
            return;
        }
        UUID owner = currentPlot.getOwner();
        if (owner == null) {
            return;
        }
        try (Jedis jedis = RedisUtils.getJedis()) {
            if (currentPlot.getDenied().contains(PlotDao.getAllUUID())) {
                jedis.set("Lock-" + owner, "true");
            } else {
                jedis.set("Lock-" + owner, "false");

            }
        }

    }


    public static boolean cacheArea(Plot currentPlot) {
        assert world != null;
        if (currentPlot == null) {
            return false;
        }

        UUID owner = currentPlot.getOwner();

        if (owner == null) {
            return false;
        }

        try (Jedis jedis = RedisUtils.getJedis()) {
            if (jedis.exists("Area-" + owner)) {
                return false;
            }
        }

        Location top = PlotUtils.fromPlotLocation(currentPlot.getTopAbs());
        Location bot = PlotUtils.fromPlotLocation(currentPlot.getBottomAbs());
        long areaCount = 0;
        for (int i = bot.getBlockX(); i < top.getBlockX(); i++) {
            for (int j = bot.getBlockZ(); j < top.getBlockZ(); j++) {
                int highestBlockYAt = world.getHighestBlockYAt(i, j);
                if (highestBlockYAt > 0) {
                    areaCount++;
                }
            }
        }

        try (Jedis jedis = RedisUtils.getJedis()) {
            jedis.setex("Area-" + owner, 60 * 15L, "" + areaCount);
        }
        return true;
    }

    public static String getCreation(UUID owner) {
        try (Jedis jedis = RedisUtils.getJedis()) {
            if (jedis.exists("Creation-" + owner)) {
                return jedis.get("Creation-" + owner);
            } else {
                return null;
            }
        }
    }

    public static long getArea(UUID owner) {
        try (Jedis jedis = RedisUtils.getJedis()) {
            if (jedis.exists("Area-" + owner)) {
                return Long.parseLong(jedis.get("Area-" + owner));
            } else {
                return -1L;
            }
        }
    }

    public static String getIslandStatus(UUID uuid) {
        try (Jedis jedis = RedisUtils.getJedis()) {
            if (jedis.exists("Lock-" + uuid)) {
                if (jedis.get("Lock-" + uuid).equalsIgnoreCase("true")) {
                    return "锁定";
                } else {
                    return "开放";
                }
            } else {
                return "未知";
            }
        }
    }

    public static String isEnablePvP(UUID uuid) {
        try (Jedis jedis = RedisUtils.getJedis()) {
            if (jedis.exists("EnablePvP-" + uuid)) {
                if (jedis.get("EnablePvP-" + uuid).equalsIgnoreCase("true")) {
                    return "开放";
                } else {
                    return "仅岛员";
                }
            } else {
                return "未知";
            }
        }
    }
    public static String isAntiFire(UUID uuid) {
        try (Jedis jedis = RedisUtils.getJedis()) {
            if (jedis.exists("AntiFire-" + uuid)) {
                if (jedis.get("AntiFire-" + uuid).equalsIgnoreCase("true")) {
                    return "开启";
                } else {
                    return "禁用";
                }
            } else {
                return "未知";
            }
        }
    }

}
