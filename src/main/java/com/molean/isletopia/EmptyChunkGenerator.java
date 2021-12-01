package com.molean.isletopia;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class EmptyChunkGenerator extends ChunkGenerator {
    public EmptyChunkGenerator() {
    }

    @Override
    public @NotNull ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int x, int z, @NotNull BiomeGrid biome) {
        return this.createChunkData(world);
    }

    @Override
    public boolean shouldGenerateStructures() {
        return true;
    }
}
