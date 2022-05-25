package com.molean.isletopia.annotations;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import com.molean.isletopia.shared.annotations.AutoInject;
import com.molean.isletopia.shared.annotations.BeanHandler;
import com.molean.isletopia.shared.annotations.BeanHandlerPriority;

import java.util.logging.Logger;

@BeanHandlerPriority(0)
public class CommandAliasHandler implements BeanHandler {
    private final PaperCommandManager paperCommandManager;


    public CommandAliasHandler(PaperCommandManager paperCommandManager) {
        this.paperCommandManager = paperCommandManager;
    }

    @Override
    public void handle(Object object) {
        if (object.getClass().isAnnotationPresent(CommandAlias.class)) {
            try {
                paperCommandManager.registerCommand((BaseCommand) object);
            } catch (Exception e) {
                throw new RuntimeException("Error register command for " + object.getClass().getName());
            }
        }
    }
}
