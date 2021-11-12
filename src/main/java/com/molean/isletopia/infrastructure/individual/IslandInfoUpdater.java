package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.utils.RedisUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class IslandInfoUpdater {
    private static World world;

    public IslandInfoUpdater() {
        world = Bukkit.getWorld("SkyWorld");
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
//                Island currentPlot = IslandManager.INSTANCE.getCurrentIsland(onlinePlayer);
//
//                if (currentPlot == null) {
//                    continue;
//                }
//                cacheCreation(currentPlot);
//                cacheCollections(onlinePlayer);
//                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
//                    cacheOptions(currentPlot);
//                });
            }
//            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
//                Island currentPlot = IslandManager.INSTANCE.getCurrentIsland(onlinePlayer);
//                if (cacheArea(currentPlot)) {
//                    break;
//                }
//            }

        }, 60, 60);
        IsletopiaTweakers.addDisableTask("Stop update island info", bukkitTask::cancel);

    }

//    public static void cacheCollections(Player player) {
////        String collection = UniversalParameter.getParameter(player, "collection");
//        if (collection == null || collection.isEmpty()) {
//            return;
//        }
//        RedisUtils.getCommand().set("Collection-" + player.getName(), collection);
//    }

//    public static void cacheCreation(Island currentPlot) {
//        if (currentPlot == null) {
//            return;
//        }
//        String owner = currentPlot.getOwner();
//            Timestamp timestamp = currentPlot.getCreation();
//            LocalDateTime localDateTime = timestamp.toLocalDateTime();
//            String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//        RedisUtils.getCommand().set("Creation-" + owner, format);
//
//
//    }

//    public static void cacheOptions(Island currentPlot) {
//        if (currentPlot == null) {
//            return;
//        }
////        String owner = currentPlot.getOwner();
//            if (BeaconIslandOption.isAntiFire(currentPlot)) {
//                RedisUtils.getCommand().set("AntiFire-" + owner, "true");
//            } else {
//                RedisUtils.getCommand().set("AntiFire-" + owner, "false");
//            }
//
//            if (BeaconIslandOption.isEnablePvP(currentPlot)) {
//                RedisUtils.getCommand().set("EnablePvP-" + owner, "true");
//            } else {
//                RedisUtils.getCommand().set("EnablePvP-" + owner, "false");
//            }
//    }


//    public static boolean cacheArea(Island currentPlot) {
//        assert world != null;
//        if (currentPlot == null) {
//            return false;
//        }
//        String owner = currentPlot.getOwner();
//
//            if (RedisUtils.getCommand().exists("Area-" + owner)>0) {
//                return false;
//            }
//
//        Location top = currentPlot.getTopLocation();
//        Location bot = currentPlot.getBottomLocation();
//        long areaCount = 0;
//        for (int i = bot.getBlockX(); i < top.getBlockX(); i++) {
//            for (int j = bot.getBlockZ(); j < top.getBlockZ(); j++) {
//                int highestBlockYAt = world.getHighestBlockYAt(i, j);
//                if (highestBlockYAt > 0) {
//                    areaCount++;
//                }
//            }
//        }
//
//        RedisUtils.getCommand().setex("Area-" + owner, 60 * 15L, "" + areaCount);
//        return true;
//    }

    public static String getCreation(UUID owner) {
            if (RedisUtils.getCommand().exists("Creation-" + owner)>0) {
                return RedisUtils.getCommand().get("Creation-" + owner);
            }
        return null;
    }

    public static long getArea(UUID owner) {
        if (RedisUtils.getCommand().exists("Area-" + owner) > 0) {

            return Long.parseLong(RedisUtils.getCommand().get("Area-" + owner));
        }
        return -1L;
    }

    public static String getIslandStatus(UUID uuid) {
        if (RedisUtils.getCommand().exists("Lock-" + uuid) > 0) {
            if (RedisUtils.getCommand().get("Lock-" + uuid).equalsIgnoreCase("true")) {
                return "锁定";
            } else {
                return "开放";
            }
        }
        return "未知";
    }

    public static String isEnablePvP(UUID uuid) {
            if (RedisUtils.getCommand().exists("EnablePvP-" + uuid)>0) {
                if (RedisUtils.getCommand().get("EnablePvP-" + uuid).equalsIgnoreCase("true")) {
                    return "开放";
                } else {
                    return "仅岛员";
                }
            }
        return "未知";
    }

    public static String isAntiFire(UUID uuid) {
            if (RedisUtils.getCommand().exists("AntiFire-" + uuid)>0) {
                if (RedisUtils.getCommand().get("AntiFire-" + uuid).equalsIgnoreCase("true")) {
                    return "开启";
                } else {
                    return "禁用";
                }
            }
        return "未知";
    }

}
