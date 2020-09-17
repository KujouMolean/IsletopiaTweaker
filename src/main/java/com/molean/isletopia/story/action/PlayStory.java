package com.molean.isletopia.story.action;

import com.molean.isletopia.story.StoryManager;
import com.molean.isletopia.story.story.Story;
import com.molean.isletopia.story.story.StoryTask;
import org.bukkit.entity.Player;

public class PlayStory implements Action {
    private final String storyID;

    public PlayStory(String storyID) {
        this.storyID = storyID;
    }

    @Override
    public String toString() {
        return "PlayStory{" +
                "storyID='" + storyID + '\'' +
                '}';
    }

    @Override
    public void play(Player player) {
        Story story = StoryManager.getStory(storyID);
        new StoryTask(player, story).run();
    }
}
