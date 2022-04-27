package com.molean.isletopia.island.flag;

import com.molean.isletopia.event.PlayerIslandChangeEvent;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.shared.model.Island;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.IsletopiaTweakersUtils;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.PluginUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.List;

public class PowerOff implements IslandFlagHandler, Listener {

    public PowerOff() {
        PluginUtils.registerEvents(this);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockRedstoneEvent event) {
        Location location = event.getBlock().getLocation();
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(location);
        if (currentIsland == null) {
            return;
        }
        if (!currentIsland.containsFlag(getKey())) {
            return;
        }
        event.setNewCurrent(0);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(CreatureSpawnEvent event) {
        Location location = event.getLocation();
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(location);
        if (currentIsland == null) {
            return;
        }
        if (!currentIsland.containsFlag(getKey())) {
            return;
        }
        event.setCancelled(true);
    }


    @Override
    public void onFlagAdd(LocalIsland island, String... data) {
        outer:
        for (Player player : island.getPlayersInIsland()) {
            MessageUtils.strong(player, "该岛屿能源欠费无法进入！");
            if (player.isOp()) {

                continue;
            }
            List<Island> playerIslands = IslandManager.INSTANCE.getPlayerIslands(player.getUniqueId());
            for (Island playerIsland : playerIslands) {
                if (!playerIsland.containsFlag(getKey())) {
                    IsletopiaTweakersUtils.universalPlotVisitByMessage(player, playerIsland.getIslandId());
                    continue outer;
                }

            }
            Tasks.INSTANCE.sync(() -> {
                player.kick(Component.text("你的所有岛屿都能源欠费了，禁止进入服务器!"));
            });
        }

    }


    @EventHandler
    public void on(PlayerIslandChangeEvent event) {
        LocalIsland to = event.getTo();
        Player player = event.getPlayer();
        if (to != null && to.containsFlag(getKey())) {
            if (event.getPlayer().isOp()) {
                MessageUtils.strong(player, "该岛屿能源欠费无法进入！");
                return;
            }
            event.setCancelled(true);
        }
    }

}
