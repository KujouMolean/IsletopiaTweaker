package com.molean.isletopia.infrastructure.individual.bars;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.shared.model.IslandId;
import com.molean.isletopia.shared.utils.LangUtils;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.ScoreboardUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ProductionBar implements Listener, CommandExecutor, TabCompleter {
    private static final Map<IslandId, Map<Material, Deque<Long>>> map = new HashMap<>();
    private static final Map<IslandId, Map<Material, Integer>> maxMap = new HashMap<>();


    public ProductionBar() {
        Objects.requireNonNull(Bukkit.getPluginCommand("productionbar")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("productionbar")).setTabCompleter(this);
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if ("ProductionBar".equalsIgnoreCase(SidebarManager.INSTANCE.getSidebar(player.getUniqueId()))) {
                    update(player);
                }
            }
        }, 20, 20);
        IsletopiaTweakers.addDisableTask("Stop update production bars", bukkitTask::cancel);
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
            stringIntegerHashMap.put(LangUtils.get(player.locale(),material.translationKey()), integer);
        });

        stringIntegerHashMap.put(MessageUtils.getMessage(player, "player.bar.production.total"), total);
        String message = MessageUtils.getMessage(player, "player.bar.production");
        ScoreboardUtils.setPlayerUniqueSidebar(player, Component.text(message), stringIntegerHashMap);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(ItemSpawnEvent event) {
        Item entity = event.getEntity();
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(event.getLocation());
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


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        UUID uuid = player.getUniqueId();

        if (!"ProductionBar".equalsIgnoreCase(SidebarManager.INSTANCE.getSidebar(uuid))) {
            ScoreboardUtils.clearPlayerUniqueSidebar(player);
            SidebarManager.INSTANCE.setSidebar(uuid, "ProductionBar");
        } else {
            SidebarManager.INSTANCE.setSidebar(uuid, null);
            ScoreboardUtils.clearPlayerUniqueSidebar(player);
        }


        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return List.of();
    }
}
