package com.molean.isletopia.prompter.prompter;

import com.molean.isletopia.tweakers.IsletopiaTweakers;
import org.bukkit.entity.Player;

public class OnlinePlayerPrompter extends PlayerPrompter {

    public OnlinePlayerPrompter(Player argPlayer, String argTtile) {
        super(argPlayer, argTtile, IsletopiaTweakers.getPlayerNames());
    }

}
