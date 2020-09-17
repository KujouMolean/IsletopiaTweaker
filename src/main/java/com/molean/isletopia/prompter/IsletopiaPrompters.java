package com.molean.isletopia.prompter;

import com.molean.isletopia.prompter.listener.PromterInventoryHandler;
import com.molean.isletopia.prompter.prompter.Prompter;

import java.util.ArrayList;
import java.util.List;

public class IsletopiaPrompters {
    private static final List<Prompter> prompterList = new ArrayList<>();

    public static List<Prompter> getChestPrompterList() {
        return prompterList;
    }

    public IsletopiaPrompters() {
        new IssueCommand();
        new PromterInventoryHandler();
    }
}
