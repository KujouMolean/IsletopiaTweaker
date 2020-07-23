package com.molean.isletopia.prompter.prompter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class OnlinePlayerPrompter extends PlayerPrompter {

    public OnlinePlayerPrompter(Player argPlayer, String argTtile) {
        super(argPlayer, argTtile, getPlayernames());
    }

    private static List<String> getPlayernames() {
        List<String> playernames = new ArrayList<>();
        Bukkit.getServer().getOnlinePlayers().forEach(playername -> playernames.add(playername.getName()));
        return playernames;
    }
}
