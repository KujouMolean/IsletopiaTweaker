package com.molean.isletopia.tutor;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.tutor.individual.*;

import java.util.logging.Logger;

public class IsletopiaTutorialSystem {
    public IsletopiaTutorialSystem() {
        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        try {
            new LogTutor();
            new HelpTutor();
            new StoneTutor();
            new IronTutor();
            new VillagerTutor();
            new MobFarmTutor();
            new SkipTutorCommand();
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.severe("Initialize isletopia tutorial system failed!");
        }
        logger.info("Initialize isletopia tutorial system successfully!");
    }
}
