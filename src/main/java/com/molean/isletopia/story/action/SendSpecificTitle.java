package com.molean.isletopia.story.action;

import org.bukkit.entity.Player;

public class SendSpecificTitle implements Action {
    private final String title;
    private final String subTitle;
    private final int fadeIn;
    private final int fateOut;
    private final int stay;

    public SendSpecificTitle(String title, String subTitle, int fadeIn, int fateOut, int stay) {
        this.title = title;
        this.subTitle = subTitle;
        this.fadeIn = fadeIn;
        this.fateOut = fateOut;
        this.stay = stay;
    }

    @Override
    public void play(Player player) {
        player.sendTitle(title,subTitle,fadeIn, stay, fateOut);

    }

    @Override
    public String toString() {
        return "SendTitle{" +
                "title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", fadeIn=" + fadeIn +
                ", fateOut=" + fateOut +
                ", stay=" + stay +
                '}';
    }
}
