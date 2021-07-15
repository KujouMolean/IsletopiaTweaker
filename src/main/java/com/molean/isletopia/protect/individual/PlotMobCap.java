package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.LangUtils;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.location.Location;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.world.PlotAreaManager;
import org.bukkit.Bukkit;
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
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PlotMobCap implements Listener, CommandExecutor, TabCompleter {

    private static final Map<EntityType, Integer> map = new HashMap<>();
    private static final List<EntityType> ignoredType = new ArrayList<>();
    private static final List<CreatureSpawnEvent.SpawnReason> ignoredReason = new ArrayList<>();

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
        setMobCap(EntityType.VILLAGER, 100);
        setMobCap(EntityType.MUSHROOM_COW, 15);

        ignoredType.add(EntityType.ITEM_FRAME);
        ignoredType.add(EntityType.GLOW_ITEM_FRAME);

        ignoredReason.add(CreatureSpawnEvent.SpawnReason.SLIME_SPLIT);

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
        if (countEntities(currentPlot, null) >= 1024) {
            event.setCancelled(true);
        }
        if (countEntities(currentPlot, null) >= 512) {
            warn(currentPlot);
        }
    }


    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        PlotAreaManager plotAreaManager = PlotSquared.get().getPlotAreaManager();
        PlotArea plotArea = plotAreaManager.getPlotArea(PlotUtils.fromBukkitLocation(event.getLocation()));
        Plot plot = plotArea.getPlot(PlotUtils.fromBukkitLocation(event.getLocation()));
        if (plot == null) {
            return;
        }
        if (ignoredReason.contains(event.getSpawnReason())) {
            return;
        }
        if (countEntities(plot, null) >= 512) {
            warn(plot);
            event.setCancelled(true);
            return;
        }
        EntityType entityType = event.getEntity().getType();
        if (map.containsKey(entityType)) {
            if (countEntities(plot, entityType) >= map.get(entityType)) {
                event.setCancelled(true);
            }
        }
    }

    public static int countEntities(@NotNull Plot plot, @Nullable EntityType entityType) {
        String typeString;
        if (entityType != null) {
            typeString = entityType.toString();
        } else {
            typeString = "all";
        }
        Integer prevCount = (Integer) plot.getMeta("Isletopia-Cap-" + typeString);
        Long prevTime = (Long) plot.getMeta("Isletopia-Cap" + typeString + "-Time");
        if (prevTime != null && System.currentTimeMillis() - prevTime < 1e3 && prevCount != null) {
            return prevCount;
        }
        int count = 0;
        PlotArea area = plot.getArea();
        assert area != null;
        World world = BukkitUtil.getWorld(area.getWorldName());
        assert world != null;
        Location bot = plot.getBottomAbs();
        Location top = plot.getTopAbs();
        BoundingBox boundingBox = new BoundingBox(bot.getX(), bot.getY(), bot.getZ(), top.getX(), top.getY(), top.getZ());
        Collection<Entity> nearbyEntities = world.getNearbyEntities(boundingBox);
        for (Entity chunkEntity : nearbyEntities) {
            if (entityType != null) {
                if (chunkEntity.getType() == entityType) {
                    count++;
                }
            } else {
                if (!ignoredType.contains(chunkEntity.getType())) {
                    count++;
                }
            }
        }
        plot.setMeta("Isletopia-Cap-" + typeString, count);
        plot.setMeta("Isletopia-Cap" + typeString + "-Time", System.currentTimeMillis());
        return count;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        if (currentPlot == null) {
            return true;
        }

        Location bot = currentPlot.getBottomAbs();
        Location top = currentPlot.getTopAbs();
        BoundingBox boundingBox = new BoundingBox(bot.getX(), bot.getY(), bot.getZ(), top.getX(), top.getY(), top.getZ());
        Collection<Entity> nearbyEntities = player.getWorld().getNearbyEntities(boundingBox);


        Map<EntityType, Integer> map = new HashMap<>();

        int total=0;

        for (Entity nearbyEntity : nearbyEntities) {
            EntityType type = nearbyEntity.getType();
            if (ignoredType.contains(type)) {
                continue;
            }
            map.put(type, map.getOrDefault(type, 0) + 1);
            total++;
        }

        ArrayList<EntityType> keys = new ArrayList<>(map.keySet());

        keys.sort((o1, o2) -> map.getOrDefault(o2, 0) - map.getOrDefault(o1, 0));
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
            String c = (PlotMobCap.map.get(keys.get(i)) != null && PlotMobCap.map.get(keys.get(i)) <= map.get(keys.get(i))) ? "c" : "a";
            String message = String.format("§a>§e%s §" + c + "%s", name, map.get(keys.get(i)));
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
