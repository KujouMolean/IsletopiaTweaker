package com.molean.isletopia.statistics.individual;

import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.player.PlayerManager;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.task.Tasks;
import org.bukkit.event.Listener;

@Singleton
public class OnlineCount implements Listener {
    public OnlineCount(PlayerManager playerManager) {
        Tasks.INSTANCE.intervalAsync(60 * 20, () -> {
            int size = playerManager.getLoggedPlayers().size();
            StatisticsDao.insertOnlineCount(ServerInfoUpdater.getServerName(), size);
        });
    }
}
