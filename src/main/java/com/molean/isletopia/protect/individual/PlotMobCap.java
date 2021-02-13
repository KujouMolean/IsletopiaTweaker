package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.LangUtils;
import com.molean.isletopia.utils.Pair;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.location.Location;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
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
        ignoredReason.add(CreatureSpawnEvent.SpawnReason.SLIME_SPLIT);

    }


    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        PlotArea plotAreaAbs = PlotSquared.get().getPlotAreaAbs(BukkitUtil.getLocation(event.getLocation()));
        Plot plot = plotAreaAbs.getPlotAbs(BukkitUtil.getLocation(event.getLocation()));
        if (plot == null) {
            return;
        }
        if (ignoredReason.contains(event.getSpawnReason())) {
            return;
        }
        if (countEntities(plot, null) >= 512) {
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
        World world = BukkitUtil.getWorld(area.getWorldName());

        Location bot = plot.getBottomAbs();
        Location top = plot.getTopAbs();
        int bx = bot.getX() >> 4;
        int bz = bot.getZ() >> 4;

        int tx = top.getX() >> 4;
        int tz = top.getZ() >> 4;

        Set<Chunk> chunks = new HashSet<>();
        for (int X = bx; X <= tx; X++) {
            for (int Z = bz; Z <= tz; Z++) {
                if (world.isChunkLoaded(X, Z)) {
                    chunks.add(world.getChunkAt(X, Z));
                }
            }
        }

        for (Chunk chunk : chunks) {
            for (Entity chunkEntity : chunk.getEntities()) {
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
        ArrayList<Pair<EntityType, Integer>> pairs = new ArrayList<>();

        int total = countEntities(currentPlot, null);
        for (EntityType entityType : EntityType.values()) {
            if (ignoredType.contains(entityType)) {
                continue;
            }
            int count = countEntities(currentPlot, entityType);
            if (count == 0) {
                continue;
            }
            pairs.add(new Pair<>(entityType, count));
        }

        pairs.sort((o1, o2) -> o2.getValue() - o1.getValue());
        player.sendMessage(String.format("§a>§e%s §" + (total < 512 ? "a" : "c") + "%s", "总计", total));
        for (int i = 0; i < 10 && i < pairs.size(); i++) {
            Pair<EntityType, Integer> pair = pairs.get(i);
            String internalName = pair.getKey().getName();
            String name;
            if (internalName != null) {
                name = LangUtils.get("entity.minecraft." + internalName.toLowerCase());
            } else {
                name = "未知";
            }
            String c = (map.get(pair.getKey()) != null && map.get(pair.getKey()) <= pair.getValue()) ? "c" : "a";
            String message = String.format("§a>§e%s §" + c + "%s", name, pair.getValue());
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
