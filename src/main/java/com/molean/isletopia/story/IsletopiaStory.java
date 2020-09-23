package com.molean.isletopia.story;


import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.story.command.SceneCommand;
import com.molean.isletopia.story.command.StoryCommand;
import com.molean.isletopia.story.listener.MoveListener;
import com.molean.isletopia.story.story.StoryManager;

import java.util.logging.Logger;

public final class IsletopiaStory {
    public IsletopiaStory() {
        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        try {
            new StoryCommand();
            new MoveListener();
            new SceneCommand();
            StoryManager.reload();
        }catch (Exception exception){
            exception.printStackTrace();
            logger.severe("Initialize isletopia story failed!");
        }
        logger.info("Initialize isletopia story successfully!");
    }
}
