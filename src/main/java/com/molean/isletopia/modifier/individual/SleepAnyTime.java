package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
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
import org.bukkit.event.world.TimeSkipEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SleepAnyTime implements Listener, CommandExecutor {
    private final Set<Player> sleepingPlayers = new HashSet<>();
    private final Map<Player, Integer> sleepTickTimeMap = new HashMap<>();
    private final Map<Player, Long> sleepDayTimeMap = new HashMap<>();
    private final World world;

    public SleepAnyTime() {
        world = Bukkit.getWorld("SkyWorld");
        assert world != null;
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        Objects.requireNonNull(Bukkit.getPluginCommand("getup")).setExecutor(this);
        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), () -> {
            sleepingPlayers.removeIf(player -> !player.isOnline());
            int size = sleepingPlayers.size();
            if (size > 0) {
                world.setTime(world.getTime() + size);
            }
        }, 0, 1);

        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), () -> {
            sleepingPlayers.removeIf(player -> !player.isOnline());
            int size = sleepingPlayers.size();
            world.setGameRule(GameRule.RANDOM_TICK_SPEED, 3 * (1 + size));
        }, 0, 20);
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
                event.getPlayer().sendMessage(Component.text("§8[§3温馨提示§8] §e睡眠将加快时间流速(+100%), 不会跳过夜晚."));
                event.getPlayer().sendMessage(Component.text("§8[§3温馨提示§8] §c使用指令 /getup 起床, 点起床按钮无效!"));
            }
        }
    }

    @EventHandler
    public void on(PlayerBedLeaveEvent event) {
        if (sleepingPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
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
            player.sendMessage("§8[§3温馨提示§8] §e早上好, 你实际睡眠" + sleepTicks + "gt, 总共过去了" + sleepDayTime + "gt.");
            player.sendMessage("§8[§3温馨提示§8] §e昨晚的睡眠质量" + (sleepTicks % 2 == 0 ? "很不错" : "很差") + ".");
        }
        return true;
    }
}
