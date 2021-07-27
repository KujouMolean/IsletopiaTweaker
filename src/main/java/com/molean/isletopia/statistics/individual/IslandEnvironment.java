package com.molean.isletopia.statistics.individual;

import com.destroystokyo.paper.event.block.BeaconEffectEvent;
import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.destroystokyo.paper.event.entity.EndermanEscapeEvent;
import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import com.destroystokyo.paper.event.entity.TurtleLayEggEvent;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.pojo.resp.CommonResponseObject;
import com.molean.isletopia.shared.utils.RedisUtils;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotId;
import io.papermc.paper.event.block.BellRingEvent;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;

import java.util.*;

public class IslandEnvironment implements Listener, CommandExecutor {

    private static final Map<PlotId, ResourceConsumeStatistics> resourceConsumeStatisticsMap = new HashMap<>();
    private static int TICK_SAMPLE_10 = 10;
    private static int TICK_SAMPLE_100 = 100;

    public static String getPowerUsageLevel(long powerUsagePerMinute){
        if(powerUsagePerMinute<10000){
            return "令人心情愉悦的程度";
        }
        int amplifier = (int) ((powerUsagePerMinute - 50000) / 10000);
        String level;
        switch (amplifier) {
            case 0:{
                level = "天空灰蒙蒙的程度";
                break;
            }
            case 1: {
                level = "空气有些污浊的程度";
                break;
            }
            case 2:{
                level = "呼吸疾病频发的程度";
                break;
            }
            case 3: {
                level = "北极冰川融化的程度";
                break;
            }
            case 4:{
                level = "令生物窒息的程度";
                break;
            }
            case 5: {
                level = "风不再流动的程度";
                break;
            }
            case 6:{
                level = "妖怪都嫌弃的程度";
                break;
            }
            case 7: {
                level = "妹红无法复活的程度";
                break;
            }
            default: {
                level = "莫良赶来删岛的程度";
            }
        }
        return level;
    }


    public static String getWaterUsageLevel(long waterUsagePerMinute){
        String level;
        if(waterUsagePerMinute<10000){
            return "水流清澈见底的程度";
        }

        int amplifier = (int) ((waterUsagePerMinute - 10000) / 5000);
        switch (amplifier) {
            case 0: {
                level = "雨水有点酸的程度";
                break;
            }
            case 1: {
                level = "水无法饮用的程度";
                break;
            }
            case 2:{
                level = "植物开始凋零的程度";
                break;
            }
            case 3: {
                level = "植物全部凋零的程度";
                break;
            }
            case 4:{
                level = "琪露诺无法结冰的程度";
                break;
            }
            case 5: {
                level = "生物无法存活的程度";
                break;
            }
            case 6: {
                level = "哥斯拉看了直摇头的程度";
                break;
            }
            case 7: {
                level = "妹红无法复活的程度";
                break;
            }
            default: {
                level = "莫良赶来删岛的程度";
            }
        }
        return level;
    }


    public static String getCachedWaterLevel(UUID owner){
        try (Jedis jedis = RedisUtils.getJedis()) {
            if(jedis.exists("WaterUsageLevel-"+owner)){
                return jedis.get("WaterUsageLevel-"+owner);
            }else{
                return "未知";
            }
        }
    }

