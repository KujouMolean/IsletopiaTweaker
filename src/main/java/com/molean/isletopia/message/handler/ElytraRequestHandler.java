package com.molean.isletopia.message.handler;

import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.pojo.resp.CommonResponseObject;
import com.molean.isletopia.shared.pojo.req.ElytraRequestObject;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.shared.message.ServerMessageUtils;


public class ElytraRequestHandler implements MessageHandler<ElytraRequestObject> {
    public ElytraRequestHandler() {
        RedisMessageListener.setHandler("ElytraRequest", this, ElytraRequestObject.class);
    }

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject,ElytraRequestObject message) {
        String player = message.getPlayer();
        String reason = message.getReason();
        UniversalParameter.addParameter("Molean", "elytra", player);
        UniversalParameter.setParameter(player, "elytraReason", reason);
        String resp = player + " 获得了鞘翅权限, 原因是: " + reason;
        CommonResponseObject commonResponseObject = new CommonResponseObject(resp);
        ServerMessageUtils.sendMessage("waterfall", "CommonResponse", commonResponseObject);

    }
}
