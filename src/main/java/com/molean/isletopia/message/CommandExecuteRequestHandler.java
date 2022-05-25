package com.molean.isletopia.message;

import com.molean.isletopia.shared.annotations.MessageHandlerType;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.pojo.req.CommandExecuteRequest;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.task.Tasks;
import org.bukkit.Bukkit;


@MessageHandlerType(CommandExecuteRequest.class)
public class CommandExecuteRequestHandler implements MessageHandler<CommandExecuteRequest> {

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, CommandExecuteRequest message) {
        String command = message.getCommand();
        Tasks.INSTANCE.sync(() -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        });

    }
}
