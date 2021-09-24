package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.Island;
import com.molean.isletopia.island.IslandId;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.shared.utils.RedisUtils;
import com.molean.isletopia.task.CustomTask;
import com.molean.isletopia.utils.LangUtils;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class IslandMobCap implements Listener, CommandExecutor, TabCompleter {

    private static final Map<EntityType, Integer> map = new HashMap<>();
    private static final List<EntityType> ignoredType = new ArrayList<>();
    private static final List<CreatureSpawnEvent.SpawnReason> ignoredReason = new ArrayList<>();
    private static final Map<IslandId, Map<EntityType, Integer>> plotsEntities = new ConcurrentHashMap<>();
    private static final Map<String, Map<EntityType, Integer>> unloadedLocalCache = new ConcurrentHashMap<>();
    private static final Map<String, Map<EntityType, Integer>> loadedChunkCache = new ConcurrentHashMap<>();
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
        setMobCap(EntityType.ZOMBIFIED_PIGLIN, 30);
        setMobCap(EntityType.PIGLIN, 30);
        setMobCap(EntityType.HOGLIN, 30);
        setMobCap(EntityType.ZOGLIN, 30);
        setMobCap(EntityType.MAGMA_CUBE, 30);
        setMobCap(EntityType.GHAST, 30);
        setMobCap(EntityType.STRIDER, 30);
        setMobCap(EntityType.GUARDIAN, 30);
        setMobCap(EntityType.SPIDER, 30);
        setMobCap(EntityType.COD, 30);
        setMobCap(EntityType.TROPICAL_FISH, 30);
        setMobCap(EntityType.SALMON, 30);
        setMobCap(EntityType.VILLAGER, 50);
        setMobCap(EntityType.MUSHROOM_COW, 15);
        ignoredType.add(EntityType.ITEM_FRAME);
        ignoredType.add(EntityType.GLOW_ITEM_FRAME);
        ignoredReason.add(CreatureSpawnEvent.SpawnReason.SLIME_SPLIT);
        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Island currentPlot = IslandManager.INSTANCE.getCurrentIsland(onlinePlayer);
                if (currentPlot != null) {
                    shouldUpdatePlot.add(currentPlot.getIslandId());
                }
            }

            Queue<Runnable> runnables = new ArrayDeque<>();

            for (IslandId plotId : shouldUpdatePlot) {
                for (int i = 0; i < 15; i++) {
                    runnables.add(() -> {
                        newCountEntities(plotId);
                    });
                }
            }

            new CustomTask(runnables, 20, null).run();

            shouldUpdatePlot.clear();
        }, 20L, 20L);
    }

    private static final Map<Island, Long> lastNotifyTimeMap = new HashMap<>();

    @EventHandler
    public void on(ChunkUnloadEvent event) {
        int x = event.getChunk().getX();
        int z = event.getChunk().getZ();
        String stringKey = "ChunkCount:" + ServerInfoUpdater.getServerName() + x + "," + z;
        byte[] key = stringKey.getBytes(StandardCharsets.UTF_8);

        try (Jedis jedis = RedisUtils.getJedis()) {
            HashMap<EntityType, Integer> entityTypeIntegerHashMap = new HashMap<>();
            for (Entity entity : event.getChunk().getEntities()) {
                int old = entityTypeIntegerHashMap.getOrDefault(entity.getType(), 0);
                entityTypeIntegerHashMap.put(entity.getType(), old + 1);
            }
            byte[] serialize = ObjectUtils.serialize(entityTypeIntegerHashMap);
            jedis.set(key, serialize);
            unloadedLocalCache.put(stringKey, entityTypeIntegerHashMap);
        }
    }

    public static Map<EntityType, Integer> countChunk(Jedis jedis, int x, int z) {

        World world = IsletopiaTweakers.getWorld();


        String stringKey = "ChunkCount:" + ServerInfoUpdater.getServerName() + x + "," + z;
        byte[] key = stringKey.getBytes(StandardCharsets.UTF_8);

        if (world.isChunkLoaded(x, z)) {
            HashMap<EntityType, Integer> entityTypeIntegerHashMap = new HashMap<>();
            // keep update if loaded
            Chunk chunkAt = world.getChunkAt(x, z);
            for (Entity entity : chunkAt.getEntities()) {
                int old = entityTypeIntegerHashMap.getOrDefault(entity.getType(), 0);
                entityTypeIntegerHashMap.put(entity.getType(), old + 1);
            }
            byte[] serialize = ObjectUtils.serialize(entityTypeIntegerHashMap);
            return entityTypeIntegerHashMap;
        } else {
            // try load from cache

            if (unloadedLocalCache.containsKey(stringKey)) {
                return unloadedLocalCache.get(stringKey);
            }

            if (jedis.exists(key)) {
                byte[] bytes = jedis.get(key);
                HashMap<EntityType, Integer> deserialize = (HashMap<EntityType, Integer>) ObjectUtils.deserialize(bytes);
                unloadedLocalCache.put(stringKey, deserialize);
                return deserialize;
            }

            Chunk chunkAt = world.getChunkAt(x, z);
            HashMap<EntityType, Integer> entityTypeIntegerHashMap = new HashMap<>();
            for (Entity entity : chunkAt.getEntities()) {
                int old = entityTypeIntegerHashMap.getOrDefault(entity.getType(), 0);
                entityTypeIntegerHashMap.put(entity.getType(), old + 1);
            }
            byte[] serialize = ObjectUtils.serialize(entityTypeIntegerHashMap);
            jedis.set(key, serialize);
            unloadedLocalCache.put(stringKey, entityTypeIntegerHashMap);
            return entityTypeIntegerHashMap;

        }
    }

    private static void warn(Island currentPlot) {
        Long lastNotifyTime = lastNotifyTimeMap.getOrDefault(currentPlot, 0L);
        if (System.currentTimeMillis() - lastNotifyTime > 15000) {
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

    @EventHandler
    public void onItem(ItemSpawnEvent event) {
        Item entity = event.getEntity();
        Location location = entity.getLocation();
        Island currentPlot = IslandManager.INSTANCE.getCurrentIsland(location);
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

    @EventHandler
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


    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        Island plot = IslandManager.INSTANCE.getCurrentIsland(event.getLocation());
        if (plot == null) {
            return;
        }
        if (ignoredReason.contains(event.getSpawnReason())) {
            return;
        }

        shouldUpdatePlot.add(plot.getIslandId());
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
            }
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


        try (Jedis jedis = RedisUtils.getJedis()) {
            for (int i = bx; i < ex; i++) {
                for (int j = bz; j < ez; j++) {
                    countChunk(jedis, i, j).forEach((entityType, integer) -> {
                        if (!ignoredType.contains(entityType)) {
                            Integer orDefault = plotEntities.getOrDefault(entityType, 0);
                            plotEntities.put(entityType, orDefault + integer);
                        }
                    });

                }
            }
        }

        plotsEntities.put(plotId, plotEntities);
        int count = 0;
        for (int value : plotEntities.values()) {
            count += value;
        }
        plotsEntityCount.put(plotId, count);

    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Island currentPlot = IslandManager.INSTANCE.getCurrentIsland(player);
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
            @SuppressWarnings("deprecation")
            String internalName = keys.get(i).getName();
            String name;
            if (internalName != null) {
                name = LangUtils.get("entity.minecraft." + internalName.toLowerCase());
            } else {
                name = "未知";
            }
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
