package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.model.IslandId;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

@Singleton
public class VillagerLimiter implements Listener {
    public VillagerLimiter() {
        Tasks.INSTANCE.interval(60 * 20, () -> {
            long start = System.currentTimeMillis();
            HashSet<Villager> tobeActivate = new HashSet<>();
            HashSet<Villager> tobeFreeze = new HashSet<>();
            HashMap<IslandId, Integer> activeVillagerNumbers = new HashMap<>();
            ArrayList<Villager> entitiesByClass = new ArrayList<>();
            for (World world : Bukkit.getWorlds()) {
                entitiesByClass.addAll(world.getEntitiesByClass(Villager.class));
            }

            for (Villager villager : entitiesByClass) {
                Location location = villager.getLocation();
                IslandId islandId = IslandId.fromLocation(ServerInfoUpdater.getServerName(), location.getBlockX(), location.getBlockZ());
                Integer activeVillagerNumber = activeVillagerNumbers.getOrDefault(islandId, 0);
                if (villager.hasAI()) {
                    activeVillagerNumbers.put(islandId, activeVillagerNumber + 1);
                }
            }
            for (Villager villager : entitiesByClass) {
                Location location = villager.getLocation();
                IslandId islandId = IslandId.fromLocation(ServerInfoUpdater.getServerName(), location.getBlockX(), location.getBlockZ());
                Integer activeVillagerNumber = activeVillagerNumbers.getOrDefault(islandId, 0);
                if (villager.hasAI()) {
                    if (activeVillagerNumber > 64) {
                        //remove ai
                        tobeFreeze.add(villager);
                        activeVillagerNumbers.put(islandId, activeVillagerNumber - 1);
                    }
                } else {
                    if (activeVillagerNumber < 64) {
                        tobeActivate.add(villager);
                        activeVillagerNumbers.put(islandId, activeVillagerNumber + 1);
                    }
                }
            }
            for (Villager villager : tobeActivate) {
                villager.setAI(true);
            }
            for (Villager villager : tobeFreeze) {
                villager.setAI(false);
            }
            long l = System.currentTimeMillis();
            if (tobeActivate.size() > 0 || tobeFreeze.size() > 0) {
                PluginUtils.getLogger().info("");
            }
            PluginUtils.getLogger().info("Villager ai check used " + (l - start) + "ms");
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Villager villager) {
            if (!villager.hasAI()) {
                MessageUtils.info(event.getPlayer(), "villager.ai");
            }
        }
    }
}
