package com.molean.isletopia.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ScoreboardUtils {

    public static void clearGlobalSidebar() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        clearSidebar(scoreboard);
    }

    public static void clearSidebar(Scoreboard scoreboard) {
        Objective sidebar = scoreboard.getObjective("sidebar");
        if (sidebar != null) {
            sidebar.unregister();
        }
    }

    public static void setGlobalSideBar(Component title, List<String> list) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            map.put(list.get(i), list.size() - i);
        }
        setGlobalSideBar(title, map);
    }

    private static void setSideBar(Scoreboard scoreboard, Component title, Map<String, Integer> map) {
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
        if (title == null || map == null) {
            return;
        }
        Objective sidebar = scoreboard.getObjective("sidebar");
        if (sidebar == null) {
            sidebar = scoreboard.registerNewObjective("sidebar", "dummy", title);
        }
        sidebar.displayName(title);
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }
        for (String s : map.keySet()) {
            sidebar.getScore("§b► §r" + s).setScore(map.get(s));
        }
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    private static void updateSideBar(Scoreboard scoreboard, Component title, Map<String, Integer> map) {
        if (title == null || map == null) {
            return;
        }
        HashSet<String> strings = new HashSet<>();
        for (String s : map.keySet()) {
            strings.add("§b► §r" + s);
        }
        Objective sidebar = scoreboard.getObjective("sidebar");
        if (sidebar == null) {
            sidebar = scoreboard.registerNewObjective("sidebar", "dummy", title);
        }
        sidebar.displayName(title);
        for (String entry : scoreboard.getEntries()) {
            if (!strings.contains(entry)) {
                scoreboard.resetScores(entry);
            }
        }
        for (String s : map.keySet()) {
            sidebar.getScore("§b► §r" + s).setScore(map.get(s));
        }
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public static void setGlobalSideBar(Component title, Map<String, Integer> map) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        setSideBar(scoreboard, title, map);
    }

    public static void setGlobalTitle(String player, @Nullable Component component, int value) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = scoreboard.getObjective("title");
        if (objective == null) {
            objective = scoreboard.registerNewObjective("title", "dummy", Component.text(""));
        }
        objective.displayName(component);
        objective.getScore(player).setScore(value);
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
    }

    public static void updateOrCreatePlayerUniqueSidebar(Player player, Component title, Map<String, Integer> map) {
        if (player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
        updateSideBar(player.getScoreboard(), title, map);
    }

    public static void clearPlayerUniqueSidebar(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard.equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            return;
        }
        clearSidebar(scoreboard);
    }
}
