package com.molean.isletopia.prompter.util;

import com.molean.isletopia.prompter.prompter.Prompter;

import java.util.ArrayList;
import java.util.List;

public class PrompterUtils {
    private static final List<Prompter> prompterList = new ArrayList<>();

    public static List<Prompter> getChestPrompterList() {
        return prompterList;
    }

}