    public static String getCachedPowerUsageLevel(UUID owner){
        try (Jedis jedis = RedisUtils.getJedis()) {
            if(jedis.exists("PowerUsageLevel-"+owner)){
                return jedis.get("PowerUsageLevel-"+owner);
            }else{
                return "未知";
            }
        }
    }
    public IslandEnvironment() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        Objects.requireNonNull(Bukkit.getPluginCommand("consume")).setExecutor(this);
        Random random = new Random();
        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), () -> {
            TICK_SAMPLE_10 = random.nextInt(20)+1;
            TICK_SAMPLE_100 = random.nextInt(200)+1;
        }, 20, 20);

        Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            for (PlotId plotId : resourceConsumeStatisticsMap.keySet()) {
                Plot plot = PlotUtils.getFirstPlotArea().getPlot(plotId);
                if(plot==null){
                    continue;
                }
                UUID owner = plot.getOwner();
                String waterUsageLevel = getWaterUsageLevel(getWaterUsagePerMinute(plotId));
                String powerUsageLevel = getPowerUsageLevel(getWaterUsagePerMinute(plotId));

                try (Jedis jedis = RedisUtils.getJedis()) {
                    jedis.set("WaterUsageLevel-" + owner, waterUsageLevel);
                    jedis.set("PowerUsageLevel-" + owner, powerUsageLevel);
                }
            }
        }, 60L, 60L);

        Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            if (ServerInfoUpdater.getOnlinePlayers().size() < 40) {
                return;
            }

            try (Jedis jedis = RedisUtils.getJedis()) {
                if (jedis.exists("Pollution-CoolDown")) {
                    return;
                }
            }

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Plot currentPlot = PlotUtils.getCurrentPlot(onlinePlayer);
                if (currentPlot == null) {
                    continue;
                }

                long waterUsagePerMinute = getWaterUsagePerMinute(currentPlot.getId());
                long powerUsagePerMinute = getPowerUsagePerMinute(currentPlot.getId());

                double p1 = 4.0 / Math.PI * Math.atan(1e-4 * waterUsagePerMinute) - 1;
                double p2 = 4.0 / Math.PI * Math.atan(2e-5 * powerUsagePerMinute) - 1;


                UUID owner = currentPlot.getOwner();
                if (owner == null) {
                    continue;
                }


                if (p1 > 0 && random.nextInt(10000) / 10000.0 < Math.pow(p1, 3)) {
                    String single = PlotSquared.get().getImpromptuUUIDPipeline().getSingle(owner, 1000L);
                    if (single == null) {
                        continue;
                    }
                    CommonResponseObject commonResponseObject = new CommonResponseObject();
                    commonResponseObject.setMessage("玩家 " + single + " 的岛屿存在水污染, 污染等级: " + getWaterUsageLevel(waterUsagePerMinute) + ".");

                    ServerMessageUtils.sendMessage("waterfall", "CommonResponse", commonResponseObject);
                    commonResponseObject.setMessage("环保局提醒大家, 节约用水, 爱护我们的大籽然.");
                    ServerMessageUtils.sendMessage("waterfall", "CommonResponse", commonResponseObject);
                    try (Jedis jedis = RedisUtils.getJedis()) {
                        jedis.setex("Pollution-CoolDown", 60 * 5L, "true");
                    }
                    return;

                }

                if (p2 > 0 && Math.pow(random.nextInt(10000) / 10000.0, 2) < Math.pow(p2, 3)) {
                    String single = PlotSquared.get().getImpromptuUUIDPipeline().getSingle(owner, 1000L);
                    if (single == null) {
                        continue;
                    }
                    CommonResponseObject commonResponseObject = new CommonResponseObject();
                    commonResponseObject.setMessage("玩家 " + single + " 的岛屿碳排超标, 排放等级: " + getPowerUsageLevel(powerUsagePerMinute) + ".");
                    ServerMessageUtils.sendMessage("waterfall", "CommonResponse", commonResponseObject);
                    commonResponseObject.setMessage("环保局提醒大家, 减少碳排放, 爱护环境就是爱护自己.");
                    ServerMessageUtils.sendMessage("waterfall", "CommonResponse", commonResponseObject);
                    try (Jedis jedis = RedisUtils.getJedis()) {
                        jedis.setex("Pollution-CoolDown", 120L, "true");
                    }
                    return;
                }

            }
        }, 60 * 20, 20 * 60);
    }

    public static ResourceConsume getResourceConsume(PlotId plotId) {
        if (resourceConsumeStatisticsMap.containsKey(plotId)) {
            return resourceConsumeStatisticsMap.get(plotId).getResourceConsume();
        }

        return new ResourceConsume(0);
    }

    private void addPowerUsage(Location location, String reason, int amount) {
        int plotX = Math.floorDiv(location.getBlockX(), 512) + 1;
        int plotZ = Math.floorDiv(location.getBlockZ(), 512) + 1;
        PlotId plotId = PlotId.of(plotX, plotZ);
        if (!resourceConsumeStatisticsMap.containsKey(plotId)) {
            resourceConsumeStatisticsMap.put(plotId, new ResourceConsumeStatistics());
        }
        ResourceConsumeStatistics resourceConsumeStatistics = resourceConsumeStatisticsMap.get(plotId);
        resourceConsumeStatistics.addPowerUsage(reason, (long) amount);
    }

    private void addWaterUsage(Location location, String reason, int amount) {
        int plotX = Math.floorDiv(location.getBlockX(), 512) + 1;
        int plotZ = Math.floorDiv(location.getBlockZ(), 512) + 1;
        PlotId plotId = PlotId.of(plotX, plotZ);
        if (!resourceConsumeStatisticsMap.containsKey(plotId)) {
            resourceConsumeStatisticsMap.put(plotId, new ResourceConsumeStatistics());
        }
        ResourceConsumeStatistics resourceConsumeStatistics = resourceConsumeStatisticsMap.get(plotId);
        resourceConsumeStatistics.addWaterUsage(reason, (long) amount);
    }

    private void addAirUsage(Location location, String reason, int amount) {
        int plotX = Math.floorDiv(location.getBlockX(), 512) + 1;
        int plotZ = Math.floorDiv(location.getBlockZ(), 512) + 1;
        PlotId plotId = PlotId.of(plotX, plotZ);
        if (!resourceConsumeStatisticsMap.containsKey(plotId)) {
            resourceConsumeStatisticsMap.put(plotId, new ResourceConsumeStatistics());
        }
        ResourceConsumeStatistics resourceConsumeStatistics = resourceConsumeStatisticsMap.get(plotId);
        resourceConsumeStatistics.addAirUsage(reason, (long) amount);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockDispenseEvent event) {
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 2 * 10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockRedstoneEvent event) {
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 5);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BeaconEffectEvent event) {
        addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 5);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockPistonExtendEvent event) {
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 15);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockPistonRetractEvent event) {
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 15);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(TNTPrimeEvent event) {
        addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 20);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(NotePlayEvent event) {
        addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 5);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockExplodeEvent event) {
        addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 20);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockShearEntityEvent event) {
        addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 2);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BrewEvent event) {
        addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 5);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BellRingEvent event) {
        addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 2);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockCookEvent event) {
        addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 2);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(FurnaceBurnEvent event) {
        addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(InventoryMoveItemEvent event) {
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_100 == 0) {
            addPowerUsage(Objects.requireNonNull(event.getSource().getLocation()), event.getEventName(), 10);
        }
    }

    //water
    @EventHandler(ignoreCancelled = true)
    public void on(BlockFromToEvent event) {
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            addWaterUsage(event.getBlock().getLocation(), event.getEventName(), 10);
        }
    }


    //entity
    @EventHandler(ignoreCancelled = true)
    public void on(EndermanEscapeEvent event) {
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            addAirUsage(event.getEntity().getLocation(), event.getEventName(), 10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(SlimeSplitEvent event) {
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            addAirUsage(event.getEntity().getLocation(), event.getEventName(), 10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(TurtleLayEggEvent event) {
        addAirUsage(event.getEntity().getLocation(), event.getEventName(), 10);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(EntitySpawnEvent event) {
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            addAirUsage(event.getEntity().getLocation(), event.getEventName(), 10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(EntityMoveEvent event) {
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_100 == 0) {
            addAirUsage(event.getEntity().getLocation(), event.getEventName(), 10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(EntityPathfindEvent event) {
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_100 == 0) {
            addAirUsage(event.getEntity().getLocation(), event.getEventName(), 10);
        }
    }


    public long getPowerUsagePerMinute(PlotId plotId) {
        ResourceConsume resourceConsume = getResourceConsume(plotId);
        Map<String, Long> powerUsageMap = resourceConsume.getPowerUsageMap();
        long power = 0;
        for (String s : powerUsageMap.keySet()) {
            Long aLong = powerUsageMap.get(s);
            power += aLong;
        }
        return power;
    }

    public long getWaterUsagePerMinute(PlotId plotId) {
        ResourceConsume resourceConsume = getResourceConsume(plotId);
        Map<String, Long> waterUsageMap = resourceConsume.getWaterUsageMap();
        long water = 0;
        for (String s : waterUsageMap.keySet()) {
            Long aLong = waterUsageMap.get(s);
            water += aLong;
        }
        return water;
    }

    public long getAirUsagePerMinute(PlotId plotId) {
        Plot currentPlot = PlotUtils.getFirstPlotArea().getPlot(plotId);
        assert currentPlot != null;
        ResourceConsume resourceConsume = getResourceConsume(plotId);
        Map<String, Long> airUsageMap = resourceConsume.getAirUsageMap();
        long air = 0;
        for (String s : airUsageMap.keySet()) {
            Long aLong = airUsageMap.get(s);
            air += aLong;
        }

        com.plotsquared.core.location.Location bot = currentPlot.getBottomAbs();
        com.plotsquared.core.location.Location top = currentPlot.getTopAbs();
        BoundingBox boundingBox = new BoundingBox(bot.getX(), bot.getY(), bot.getZ(), top.getX(), top.getY(), top.getZ());
        World world = Bukkit.getWorld(Objects.requireNonNull(currentPlot.getWorldName()));
        assert world != null;
        Collection<Entity> nearbyEntities = world.getNearbyEntities(boundingBox);
        for (Entity nearbyEntity : nearbyEntities) {
            if (nearbyEntity instanceof Animals) {
                air += 10;
            }
            if (nearbyEntity instanceof Monster) {
                air += 50;
            }
            if (nearbyEntity instanceof Villager) {
                air += 500;
            }
            if (nearbyEntity instanceof Minecart) {
                air += 20;
            }
        }
        return air;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        assert currentPlot != null;

        player.sendMessage("能源消耗: " + (getPowerUsagePerMinute(currentPlot.getId())) + "KJ/min");
        player.sendMessage("水源污染: " + (getWaterUsagePerMinute(currentPlot.getId())) + "t/min");
        player.sendMessage("生物负载: " + (getAirUsagePerMinute(currentPlot.getId())) + "/min");

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("detail")) {
                {
                    ResourceConsume resourceConsume = getResourceConsume(currentPlot.getId());
                    Map<String, Long> powerUsageMap = resourceConsume.getPowerUsageMap();
                    ArrayList<String> strings = new ArrayList<>(powerUsageMap.keySet());
                    strings.sort((o1, o2) -> (int) (powerUsageMap.get(o2) - powerUsageMap.get(o1)));
                    for (int i = 0; i < 10 && i < strings.size(); i++) {
                        player.sendMessage(strings.get(i) + " " + powerUsageMap.get(strings.get(i)));
                    }
                }
                {
                    ResourceConsume resourceConsume = getResourceConsume(currentPlot.getId());
                    Map<String, Long> airUsageMap = resourceConsume.getAirUsageMap();
                    ArrayList<String> strings = new ArrayList<>(airUsageMap.keySet());
                    strings.sort((o1, o2) -> (int) (airUsageMap.get(o2) - airUsageMap.get(o1)));
                    for (int i = 0; i < 10 && i < strings.size(); i++) {
                        player.sendMessage(strings.get(i) + " " + airUsageMap.get(strings.get(i)));
                    }
                }

            }
        }
        return true;
    }
}
