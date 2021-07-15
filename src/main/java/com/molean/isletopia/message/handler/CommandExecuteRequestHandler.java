package com.molean.isletopia.message.handler;

import com.google.gson.Gson;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.message.core.ServerMessage;
import com.molean.isletopia.message.core.ServerMessageListener;
import com.molean.isletopia.message.core.ServerMessageManager;
import com.molean.isletopia.message.obj.CommandExecuteRequest;
import org.bukkit.Bukkit;

public class CommandExecuteRequestHandler implements ServerMessageListener {
    public CommandExecuteRequestHandler() {
        ServerMessageManager.registerHandler("command", this);
        CommandExecuteRequest commandExecuteRequest = new CommandExecuteRequest();
    }

    @Override
    public void handleMessage(ServerMessage serverMessage) {
        Gson gson = new Gson();
        CommandExecuteRequest obj = gson.fromJson(serverMessage.getMessage(), CommandExecuteRequest.class);
        String command = obj.getCommand();
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        });

        serverMessage.setStatus("done");
    }
}
