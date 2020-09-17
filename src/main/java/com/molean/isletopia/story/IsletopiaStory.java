package com.molean.isletopia.story;


import com.molean.isletopia.story.command.StoryCommand;
import com.molean.isletopia.story.listener.MoveListener;

public final class IsletopiaStory {
    public IsletopiaStory() {
        new StoryCommand();
        new MoveListener();
        StoryManager.reload();
    }
}
