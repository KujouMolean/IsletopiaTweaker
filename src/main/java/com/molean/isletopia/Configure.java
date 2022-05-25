package com.molean.isletopia;

import co.aikar.commands.*;
import com.molean.isletopia.shared.annotations.Bean;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.virtualmenu.internal.VirtualMenuManager;

import java.util.logging.Logger;

@Bean
public class Configure {

    private PaperCommandManager paperCommandManager;
    @Bean
    public IsletopiaTweakers isletopiaTweakers() {
        return IsletopiaTweakers.getPlugin();
    }

    @Bean
    public PaperCommandManager paperCommandManager() {
        if (paperCommandManager == null) {
            paperCommandManager = new PaperCommandManager(isletopiaTweakers());
        }
        return paperCommandManager;
    }

    @Bean
    public Logger logger() {
        return isletopiaTweakers().getLogger();
    }

    @Bean
    public CommandConditions<BukkitCommandIssuer, BukkitCommandExecutionContext, BukkitConditionContext> commandConditions() {
        return paperCommandManager().getCommandConditions();
    }

    @Bean
    public CommandCompletions<BukkitCommandCompletionContext> commandCompletions() {
        return paperCommandManager().getCommandCompletions();
    }
    @Bean
    public CommandContexts<BukkitCommandExecutionContext> commandContexts() {
        return paperCommandManager().getCommandContexts();
    }

    @Bean
    public CommandReplacements commandReplacements() {
        return paperCommandManager().getCommandReplacements();
    }

    @Bean
    public VirtualMenuManager virtualMenuManager() {
        return VirtualMenuManager.INSTANCE;
    }

    @Bean
    public Tasks tasks() {
        return Tasks.INSTANCE;
    }
}
