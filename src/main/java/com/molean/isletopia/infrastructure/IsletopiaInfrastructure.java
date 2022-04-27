package com.molean.isletopia.infrastructure;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.infrastructure.individual.*;
import com.molean.isletopia.infrastructure.individual.bars.EntityBar;
import com.molean.isletopia.infrastructure.individual.bars.ProductionBar;
import com.molean.isletopia.shared.database.PlayerStatsDao;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;

import java.util.logging.Logger;

public class IsletopiaInfrastructure {
    public IsletopiaInfrastructure() {
        Logger logger = PluginUtils.getLogger();
        long l = System.currentTimeMillis();
        try {

            new ClockMenu();
            new TeleportSign();
            new RespawnPoint();
            new StaticMap();
            new MoreChairs();
            new IslandEnterMessage();
            new ServerBumpReward();
            new MenuCommand();
//            new IslandInfoUpdater();
            new PlayerRider();
            new IronElevator();
            new RailWay();
            new FirstSapling();
            new SaveDownload();
            new IslandBackup();
            new SlimeChunk();
            new IslandVisitRecord();
            new ProductionBar();
            new ServerLikeReward();
            new EntityBar();
            new Music();
            new ItemRemover();
            new KeepInventory();
            new DynamicMobSpawnRate();
            new VillagerLimiter();
            new PlayerCommand();
            new TPSBar();
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.severe("Initialize isletopia infrastructure failed!");
        }
        logger.info("Initialize isletopia infrastructure successfully in " + (System.currentTimeMillis()-l)+ "ms");
    }
}
