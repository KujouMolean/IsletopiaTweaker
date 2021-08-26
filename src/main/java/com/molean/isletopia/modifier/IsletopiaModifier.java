package com.molean.isletopia.modifier;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.infrastructure.individual.FirstSapling;
import com.molean.isletopia.modifier.individual.*;

import java.util.logging.Logger;

public class IsletopiaModifier {
    public IsletopiaModifier() {
        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
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
//            new AdvancedDispenser();

            new TestListener();
            new NetherPortal();
            new ShulkerRespawn();
            new TestListener();
            new LuckyColor();
            new RichPiglin();
            new SleepAnyTime();

        } catch (Exception exception) {
            exception.printStackTrace();
            logger.severe("Initialize isletopia modifier failed!");
        }
        logger.info("Initialize isletopia modifier successfully!");
    }
}
