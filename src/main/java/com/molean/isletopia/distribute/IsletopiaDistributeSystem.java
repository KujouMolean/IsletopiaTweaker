package com.molean.isletopia.distribute;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.database.PlayerParameterDao;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.distribute.individual.*;
import org.bukkit.Bukkit;

import java.util.logging.Logger;

public class IsletopiaDistributeSystem {
    public IsletopiaDistributeSystem() {


        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        long l = System.currentTimeMillis();
        try {
            new ParameterCommand();
            new NewbieOperation();
            new VisitCommand();
            new ServerInfoUpdater();
            new LastServerUpdater();
            new ClubServer();
            new PlayerDataSync();
            new PlayerStatsSync();
            new UpdateServerStatus();
            new UploadServerMSPT();
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.severe("Initialize isletopia distribute failed!");
            Bukkit.getServer().shutdown();

        }
        logger.info("Initialize isletopia distribute successfully in " + (System.currentTimeMillis()-l) + "ms");
    }
}
