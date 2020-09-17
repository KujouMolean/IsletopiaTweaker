package com.molean.isletopia.story.action;

import com.molean.isletopia.story.SceneManager;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Scene implements Action {

    private final String name;
    private final String namespace;
    private final int n;

    public Scene(String name, String namespace, int n) {
        this.name = name;
        this.namespace = namespace;
        this.n = n;
        SceneManager.registScene(namespace);
    }

    @Override
    public void play(Player player) {
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        if (currentPlot == null) {
            Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                player.kickPlayer("严重错误, 在非岛屿位置.");
            });
            return;
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(currentPlot.getOwner());
        String name = offlinePlayer.getName();
        Location location = SceneManager.getScene(name, namespace, n);
        if (location == null) {
            player.sendTitle(name + "(未设置)", "", 0, 0, 40);
        } else {
            location.setWorld(player.getWorld());
            player.teleport(location);
            player.sendTitle(name, "", 0, 0, 40);
        }
    }

    @Override
    public String toString() {
        return "Scene{" +
                "name='" + name + '\'' +
                ", namespace='" + namespace + '\'' +
                ", n=" + n +
                '}';
    }
}
