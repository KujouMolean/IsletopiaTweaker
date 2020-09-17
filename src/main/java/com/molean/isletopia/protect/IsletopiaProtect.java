package com.molean.isletopia.protect;

import com.molean.isletopia.protect.protections.*;

public class IsletopiaProtect {
    public IsletopiaProtect() {
        new AnimalProtect();
        new LavaProtect();
        new PlotMobCap();
        new PreventCreeperBreak();
        new RemoveDisgustingMob();
    }
}
