package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.MessageUtils;
import io.papermc.paper.event.player.PlayerDeepSleepEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SleepAnyTime implements Listener, CommandExecutor {
    private final Set<Player> sleepingPlayers = new HashSet<>();
    private final Map<Player, Integer> sleepTickTimeMap = new HashMap<>();
    private final Map<Player, Long> sleepDayTimeMap = new HashMap<>();
    private final World world;

    public SleepAnyTime() {
        world = IsletopiaTweakers.getWorld();
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        Objects.requireNonNull(Bukkit.getPluginCommand("getup")).setExecutor(this);
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), () -> {
            sleepingPlayers.removeIf(player -> !player.isOnline());
            int size = sleepingPlayers.size();
            if (size > 0) {
                world.setTime(world.getTime() + size);
            }
        }, 0, 1);
        IsletopiaTweakers.addDisableTask("Stop skip world time for sleep player..", bukkitTask::cancel);

        BukkitTask bukkitTask1 = Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), () -> {
            sleepingPlayers.removeIf(player -> !player.isOnline());
            int size = sleepingPlayers.size();
            world.setGameRule(GameRule.RANDOM_TICK_SPEED, 3 * (1 + size));
        }, 0, 20);

        IsletopiaTweakers.addDisableTask("Stop update random tick speed for sleep player..", bukkitTask1::cancel);

    }


    @EventHandler
    public void on(PlayerBedEnterEvent event) {
        PlayerBedEnterEvent.BedEnterResult bedEnterResult = event.getBedEnterResult();
        if (event.getPlayer().isSleeping()) {
            return;
        }
        switch (bedEnterResult) {
            case NOT_POSSIBLE_NOW -> {
                event.getPlayer().sleep(event.getBed().getLocation(), true);
                event.setCancelled(true);
            }
            case OK -> {
                sleepingPlayers.add(event.getPlayer());
                sleepTickTimeMap.put(event.getPlayer(), Bukkit.getCurrentTick());
                sleepDayTimeMap.put(event.getPlayer(), world.getFullTime());
                MessageUtils.notify(event.getPlayer(), "睡眠将加快时间流速(+100%), 不会跳过夜晚.");
                MessageUtils.strong(event.getPlayer(), "使用指令 /getup 起床, 点起床按钮无效!");

            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerBedLeaveEvent event) {
        if (sleepingPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }



    @EventHandler(ignoreCancelled = true)
    public void on(TimeSkipEvent event) {
        if (event.getSkipReason().equals(TimeSkipEvent.SkipReason.NIGHT_SKIP)) {
            event.setCancelled(true);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        sleepingPlayers.remove(player);
        if (player.isSleeping()) {
            player.teleport(Objects.requireNonNull(player.getBedSpawnLocation()));
            int sleepTicks = Bukkit.getCurrentTick() - sleepTickTimeMap.get(player);
            long sleepDayTime = world.getFullTime() - sleepDayTimeMap.get(player);
            MessageUtils.info(player,"早上好, 你实际睡眠" + sleepTicks + "gt, 总共过去了" + sleepDayTime + "gt.");
            MessageUtils.info(player, "昨晚的睡眠质量" + (sleepTicks % 2 == 0 ? "很不错" : "很差") + ".");
        }
        return true;
    }
}
