package com.molean.isletopia.bars;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.molean.isletopia.annotations.Interval;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.shared.model.IslandId;
import com.molean.isletopia.shared.utils.LangUtils;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.ScoreboardUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@CommandAlias("ProductionBar")
@Singleton
public class ProductionBar extends BaseCommand implements Listener {
    private static final Map<IslandId, Map<Material, Deque<Long>>> map = new HashMap<>();
    private static final Map<IslandId, Map<Material, Integer>> maxMap = new HashMap<>();


    private final SidebarManager sidebarManager;

    public ProductionBar(SidebarManager sidebarManager) {
        this.sidebarManager = sidebarManager;
    }

    @Interval(value = 20,async = true)
    public void productionBarUpdate() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if ("ProductionBar".equalsIgnoreCase(sidebarManager.getSidebar(player))) {
                update(player);
            }
        }
    }


    public static void update(Player player) {
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null) {
            ScoreboardUtils.clearPlayerUniqueSidebar(player);
            return;
        }
        Map<Material, Integer> productionPerMin = productionPerMin(currentIsland.getIslandId());
        int total = 0;
        for (Integer value : productionPerMin.values()) {
            total += value;
        }

        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        productionPerMin.forEach((material, integer) -> {
            stringIntegerHashMap.put(LangUtils.get(player.locale(), material.translationKey()), integer);
        });

        stringIntegerHashMap.put(MessageUtils.getMessage(player, "player.bar.production.total"), total);
        String message = MessageUtils.getMessage(player, "player.bar.production");
        Tasks.INSTANCE.sync(() -> {
            ScoreboardUtils.updateOrCreatePlayerUniqueSidebar(player, Component.text(message), stringIntegerHashMap);
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void on(ItemSpawnEvent event) {
        Item entity = event.getEntity();
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIslandIfLoaded(event.getLocation());
        if (currentIsland == null) {
            return;
        }
        IslandId islandId = currentIsland.getIslandId();
        if (!map.containsKey(islandId)) {
            map.put(islandId, new HashMap<>());
        }
        ItemStack itemStack = entity.getItemStack();
        Map<Material, Deque<Long>> currentMap = map.get(islandId);
        if (!currentMap.containsKey(itemStack.getType())) {
            currentMap.put(itemStack.getType(), new LinkedList<>());
        }
        Deque<Long> deque = currentMap.get(itemStack.getType());
        long l = System.currentTimeMillis();
        for (int i = 0; i < itemStack.getAmount(); i++) {
            deque.add(l);
        }
    }

    public static Map<Material, Integer> productionPerMin(IslandId islandId) {
        Map<Material, Deque<Long>> materialDequeMap = map.get(islandId);
        HashMap<Material, Integer> materialIntegerHashMap = new HashMap<>();
        if (materialDequeMap == null) {
            return materialIntegerHashMap;
        }
        long l = System.currentTimeMillis() - 60 * 1000;

        if (!maxMap.containsKey(islandId)) {
            maxMap.put(islandId, new HashMap<>());
        }
        Map<Material, Integer> maxMapPerIsland = maxMap.get(islandId);
        HashSet<Material> tobeRemove = new HashSet<>();
        materialDequeMap.forEach((material, longs) -> {
            while (!longs.isEmpty() && longs.getFirst() < l) {
                longs.removeFirst();
            }
            if (longs.isEmpty()) {
                tobeRemove.add(material);
                return;
            }
            Integer max = maxMapPerIsland.getOrDefault(material, 0);
            maxMapPerIsland.put(material, Math.max(longs.size(), max));
            materialIntegerHashMap.put(material, longs.size());
        });
        for (Material material : tobeRemove) {
            materialDequeMap.remove(material);
        }

        return materialIntegerHashMap;
    }


    @Default
    public void onDefault(Player player) {
        if (!"ProductionBar".equalsIgnoreCase(sidebarManager.getSidebar(player))) {
            ScoreboardUtils.clearPlayerUniqueSidebar(player);
            Tasks.INSTANCE.async(() -> {
                sidebarManager.setSidebar(player, "ProductionBar");
            });
            Tasks.INSTANCE.async(() -> {

            });
        } else {
            sidebarManager.setSidebar(player, null);
            ScoreboardUtils.clearPlayerUniqueSidebar(player);
        }


    }

}
