//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.molean.isletopia.message.handler;

import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.pojo.resp.VisitResponse;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

@Singleton
public class VisitResponseHandler implements MessageHandler<VisitResponse> {
    public VisitResponseHandler() {
        RedisMessageListener.setHandler("VisitResponse", this, VisitResponse.class);
    }

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, VisitResponse visitResponse) {
        Player player = Bukkit.getPlayer(visitResponse.getTarget());
        if (player != null) {
            if (!Objects.equals("accepted", visitResponse.getResponse())) {
                MessageUtils.fail(player, visitResponse.getResponseMessage());
            } else {
                ServerMessageUtils.switchServer(player.getName(), wrappedMessageObject.getFrom());
            }
        }
    }
}
