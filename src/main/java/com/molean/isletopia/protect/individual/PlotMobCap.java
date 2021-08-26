package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.task.PlotChunkTask;
import com.molean.isletopia.task.PlotLoadedChunkTask;
import com.molean.isletopia.utils.LangUtils;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.location.Location;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.PlotId;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Bukkit;
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
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlotMobCap implements Listener, CommandExecutor, TabCompleter {

    private static final Map<EntityType, Integer> map = new HashMap<>();
    private static final List<EntityType> ignoredType = new ArrayList<>();
    private static final List<CreatureSpawnEvent.SpawnReason> ignoredReason = new ArrayList<>();
    private static final Map<PlotId, Map<EntityType, Integer>> plotsEntities = new ConcurrentHashMap<>();
    private static final Map<PlotId, Integer> plotsEntityCount = new ConcurrentHashMap<>();


    private static final Set<PlotId> shouldUpdatePlot = new HashSet<>();


    public static void setMobCap(EntityType entityType, Integer integer) {
        map.put(entityType, integer);
    }

    public static void unsetMobCap(EntityType entityType) {
        map.remove(entityType);
    }

    public PlotMobCap() {
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
                Plot currentPlot = PlotUtils.getCurrentPlot(onlinePlayer);
                if (currentPlot != null) {
                    shouldUpdatePlot.add(currentPlot.getId());
                }
            }
            for (PlotId plotId : shouldUpdatePlot) {
                newCountEntities(plotId);
            }
            shouldUpdatePlot.clear();
        }, 20L, 20L);
    }

    private static final Map<Plot, Long> lastNotifyTimeMap = new HashMap<>();

    private static void warn(Plot currentPlot) {
        Long lastNotifyTime = lastNotifyTimeMap.getOrDefault(currentPlot, 0L);
        if (System.currentTimeMillis() - lastNotifyTime > 15000) {
            lastNotifyTimeMap.put(currentPlot, System.currentTimeMillis());
            List<PlotPlayer<?>> playersInPlot = currentPlot.getPlayersInPlot();
            for (PlotPlayer<?> plotPlayer : playersInPlot) {
                Player player = Bukkit.getPlayer(plotPlayer.getUUID());
                if (player == null || !PlotUtils.hasCurrentPlotPermission(player)) {
                    continue;
                }
                player.sendMessage("§8[§c危险警告§8] §e此岛屿实体已达上限, 实体无法再继续生成, 掉落物品可能会消失.");
            }
        }
    }

    @EventHandler
    public void onItem(ItemSpawnEvent event) {
        Item entity = event.getEntity();
        org.bukkit.Location location = entity.getLocation();
        Plot currentPlot = PlotUtils.getCurrentPlot(location);
        if (!plotsEntityCount.containsKey(currentPlot.getId())) {
            return;
        }
        int count = plotsEntityCount.get(currentPlot.getId());
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
    public void on(EntityMoveEvent event) {
        if (event.getEntityType().equals(EntityType.PLAYER)) {
            return;
        }
        org.bukkit.Location from = event.getFrom();
        org.bukkit.Location to = event.getTo();
        int plotFromX = Math.floorDiv(from.getBlockX(), 512) + 1;
        int plotFromZ = Math.floorDiv(from.getBlockZ(), 512) + 1;
        int plotToX = Math.floorDiv(to.getBlockX(), 512) + 1;
        int plotToZ = Math.floorDiv(to.getBlockZ(), 512) + 1;
        if (plotFromX != plotToX || plotFromZ != plotToZ) {
            event.getEntity().remove();
        }
    }
    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        PlotArea plotArea = PlotUtils.getFirstPlotArea();
        Plot plot = plotArea.getPlot(PlotUtils.fromBukkitLocation(event.getLocation()));
        if (plot == null) {
            return;
        }
        if (ignoredReason.contains(event.getSpawnReason())) {
            return;
        }

        shouldUpdatePlot.add(plot.getId());
        Map<EntityType, Integer> entityTypeIntegerMap = plotsEntities.get(plot.getId());
        if (entityTypeIntegerMap == null) {
            return;
        }
        if (plotsEntityCount.get(plot.getId()) >= 512) {
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
    public static void newCountEntities(PlotId plotId) {
        Plot plot = PlotUtils.getFirstPlotArea().getPlot(plotId);
        assert plot != null;
        HashMap<EntityType, Integer> plotEntities = new HashMap<>();

        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            new PlotLoadedChunkTask(plot, chunk -> {
                @NotNull Entity[] entities = chunk.getEntities();
                for (Entity entity : entities) {
                    if (!ignoredType.contains(entity.getType())) {
                        Integer orDefault = plotEntities.getOrDefault(entity.getType(), 0);
                        plotEntities.put(entity.getType(), orDefault + 1);
                    }
                }
            }, () -> {
                plotsEntities.put(plotId, plotEntities);
                int count = 0;
                for (int value : plotEntities.values()) {
                    count += value;
                }
                plotsEntityCount.put(plotId, count);
            }, 20).run();
        });

    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        if (currentPlot == null) {
            return true;
        }
        Map<EntityType, Integer> entityTypeIntegerMap = plotsEntities.get(currentPlot.getId());
        if (entityTypeIntegerMap == null) {
            player.sendMessage("还在统计中, 稍后重试");
            return true;
        }
        int total = plotsEntityCount.get(currentPlot.getId());
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
