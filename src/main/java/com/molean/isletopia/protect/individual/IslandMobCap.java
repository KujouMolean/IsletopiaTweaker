package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.model.IslandId;
import com.molean.isletopia.shared.utils.LangUtils;
import com.molean.isletopia.shared.utils.ObjectUtils;
import com.molean.isletopia.shared.utils.RedisUtils;
import com.molean.isletopia.task.CustomTask;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class IslandMobCap implements Listener, CommandExecutor, TabCompleter {

    private static final Map<EntityType, Integer> map = new HashMap<>();
    private static final List<EntityType> ignoredType = new ArrayList<>();
    private static final Map<IslandId, Map<EntityType, Integer>> plotsEntities = new ConcurrentHashMap<>();
    private static final Map<String, Map<EntityType, Integer>> unloadedLocalCache = new ConcurrentHashMap<>();
    private static final Map<IslandId, Integer> plotsEntityCount = new ConcurrentHashMap<>();

    private static final Set<IslandId> shouldUpdatePlot = new HashSet<>();

    public static void setMobCap(EntityType entityType, Integer integer) {
        map.put(entityType, integer);
    }

    public static void unsetMobCap(EntityType entityType) {
        map.remove(entityType);
    }

    public IslandMobCap() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        Objects.requireNonNull(Bukkit.getPluginCommand("mobcap")).setTabCompleter(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("mobcap")).setExecutor(this);
        setMobCap(EntityType.GUARDIAN, 50);
        ignoredType.add(EntityType.ITEM_FRAME);
        ignoredType.add(EntityType.GLOW_ITEM_FRAME);
        ignoredType.add(EntityType.SMALL_FIREBALL);
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(onlinePlayer);
                if (currentPlot != null) {
                    shouldUpdatePlot.add(currentPlot.getIslandId());
                }
            }

            Queue<Runnable> runnables = new ArrayDeque<>();

            for (IslandId plotId : shouldUpdatePlot) {
                runnables.add(() -> {
                    newCountEntities(plotId);
                });
            }

            new CustomTask(runnables, 20, null).runAsync();

            shouldUpdatePlot.clear();
        }, 20L, 20L);
        IsletopiaTweakers.addDisableTask("Stop record mob cap data..", bukkitTask::cancel);
    }

    private static final Map<LocalIsland, Long> lastNotifyTimeMap = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void on(ChunkUnloadEvent event) {
        int x = event.getChunk().getX();
        int z = event.getChunk().getZ();
        String stringKey = "ChunkCount:" + ServerInfoUpdater.getServerName() + x + "," + z;
        byte[] key = stringKey.getBytes(StandardCharsets.UTF_8);
        HashMap<EntityType, Integer> entityTypeIntegerHashMap = new HashMap<>();
        for (Entity entity : event.getChunk().getEntities()) {
            int old = entityTypeIntegerHashMap.getOrDefault(entity.getType(), 0);
            entityTypeIntegerHashMap.put(entity.getType(), old + 1);
        }
        byte[] serialize = ObjectUtils.serialize(entityTypeIntegerHashMap);
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            RedisUtils.getByteCommand().set(key, serialize);
            unloadedLocalCache.put(stringKey, entityTypeIntegerHashMap);
        });
    }

    public static void countChunk(int x, int z, Consumer<Map<EntityType, Integer>> consumer) {
        World world = IsletopiaTweakers.getWorld();
        String stringKey = "ChunkCount:" + ServerInfoUpdater.getServerName() + x + "," + z;
        byte[] key = stringKey.getBytes(StandardCharsets.UTF_8);

        if (world.isChunkLoaded(x, z)) {
            HashMap<EntityType, Integer> entityTypeIntegerHashMap = new HashMap<>();
            // keep update if loaded
            world.getChunkAtAsync(x, z, false, chunk -> {
                for (Entity entity : chunk.getEntities()) {
                    int old = entityTypeIntegerHashMap.getOrDefault(entity.getType(), 0);
                    entityTypeIntegerHashMap.put(entity.getType(), old + 1);
                }
                consumer.accept(entityTypeIntegerHashMap);
            });
        } else {
            // try load from local memory cache
            if (unloadedLocalCache.containsKey(stringKey)) {
                consumer.accept(unloadedLocalCache.get(stringKey));
                return;
            }

            //else load from redis server
            if (RedisUtils.getByteCommand().exists(key) > 0) {
                byte[] bytes = RedisUtils.getByteCommand().get(key);
                HashMap<EntityType, Integer> deserialize = (HashMap<EntityType, Integer>) ObjectUtils.deserialize(bytes);
                unloadedLocalCache.put(stringKey, deserialize);

                consumer.accept(deserialize);
                //end
                return;
            }

            //else manually load chunk and count
            Chunk chunkAt = world.getChunkAt(x, z);
            HashMap<EntityType, Integer> entityTypeIntegerHashMap = new HashMap<>();
            for (Entity entity : chunkAt.getEntities()) {
                int old = entityTypeIntegerHashMap.getOrDefault(entity.getType(), 0);
                entityTypeIntegerHashMap.put(entity.getType(), old + 1);
            }
            byte[] serialize = ObjectUtils.serialize(entityTypeIntegerHashMap);
            RedisUtils.getByteCommand().set(key, serialize);
            unloadedLocalCache.put(stringKey, entityTypeIntegerHashMap);
            consumer.accept(entityTypeIntegerHashMap);
            //end
        }
    }

    private static void warn(LocalIsland currentPlot) {
        Long lastNotifyTime = lastNotifyTimeMap.getOrDefault(currentPlot, 0L);
        if (System.currentTimeMillis() - lastNotifyTime > 3 * 60 * 1000L) {

            lastNotifyTimeMap.put(currentPlot, System.currentTimeMillis());
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (Objects.equals(IslandManager.INSTANCE.getCurrentIsland(onlinePlayer), currentPlot)) {
                    if (IslandManager.INSTANCE.hasCurrentIslandPermission(onlinePlayer)) {
                        MessageUtils.warn(onlinePlayer, "此岛屿实体已达上限, 实体无法再继续生成, 掉落物品可能会消失.");
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItem(ItemSpawnEvent event) {
        Item entity = event.getEntity();
        Location location = entity.getLocation();
        LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(location);
        if (currentPlot == null) {
            return;
        }
        if (!plotsEntityCount.containsKey(currentPlot.getIslandId())) {
            return;
        }
        int count = plotsEntityCount.get(currentPlot.getIslandId());
        if (count >= 1024) {
            event.setCancelled(true);
        }
        if (count >= 512) {
            warn(currentPlot);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(VehicleMoveEvent event) {
        org.bukkit.Location from = event.getFrom();
        org.bukkit.Location to = event.getTo();
        int plotFromX = Math.floorDiv(from.getBlockX(), 512) + 1;
        int plotFromZ = Math.floorDiv(from.getBlockZ(), 512) + 1;
        int plotToX = Math.floorDiv(to.getBlockX(), 512) + 1;
        int plotToZ = Math.floorDiv(to.getBlockZ(), 512) + 1;
        if (plotFromX != plotToX || plotFromZ != plotToZ) {
            event.getVehicle().remove();
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntitySpawn(EntitySpawnEvent event) {
        LocalIsland plot = IslandManager.INSTANCE.getCurrentIsland(event.getLocation());
        if (plot == null) {
            return;
        }
        Map<EntityType, Integer> entityTypeIntegerMap = plotsEntities.get(plot.getIslandId());
        if (entityTypeIntegerMap == null) {
            return;
        }
        if (plotsEntityCount.get(plot.getIslandId()) >= 512) {
            warn(plot);
            event.setCancelled(true);
            return;
        }
        EntityType entityType = event.getEntity().getType();
        if (map.containsKey(entityType)) {
            if (entityTypeIntegerMap.getOrDefault(entityType, 0) >= map.get(entityType)) {
                event.setCancelled(true);
            } else {
                entityTypeIntegerMap.put(entityType, entityTypeIntegerMap.getOrDefault(entityType, 0) + 1);
                plotsEntityCount.put(plot.getIslandId(), plotsEntityCount.getOrDefault(plot.getIslandId(), 0) + 1);
            }
        }
        shouldUpdatePlot.add(plot.getIslandId());
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onMobDeath(EntityDeathEvent event) {
        LocalIsland plot = IslandManager.INSTANCE.getCurrentIsland(event.getEntity().getLocation());
        if (plot == null) {
            return;
        }
        shouldUpdatePlot.add(plot.getIslandId());
        Map<EntityType, Integer> entityTypeIntegerMap = plotsEntities.get(plot.getIslandId());
        if (entityTypeIntegerMap == null) {
            return;
        }
        if (entityTypeIntegerMap.containsKey(event.getEntityType())) {
            entityTypeIntegerMap.put(event.getEntityType(), entityTypeIntegerMap.getOrDefault(event.getEntityType(), 0) - 1);
            plotsEntityCount.put(plot.getIslandId(), plotsEntityCount.getOrDefault(plot.getIslandId(), 0) - 1);

        }
    }


    public static void newCountEntities(IslandId plotId) {

        HashMap<EntityType, Integer> plotEntities = new HashMap<>();

        if (!plotId.getServer().equals(ServerInfoUpdater.getServerName())) {
            throw new RuntimeException("Can't count mob in other server!");
        }

        int bx = plotId.getX() << 5;
        int ex = (plotId.getX() + 1) << 5;

        int bz = plotId.getZ() << 5;
        int ez = (plotId.getZ() + 1) << 5;
        new Runnable() {
            int count = 0;
            private final Object countLock = new Object();

            @Override
            public void run() {
                for (int i = bx; i < ex; i++) {
                    for (int j = bz; j < ez; j++) {
                        synchronized (countLock) {
                            count++;
                        }
                        countChunk(i, j, entityTypeIntegerMap -> {
                            entityTypeIntegerMap.forEach((entityType, integer) -> {
                                if (!ignoredType.contains(entityType)) {
                                    Integer orDefault = plotEntities.getOrDefault(entityType, 0);
                                    plotEntities.put(entityType, orDefault + integer);
                                }
                            });
                            synchronized (countLock) {
                                count--;
                            }

                            if (count == 0) {
                                plotsEntities.put(plotId, plotEntities);
                                int count = 0;
                                for (int value : plotEntities.values()) {
                                    count += value;
                                }
                                plotsEntityCount.put(plotId, count);
                            }
                        });
                    }
                }
            }
        }.run();

    }

    public static Map<String, Integer> getSnapshot(@NotNull IslandId islandId) {
        Map<EntityType, Integer> entityTypeIntegerMap = plotsEntities.get(islandId);
        if (entityTypeIntegerMap == null) {
            return null;
        }
        int total = plotsEntityCount.get(islandId);
        Map<String, Integer> map = new HashMap<>();
        ArrayList<EntityType> keys = new ArrayList<>(entityTypeIntegerMap.keySet());
        keys.sort((o1, o2) -> entityTypeIntegerMap.get(o2) - entityTypeIntegerMap.get(o1));
        for (EntityType key : keys) {
            String c = (IslandMobCap.map.get(key) != null
                    && IslandMobCap.map.get(key) <= entityTypeIntegerMap.get(key))
                    ? "c" : "a";

            String name = LangUtils.get(key.translationKey());
            map.put("§" + c + name, entityTypeIntegerMap.get(key));
        }
        map.put("§" + (total < 512 ? "a" : "c") + "总计", total);

        return map;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentPlot == null) {
            return true;
        }
        Map<EntityType, Integer> entityTypeIntegerMap = plotsEntities.get(currentPlot.getIslandId());
        if (entityTypeIntegerMap == null) {
            player.sendMessage("还在统计中, 稍后重试");
            return true;
        }
        int total = plotsEntityCount.get(currentPlot.getIslandId());
        ArrayList<EntityType> keys = new ArrayList<>(entityTypeIntegerMap.keySet());
        keys.sort((o1, o2) -> entityTypeIntegerMap.get(o2) - entityTypeIntegerMap.get(o1));
        player.sendMessage(String.format("§a>§e%s §" + (total < 512 ? "a" : "c") + "%s", "总计", total));
        for (int i = 0; i < 10 && i < keys.size(); i++) {
            String name = LangUtils.get(keys.get(i).translationKey());
            String c = (map.get(keys.get(i)) != null && map.get(keys.get(i)) <= entityTypeIntegerMap.get(keys.get(i))) ? "c" : "a";
            String message = String.format("§a>§e%s §" + c + "%s", name, entityTypeIntegerMap.get(keys.get(i)));
            player.sendMessage(message);
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
