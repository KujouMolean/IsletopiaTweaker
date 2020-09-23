package com.molean.isletopia.story.story;

import com.molean.isletopia.story.action.Action;
import com.molean.isletopia.story.listener.MoveListener;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StoryTask implements Runnable {

    private static final List<Player> playerList = new ArrayList<>();

    private final Player player;
    private final Story story;

    public StoryTask(Player player, Story story) {
        this.player = player;
        this.story = story;
    }

    @Override
    public void run() {
        if (playerList.contains(player)) {
            player.sendMessage("§c你已经在一个故事中, 无法加入更多故事.");
            return;
        }
        try {
            playerList.add(player);
            for (Action action : story.getActions()) {
                action.play(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            playerList.remove(player);
            MoveListener.unsetmovable(player.getName());
        }
    }


}
