package com.molean.isletopia.island;

import com.molean.isletopia.island.flag.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public enum IslandFlagManager {
    INSTANCE;

    private final Map<String, IslandFlagHandler> map = new HashMap<>();


    IslandFlagManager() {
        try {
            registerHandler(Fly.class);
            registerHandler(DisableLiquidFlow.class);
            registerHandler(DisableRedstone.class);
            registerHandler(Lock.class);
            registerHandler(SpectatorVisitor.class);
            registerHandler(DisableMobSpawn.class);
            registerHandler(DisableBlockBurn.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerHandler(Class<? extends IslandFlagHandler> handler) throws Exception {
        Constructor<? extends IslandFlagHandler> declaredConstructor = null;
        try {
            declaredConstructor = handler.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new Exception("No non-parameter constructor");
        }
        IslandFlagHandler islandFlagHandler = declaredConstructor.newInstance();
        map.put(islandFlagHandler.getKey(), islandFlagHandler);

    }

    public void addFlag(@NotNull Island island, @NotNull String islandFlag) {
        String[] split = islandFlag.split("#");
        IslandFlagHandler islandFlagHandler = map.get(split[0]);
        if (islandFlagHandler != null) {
            islandFlagHandler.onFlagAdd(island, split);
        }
    }

    public void removeFlag(@NotNull Island island, @NotNull String islandFlag) {
        String[] split = islandFlag.split("#");
        IslandFlagHandler islandFlagHandler = map.get(split[0]);
        if (islandFlagHandler != null) {
            islandFlagHandler.onFlagAdd(island, split);
        }
    }

}
