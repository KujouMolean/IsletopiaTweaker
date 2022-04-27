package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.utils.RedisUtils;
import com.molean.isletopia.task.Tasks;

public class UpdateServerStatus {
    public UpdateServerStatus() {
        Tasks.INSTANCE.interval(20, () -> Tasks.INSTANCE.async(() -> {
            long l = System.currentTimeMillis();
            RedisUtils.getCommand().set("ServerStatus:LastUpdate:" + ServerInfoUpdater.getServerName(), l + "");
        }));

    }
}
