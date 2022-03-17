package com.molean.isletopia.island.flag;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.task.PlotChunkTask;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DisableVillagerAI implements IslandFlagHandler, Listener {

    public DisableVillagerAI() {
        PluginUtils.registerEvents(this);
    }

    @EventHandler
    public void on(EntityAddToWorldEvent event) {
        Entity entity = event.getEntity();
        if (!entity.getType().equals(EntityType.VILLAGER)) {
            return;
        }
        Villager villager = (Villager) entity;
        Location location = event.getEntity().getLocation();
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(location);
        if (currentIsland == null) {
            return;
        }
        if (currentIsland.containsFlag(this.getKey())) {
            villager.setAI(false);
        } else {
            villager.setAI(true);
        }
    }

    @Override
    public void onFlagAdd(LocalIsland island, String... data) {
        new PlotChunkTask(island, chunk -> {
            for (Entity entity : chunk.getEntities()) {
                if (entity.getType().equals(EntityType.VILLAGER)) {
                    Villager villager = (Villager) entity;
                    villager.setAI(false);
                }
            }
        }, () -> {
//            for (Player player : island.getPlayersInIsland()) {
//                MessageUtils.notify(player, "此岛屿的村民失去了AI。");
//            }
        }, 120).run();


    }

    @Override
    public void onFlagRemove(LocalIsland island, String... data) {
        new PlotChunkTask(island, chunk -> {
            for (Entity entity : chunk.getEntities()) {
                if (entity.getType().equals(EntityType.VILLAGER)) {
                    Villager villager = (Villager) entity;
                    villager.setAI(true);
                }
            }
        }, () -> {
//            for (Player player : island.getPlayersInIsland()) {
//                MessageUtils.notify(player, "此岛屿的村民已恢复AI。");
//            }
        }, 120).run();
    }
}
