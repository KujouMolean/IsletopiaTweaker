package com.molean.isletopia.bungee;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.admin.individual.UniversalTeleportCommand;
import com.molean.isletopia.bungee.individual.PlayerMessageHandler;
import com.molean.isletopia.bungee.individual.UniversalTeleportHandler;
import com.molean.isletopia.bungee.individual.UniversalVisitHandler;
import com.molean.isletopia.bungee.individual.VisitNotificationHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.logging.Logger;

public class IsletopiaBungee {
    public IsletopiaBungee() {
        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        try {
            new PlayerMessageHandler();
            new UniversalVisitHandler();
            new UniversalTeleportHandler();
            new VisitNotificationHandler();
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.severe("Initialize isletopia bungee failed!");
        }
        logger.info("Initialize isletopia bungee successfully!");
    }
}
