package com.molean.isletopia.prompter;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.prompter.command.IssueCommand;
import com.molean.isletopia.prompter.listener.PromterInventoryHandler;
import com.molean.isletopia.prompter.prompter.Prompter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class IsletopiaPrompters {
    private static final List<Prompter> prompterList = new ArrayList<>();

    public static List<Prompter> getChestPrompterList() {
        return prompterList;
    }

    public IsletopiaPrompters() {

        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        try {
            new IssueCommand();
            new PromterInventoryHandler();
        }catch (Exception exception){
            exception.printStackTrace();
            logger.severe("Initialize isletopia prompter failed!");
        }
        logger.info("Initialize isletopia prompter successfully!");

    }
}
