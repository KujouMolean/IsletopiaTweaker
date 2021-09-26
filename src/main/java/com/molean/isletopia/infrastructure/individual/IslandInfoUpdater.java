package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.island.Island;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.protect.individual.BeaconIslandOption;
import com.molean.isletopia.shared.utils.RedisUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import redis.clients.jedis.Jedis;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class IslandInfoUpdater {
    private static World world;

    public IslandInfoUpdater() {
        world = Bukkit.getWorld("SkyWorld");
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Island currentPlot = IslandManager.INSTANCE.getCurrentIsland(onlinePlayer);

                if (currentPlot == null) {
                    continue;
                }
                cacheCreation(currentPlot);
                cacheCollections(onlinePlayer);
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                    cacheOptions(currentPlot);
                });
            }
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Island currentPlot = IslandManager.INSTANCE.getCurrentIsland(onlinePlayer);
                if (cacheArea(currentPlot)) {
                    break;
                }
            }

        }, 60, 60);
        IsletopiaTweakers.addDisableTask("Stop update island info", bukkitTask::cancel);

    }

    public static void cacheCollections(Player player) {
        String collection = UniversalParameter.getParameter(player.getName(), "collection");
        if (collection == null || collection.isEmpty()) {
            return;
        }
        try (Jedis jedis = RedisUtils.getJedis()) {
            jedis.set("Collection-" + player.getName(), collection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cacheCreation(Island currentPlot) {
        if (currentPlot == null) {
            return;
        }
        String owner = currentPlot.getOwner();
        try (Jedis jedis = RedisUtils.getJedis()) {
            Timestamp timestamp = currentPlot.getCreation();
            LocalDateTime localDateTime = timestamp.toLocalDateTime();
            String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            jedis.set("Creation-" + owner, format);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void cacheOptions(Island currentPlot) {
        if (currentPlot == null) {
            return;
        }
        String owner = currentPlot.getOwner();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean cacheArea(Island currentPlot) {
        assert world != null;
        if (currentPlot == null) {
            return false;
        }
        String owner = currentPlot.getOwner();

        try (Jedis jedis = RedisUtils.getJedis()) {
            if (jedis.exists("Area-" + owner)) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Location top = currentPlot.getTopLocation();
        Location bot = currentPlot.getBottomLocation();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String getCreation(UUID owner) {
        try (Jedis jedis = RedisUtils.getJedis()) {
            if (jedis.exists("Creation-" + owner)) {
                return jedis.get("Creation-" + owner);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long getArea(UUID owner) {
        try (Jedis jedis = RedisUtils.getJedis()) {
            if (jedis.exists("Area-" + owner)) {
                return Long.parseLong(jedis.get("Area-" + owner));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1L;
    }

    public static String getIslandStatus(UUID uuid) {
        try (Jedis jedis = RedisUtils.getJedis()) {
            if (jedis.exists("Lock-" + uuid)) {
                if (jedis.get("Lock-" + uuid).equalsIgnoreCase("true")) {
                    return "锁定";
                } else {
                    return "开放";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "未知";
    }

    public static String isEnablePvP(UUID uuid) {
        try (Jedis jedis = RedisUtils.getJedis()) {
            if (jedis.exists("EnablePvP-" + uuid)) {
                if (jedis.get("EnablePvP-" + uuid).equalsIgnoreCase("true")) {
                    return "开放";
                } else {
                    return "仅岛员";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "未知";
    }

    public static String isAntiFire(UUID uuid) {
        try (Jedis jedis = RedisUtils.getJedis()) {
            if (jedis.exists("AntiFire-" + uuid)) {
                if (jedis.get("AntiFire-" + uuid).equalsIgnoreCase("true")) {
                    return "开启";
                } else {
                    return "禁用";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "未知";
    }

}
