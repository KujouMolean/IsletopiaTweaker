//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.molean.isletopia.message.handler;

import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.pojo.resp.TeleportResponse;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TeleportResponseHandler implements MessageHandler<TeleportResponse> {
    public TeleportResponseHandler() {
        RedisMessageListener.setHandler("TeleportResponse", this, TeleportResponse.class);
    }


    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, TeleportResponse message) {
        String target = message.getTarget();
        Player player = Bukkit.getPlayerExact(target);
        if (player != null) {
            if (message.getResponse().equals("accepted")) {
                ServerMessageUtils.switchServer(player.getName(), wrappedMessageObject.getFrom());
            } else {
                MessageUtils.fail(player, message.getResponseMessage());
            }

        }
    }
}
