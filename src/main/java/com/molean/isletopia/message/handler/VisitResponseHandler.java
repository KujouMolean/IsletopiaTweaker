//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.molean.isletopia.message.handler;

import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.pojo.resp.VisitResponse;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.utils.RedisUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.Objects;

public class VisitResponseHandler implements MessageHandler<VisitResponse> {
    public VisitResponseHandler() {
        RedisMessageListener.setHandler("VisitResponse", this, VisitResponse.class);
    }

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, VisitResponse visitResponse) {
        Player player = Bukkit.getPlayer(visitResponse.getTarget());
        if (player != null) {
            if (!Objects.equals("accepted", visitResponse.getResponse())) {
                player.sendMessage(visitResponse.getResponseMessage());
            } else {
                if (!ServerInfoUpdater.getServerName().equalsIgnoreCase(wrappedMessageObject.getFrom())) {
                    ServerMessageUtils.switchServer(player.getName(), wrappedMessageObject.getFrom());
                }
            }
        }
    }
}
