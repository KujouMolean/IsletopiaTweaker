package com.molean.isletopia.message.handler;

import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.pojo.req.BeaconRequestObject;
import com.molean.isletopia.shared.pojo.resp.CommonResponseObject;
import com.molean.isletopia.shared.utils.UUIDUtils;

import java.util.UUID;

public class BeaconRequestHandler implements MessageHandler<BeaconRequestObject> {
    public BeaconRequestHandler() {
        RedisMessageListener.setHandler("BeaconRequest", this, BeaconRequestObject.class);
    }

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, BeaconRequestObject message) {
        String player = message.getPlayer();
        String reason = message.getReason();
        UUID uuid = UUIDUtils.get(player);
        String resp;
        if (uuid == null) {
            resp = player + "该玩家不存在";
        } else {
            UniversalParameter.addParameter(uuid, "beacon", "true");
            UniversalParameter.setParameter(uuid, "beaconReason", reason);
            resp = player + " 获得了信标权限, 原因是: " + reason;
        }

        CommonResponseObject commonResponseObject = new CommonResponseObject(resp);
        ServerMessageUtils.sendMessage("waterfall", "CommonResponse", commonResponseObject);
    }
}
