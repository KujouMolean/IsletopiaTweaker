package com.molean.isletopia.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang3.ThreadUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
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
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
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

    public static void hideGlobalTitle() {

    }

    public static void showGlobalTitle() {

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

    public static void setPlayerUniqueSidebar(Player player, Component title, Map<String, Integer> map) {
        if (player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
//        player.getScoreboard().getScores()

        setSideBar(player.getScoreboard(), title, map);
    }

    public static void clearPlayerUniqueSidebar(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard.equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            return;
        }
        clearSidebar(scoreboard);
    }

    public static void test(Player player) {
        Team test = player.getScoreboard().registerNewTeam("test");
        Team test2 = player.getScoreboard().registerNewTeam("test2");

    }

}
