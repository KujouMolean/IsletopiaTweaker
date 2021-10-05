package com.molean.isletopia.message.handler;

import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.pojo.req.TellMessageRequest;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.message.RedisMessageListener;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TellMessageRequestHandler implements MessageHandler<TellMessageRequest> {
    public TellMessageRequestHandler() {
        RedisMessageListener.setHandler("TellMessage", this, TellMessageRequest.class);
    }

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, TellMessageRequest message) {
        String target = message.getTarget();
        Player player = Bukkit.getPlayerExact(target);
        if (player != null) {
            String finalMessage = "§7" + message.getSource() + " -> "
                    + message.getTarget() + ": "
                    + message.getMessage();
            player.sendMessage(Component.text(finalMessage));
        }
    }
}
