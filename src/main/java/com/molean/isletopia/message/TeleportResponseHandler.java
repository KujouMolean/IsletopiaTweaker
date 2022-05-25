//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.molean.isletopia.message;

import com.molean.isletopia.shared.annotations.AutoInject;
import com.molean.isletopia.shared.annotations.MessageHandlerType;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.pojo.resp.TeleportResponse;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.message.ServerMessageService;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


@MessageHandlerType(TeleportResponse.class)
public class TeleportResponseHandler implements MessageHandler<TeleportResponse> {


    @AutoInject
    private ServerMessageService serverMessageService;

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, TeleportResponse message) {
        String target = message.getTarget();
        Player player = Bukkit.getPlayerExact(target);
        if (player != null) {
            if (message.getResponse().equals("accepted")) {
                serverMessageService.switchServer(player.getName(), wrappedMessageObject.getFrom());
            } else {
                MessageUtils.fail(player, message.getResponseMessage());
            }

        }
    }
}
