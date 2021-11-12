package com.molean.isletopia.island.flag;

import com.molean.isletopia.island.LocalIsland;
import org.jetbrains.annotations.NotNull;

public interface IslandFlagHandler {

    default void onFlagAdd(LocalIsland island, String ...data){

    }

    default void onFlagRemove(LocalIsland island, String ...data){

    }

    default @NotNull String getKey(){
        return this.getClass().getSimpleName();
    }
}
