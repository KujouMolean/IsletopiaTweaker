package com.molean.isletopia.modifier;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.modifier.individual.*;

import java.util.logging.Logger;

public class IsletopiaModifier {
    public IsletopiaModifier() {
        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        try {
            new RichWanderingTrader();
            new MoreRecipe();
            new RemoveUnbreakable();
            new FertilizeFlower();
            new HungerKeeper();
            new IronElevator();
            new RailWay();
            new RemoveJoinLeftMessage();
            new RandomPatrol();
            new DeepOceanGuardian();
            new AntiZombification();
            new DragonHeadExtractBreath();
            new EquipmentWeaker();
            new PlayerHeadDrop();
            new PreventRenameHead();
            new AdvancedDispenser();
            new FirstSapling();
            new TestListener();
            new NetherPortal();
//            new HopperFilter();
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.severe("Initialize isletopia modifier failed!");
        }
        logger.info("Initialize isletopia modifier successfully!");
    }
}
