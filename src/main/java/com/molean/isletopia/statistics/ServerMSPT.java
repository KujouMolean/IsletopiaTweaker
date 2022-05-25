package com.molean.isletopia.statistics.individual;

import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.shared.database.MSPTDao;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MSPTUtils;

import java.sql.SQLException;

@Singleton
public class ServerMSPT {
    public ServerMSPT() {
        Tasks.INSTANCE.interval(20 * 5 * 60, () -> {
            try {
                double mspt = MSPTUtils.get();

                Tasks.INSTANCE.async(() -> {
                    try {
                        MSPTDao.addRecord(ServerInfoUpdater.getServerName(), mspt);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
