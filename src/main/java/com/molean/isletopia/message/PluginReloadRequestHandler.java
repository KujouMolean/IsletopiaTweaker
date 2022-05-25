package com.molean.isletopia.message;

import com.molean.isletopia.shared.annotations.MessageHandlerType;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.pojo.req.PluginReloadRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

@MessageHandlerType(PluginReloadRequest.class)
public class PluginReloadRequestHandler implements MessageHandler<PluginReloadRequest> {

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, PluginReloadRequest message) {
        ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();
        Bukkit.dispatchCommand(consoleSender, "plugman reload IsletopiaTweakers");
    }
}
