package com.molean.isletopia.cloud;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.PluginUtils;

import java.util.logging.Logger;

public class CloudInventorySystem {
    public CloudInventorySystem() {
        Logger logger = PluginUtils.getLogger();
        long l = System.currentTimeMillis();
        try {
            new CloudInventoryCommand();
            new CloudInventoryListener();
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.severe("Initialize cloud inventory failed!");
        }
        logger.info("Initialize cloud inventory successfully in " + (System.currentTimeMillis() - l) + "ms");
    }
}
