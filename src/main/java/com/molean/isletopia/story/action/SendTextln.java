package com.molean.isletopia.story.action;

import org.bukkit.entity.Player;

public class SendTextln extends SendText {

    public SendTextln(String text) {
        super(text);
    }

    @Override
    public void play(Player player) {
        super.play(player);
        player.sendMessage(" ");
    }
}
