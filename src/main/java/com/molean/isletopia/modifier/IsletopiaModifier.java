package com.molean.isletopia.modifier;

import com.molean.isletopia.modifier.modifiers.AddMerchant;
import com.molean.isletopia.modifier.modifiers.FertilizeFlower;
import com.molean.isletopia.modifier.modifiers.RegistRecipe;
import com.molean.isletopia.modifier.modifiers.WoodenItemBooster;

public class IsletopiaModifier {
    public IsletopiaModifier() {
        new AddMerchant();
        new RegistRecipe();
        new WoodenItemBooster();
        new FertilizeFlower();
    }
}
