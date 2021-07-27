package com.molean.isletopia.message.handler;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.pojo.req.CommandExecuteRequest;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.message.RedisMessageListener;
import org.bukkit.Bukkit;

public class CommandExecuteRequestHandler implements MessageHandler<CommandExecuteRequest> {
    public CommandExecuteRequestHandler() {
        RedisMessageListener.setHandler("CommandExecuteRequest", this, CommandExecuteRequest.class);
        CommandExecuteRequest commandExecuteRequest = new CommandExecuteRequest();
    }

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, CommandExecuteRequest message) {
        String command = message.getCommand();
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        });

    }
}
