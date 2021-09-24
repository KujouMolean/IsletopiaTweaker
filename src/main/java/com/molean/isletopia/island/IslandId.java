package com.molean.isletopia.island;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.utils.IsletopiaTweakersUtils;
import org.jetbrains.annotations.NotNull;

public class IslandId {
    @NotNull
    private final String server;
    private final int x;
    private final int z;

    public IslandId(@NotNull String server, int x, int y) {
        this.server = server;
        this.x = x;
        this.z = y;
    }

    public static IslandId fromLocation(int blockX, int blockZ) {
        return new IslandId(ServerInfoUpdater.getServerName(), blockX >> 9, blockZ >> 9 );
    }


    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public @NotNull String getServer() {
        return server;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IslandId islandId = (IslandId) o;

        if (x != islandId.x) return false;
        if (z != islandId.z) return false;
        return server.equals(islandId.server);
    }

    @Override
    public int hashCode() {
        int result = server.hashCode();
        result = 31 * result + x;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return "IslandId{" +
                "server='" + server + '\'' +
                ", x=" + x +
                ", z=" + z +
                '}';
    }

    public String toLocalString() {
        return IsletopiaTweakersUtils.getLocalServerName() + "(" + x + "," + z + ")";
    }
}
