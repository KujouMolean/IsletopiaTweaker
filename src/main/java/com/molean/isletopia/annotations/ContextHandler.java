package com.molean.isletopia.annotations;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandContexts;
import co.aikar.commands.contexts.ContextResolver;
import com.molean.isletopia.shared.annotations.BeanHandler;
import com.molean.isletopia.shared.annotations.BeanHandlerPriority;

@BeanHandlerPriority(20)
public class ContextHandler implements BeanHandler {

    private CommandContexts<BukkitCommandExecutionContext> commandContexts;

    public ContextHandler(CommandContexts<BukkitCommandExecutionContext> commandContexts) {
        this.commandContexts = commandContexts;
    }

    @Override
    public void handle(Object object) {
        if (object.getClass().isAnnotationPresent(Context.class)) {
            Context annotation = object.getClass().getAnnotation(Context.class);
            Class<Object> value = (Class<Object>) annotation.value();
            ContextResolver<Object, BukkitCommandExecutionContext> context = (ContextResolver<Object, BukkitCommandExecutionContext>) object;
            commandContexts.registerContext(value, context);
        }
    }
}
