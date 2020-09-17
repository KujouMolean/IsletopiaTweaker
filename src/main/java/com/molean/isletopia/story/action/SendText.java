package com.molean.isletopia.story.action;

import org.bukkit.entity.Player;

public class SendText implements Action {
    private final String text;

    public SendText(String text) {
        this.text = text;
    }

    @Override
    public void play(Player player) {
        player.sendMessage(text);
    }

    @Override
    public String toString() {
        return "SendText{" +
                "text='" + text + '\'' +
                '}';
    }
}
