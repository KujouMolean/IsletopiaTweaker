package com.molean.isletopia.distribute;

import com.molean.isletopia.annotations.Interval;
import com.molean.isletopia.shared.annotations.AutoInject;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.service.RedisService;
import com.molean.isletopia.task.Tasks;
import org.bukkit.Bukkit;

@Singleton
public class UpdateServerStatus {
    @AutoInject
    private RedisService redisService;
    private boolean initialUpdate = false;


    @Interval(value = 20,async = true)
    public void check() {
        if (!initialUpdate) {
            return;
        }

        String s = redisService.getCommand().get("ServerStatus:LastUpdate:" + ServerInfoUpdater.getServerName());
        long l = Long.parseLong(s);
        if (System.currentTimeMillis() - l > 30 * 1000) {
            //prepare halt
            Runtime.getRuntime().halt(0);
        }
    }

    @Interval(20)
    public void update() {
        long l = System.currentTimeMillis();
        initialUpdate = true;
        redisService.getCommand().set("ServerStatus:LastUpdate:" + ServerInfoUpdater.getServerName(), l + "");
    }

}
