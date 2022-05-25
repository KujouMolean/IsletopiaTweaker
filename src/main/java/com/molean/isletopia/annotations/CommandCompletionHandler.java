package com.molean.isletopia.annotations;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import com.molean.isletopia.shared.annotations.BeanHandler;
import com.molean.isletopia.shared.annotations.BeanHandlerPriority;

@BeanHandlerPriority(100)

public class CommandCompletionHandler implements BeanHandler {
    private final CommandCompletions<BukkitCommandCompletionContext> commandCompletions;

    public CommandCompletionHandler(CommandCompletions<BukkitCommandCompletionContext> commandCompletions) {
        this.commandCompletions = commandCompletions;
    }

    @Override
    @SuppressWarnings("all")
    public void handle(Object object) {
        if (object.getClass().isAnnotationPresent(Completion.class)) {
            Completion annotation = object.getClass().getAnnotation(Completion.class);
            String value = annotation.value();
            if (object instanceof CommandCompletions.AsyncCommandCompletionHandler<?>) {
                commandCompletions.registerAsyncCompletion(value, (CommandCompletions.AsyncCommandCompletionHandler<BukkitCommandCompletionContext>) object);
            }
        }
    }
}
