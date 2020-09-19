package com.molean.isletopia.story.action;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.story.PlayerScene;
import com.molean.isletopia.story.SceneManager;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TeleportScene implements Action {

    private final String namespace;
    private final String name;
    private final String display;

    public TeleportScene(String namespace, String name, String display) {
        this.namespace = namespace;
        this.name = name;
        this.display = display;
        SceneManager.registerScene(namespace, name);
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
        PlayerScene scene = SceneManager.getScene(offlinePlayer.getName(), namespace, name);
        if (scene == null) {
            player.sendTitle(display + "(未设置)", namespace + ":" + name, 0, 40, 0);
            player.sendMessage("岛主输入`/scene set " + namespace + " " + name + "` 为\"" + display + "\"设置自定义场景.");
        } else {
            Location location = new Location(player.getWorld(), scene.getX(), scene.getY(), scene.getZ());
            Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                player.teleport(location);
            });

            player.sendTitle(display, namespace + ":" + name, 0, 40, 0);
        }
    }

}
