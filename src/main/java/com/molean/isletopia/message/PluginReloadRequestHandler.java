package com.molean.isletopia.message.handler;

import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.pojo.obj.PlaySoundObject;
import com.molean.isletopia.shared.pojo.req.PluginReloadRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

@Singleton
public class PluginReloadRequestHandler implements MessageHandler<PluginReloadRequest> {
    public PluginReloadRequestHandler() {
        RedisMessageListener.setHandler("PluginReload", this, PluginReloadRequest.class);
    }

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, PluginReloadRequest message) {
        ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();
        Bukkit.dispatchCommand(consoleSender, "plugman reload IsletopiaTweakers");
    }
}
