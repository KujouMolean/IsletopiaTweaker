package com.molean.isletopia.message.handler;

import com.molean.isletopia.shared.database.PlayTimeStatisticsDao;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.pojo.req.PlayTimeRequest;
import com.molean.isletopia.shared.pojo.resp.CommonResponseObject;
import com.molean.isletopia.shared.utils.UUIDUtils;

import java.util.UUID;

public class PlayTimeRequestHandler implements MessageHandler<PlayTimeRequest> {
    public PlayTimeRequestHandler() {
        RedisMessageListener.setHandler("PlayTimeRequest", this, PlayTimeRequest.class);
    }

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, PlayTimeRequest message) {
        String player = message.getPlayer();
        long l = System.currentTimeMillis();
        UUID uuid = UUIDUtils.get(player);
        long recent30d = PlayTimeStatisticsDao.getRecentPlayTime(uuid, l - 30L * 24 * 60 * 60 * 1000);
        long recent7d = PlayTimeStatisticsDao.getRecentPlayTime(uuid, l - 7L * 24 * 60 * 60 * 1000);
        long recent3d = PlayTimeStatisticsDao.getRecentPlayTime(uuid, l - 3L * 24 * 60 * 60 * 1000);
        recent30d /= 1000 * 60 * 60;
        recent7d /= 1000 * 60 * 60;
        recent3d /= 1000 * 60 * 60;
        String str = player + " 最近游戏情况: " +
                "3天内" + recent3d + "小时, " +
                "7天内" + recent7d + "小时, " +
                "30天内" + recent30d + "小时.";
        CommonResponseObject commonResponseObject = new CommonResponseObject(str);
        ServerMessageUtils.sendMessage("waterfall", "CommonResponse", commonResponseObject);
    }
}
