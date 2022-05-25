package com.molean.isletopia.statistics;

import com.molean.isletopia.annotations.Interval;
import com.molean.isletopia.shared.annotations.AutoInject;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.player.PlayerManager;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.task.Tasks;
import org.bukkit.event.Listener;

@Singleton
public class OnlineCount implements Listener {
    @AutoInject
    private PlayerManager playerManager;

    @Interval(value = 20 * 60, async = true)
    public void submit() {
        int size = playerManager.getLoggedPlayers().size();
        StatisticsDao.insertOnlineCount(ServerInfoUpdater.getServerName(), size);
    }
}
