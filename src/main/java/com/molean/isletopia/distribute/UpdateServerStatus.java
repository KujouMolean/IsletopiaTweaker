package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.utils.RedisUtils;
import com.molean.isletopia.task.Tasks;

@Singleton
public class UpdateServerStatus {
    public UpdateServerStatus() {
        Tasks.INSTANCE.timeout(200, () -> {
            Tasks.INSTANCE.intervalAsync(20, () -> {
                String s = RedisUtils.getCommand().get("ServerStatus:LastUpdate:" + ServerInfoUpdater.getServerName());
                long l = Long.parseLong(s);
                if (System.currentTimeMillis() - l > 30 * 1000) {
                    //prepare halt
                    Runtime.getRuntime().halt(0);
                }
            });
        });
        Tasks.INSTANCE.interval(20, () -> Tasks.INSTANCE.async(() -> {
            long l = System.currentTimeMillis();
            RedisUtils.getCommand().set("ServerStatus:LastUpdate:" + ServerInfoUpdater.getServerName(), l + "");
        }));




    }
}
