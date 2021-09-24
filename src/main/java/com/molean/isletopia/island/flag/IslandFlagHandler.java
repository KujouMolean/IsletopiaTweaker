package com.molean.isletopia.island.flag;

import com.molean.isletopia.island.Island;
import org.jetbrains.annotations.NotNull;

public interface IslandFlagHandler {

    default void onFlagAdd(Island island, String ...data){

    }

    default void onFlagRemove(Island island, String ...data){

    }

    default @NotNull String getKey(){
        return this.getClass().getSimpleName();
    }
}
