package com.molean.isletopia.story.action;

import com.molean.isletopia.story.story.StoryManager;
import com.molean.isletopia.story.story.Story;
import com.molean.isletopia.story.story.StoryTask;
import org.bukkit.entity.Player;

public class PlayStory implements Action {
    private final String namespace;
    private final String name;

    public PlayStory(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    @Override
    public String toString() {
        return "PlayStory{" +
                "namespace='" + namespace + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public void play(Player player) {
        Story story = StoryManager.getStory(namespace,name);
        new StoryTask(player, story).run();
    }
}
