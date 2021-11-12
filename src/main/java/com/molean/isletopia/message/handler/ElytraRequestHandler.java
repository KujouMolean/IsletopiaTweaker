package com.molean.isletopia.message.handler;

import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.pojo.req.ElytraRequestObject;
import com.molean.isletopia.shared.pojo.resp.CommonResponseObject;
import com.molean.isletopia.shared.utils.UUIDUtils;

import java.util.UUID;


public class ElytraRequestHandler implements MessageHandler<ElytraRequestObject> {
    public ElytraRequestHandler() {
        RedisMessageListener.setHandler("ElytraRequest", this, ElytraRequestObject.class);
    }

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, ElytraRequestObject message) {
        String player = message.getPlayer();
        String reason = message.getReason();
        UUID uuid = UUIDUtils.get(player);
        String resp = player + " 获得了鞘翅权限, 原因是: " + reason;
        if (uuid == null) {
            resp = "该玩家不存在";
        } else {
            resp = player + " 获得了鞘翅权限, 原因是: " + reason;
            UniversalParameter.addParameter(uuid, "elytra", player);
            UniversalParameter.setParameter(uuid, "elytraReason", reason);
        }
        CommonResponseObject commonResponseObject = new CommonResponseObject(resp);
        ServerMessageUtils.sendMessage("waterfall", "CommonResponse", commonResponseObject);

    }
}
