//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.molean.isletopia.message;

import com.molean.isletopia.shared.annotations.AutoInject;
import com.molean.isletopia.shared.annotations.MessageHandlerType;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.message.ServerMessageService;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.pojo.resp.VisitResponse;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

@MessageHandlerType(VisitResponse.class)
public class VisitResponseHandler implements MessageHandler<VisitResponse> {

    @AutoInject
    private ServerMessageService serverMessageService;

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, VisitResponse visitResponse) {
        Player player = Bukkit.getPlayer(visitResponse.getTarget());
        if (player != null) {
            if (!Objects.equals("accepted", visitResponse.getResponse())) {
                MessageUtils.fail(player, visitResponse.getResponseMessage());
            } else {
                serverMessageService.switchServer(player.getName(), wrappedMessageObject.getFrom());
            }
        }
    }
}
