package com.molean.isletopia.prompter.prompter;

import com.molean.isletopia.distribute.system.ServerInfoUpdater;
import org.bukkit.entity.Player;

public class OnlinePlayerPrompter extends PlayerPrompter {

    public OnlinePlayerPrompter(Player argPlayer, String argTtile) {
        super(argPlayer, argTtile, ServerInfoUpdater.getOnlinePlayers());
    }

}
