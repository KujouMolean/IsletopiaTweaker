package com.molean.isletopia;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

public class EmptyChunkGenerator extends ChunkGenerator {
    public EmptyChunkGenerator() {
    }

    @Override
    public @NotNull ChunkData createVanillaChunkData(@NotNull World world, int x, int z) {
        return super.createVanillaChunkData(world, x, z);
    }

    @Override
    public boolean shouldGenerateStructures() {
        return true;
    }
}
