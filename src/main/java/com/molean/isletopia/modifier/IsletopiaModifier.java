package com.molean.isletopia.modifier;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.modifier.individual.*;

import java.util.logging.Logger;

public class IsletopiaModifier {
    public IsletopiaModifier() {
        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        long l = System.currentTimeMillis();
        try {
            new PlayerHeadDrop();
            new RichWanderingTrader();
            new MoreRecipe();
            new RemoveUnbreakable();
            new FertilizeFlower();
            new HungerKeeper();
            new RemoveJoinLeftMessage();
            new RandomPatrol();
            new DeepOceanGuardian();
            new AntiZombification();
            new DragonHeadExtractBreath();
            new EquipmentWeaker();
            new PreventRenameHead();
            new AutoFloor();
            new ShulkerRespawn();
            new AutoFloor();
            new LuckyColor();
            new RichPiglin();
//            new SleepAnyTime();
            new ElderGuardianSpawner();
            new CaveSpiderSpawner();
            new PlayerDeathMob();
            new HexBeacon();
            new AutoCraft();
            new NetherAdapter();

        } catch (Exception exception) {
            exception.printStackTrace();
            logger.severe("Initialize isletopia modifier failed!");
        }
        logger.info("Initialize isletopia modifier successfully in " + (System.currentTimeMillis() - l) + "ms");
    }
}
