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


    private class EmptyChunkData implements ChunkData {
        private ChunkData chunkData;

        public EmptyChunkData(World world,int x,int z) {
            chunkData = Bukkit.createVanillaChunkData(world, x, z);
        }

        @Override
        public int getMinHeight() {
            return chunkData.getMinHeight();
        }

        @Override
        public int getMaxHeight() {
            return chunkData.getMaxHeight();
        }

        @Override
        public @NotNull Biome getBiome(int x, int y, int z) {
            return Biome.PLAINS;
        }

        @Override
        public void setBlock(int x, int y, int z, @NotNull Material material) {
            chunkData.setBlock(x,y,z,material);
        }

        @Override
        public void setBlock(int x, int y, int z, @NotNull MaterialData material) {
            chunkData.setBlock(x, y, z, material);
        }

        @Override
        public void setBlock(int x, int y, int z, @NotNull BlockData blockData) {
            chunkData.setBlock(x, y, z, blockData);
        }

        @Override
        public void setRegion(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, @NotNull Material material) {
            chunkData.setRegion(xMin, yMin, zMin, xMax, yMax, zMax, material);
        }

        @Override
        public void setRegion(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, @NotNull MaterialData material) {
            chunkData.setRegion(xMin, yMin, zMin, xMax, yMax, zMax, material);
        }

        @Override
        public void setRegion(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, @NotNull BlockData blockData) {
            chunkData.setRegion(xMin, yMin, zMin, xMax, yMax, zMax, blockData);
        }

        @Override
        public @NotNull Material getType(int x, int y, int z) {
            return chunkData.getType(x, y, z);
        }

        @Override
        public @NotNull MaterialData getTypeAndData(int x, int y, int z) {
            return chunkData.getTypeAndData(x, y, z);
        }

        @Override
        public @NotNull BlockData getBlockData(int x, int y, int z) {
            return chunkData.getBlockData(x, y, z);
        }

        @Override
        public byte getData(int x, int y, int z) {
            return chunkData.getData(x, y, z);
        }
    }

    private EmptyChunkData emptyChunkData;

    @Override
    public @NotNull ChunkData createVanillaChunkData(@NotNull World world, int x, int z) {
        if (emptyChunkData == null) {
            emptyChunkData = new EmptyChunkData(world, 0, 0);
        }
        return emptyChunkData;
    }

    @Override
    public boolean shouldGenerateStructures() {
        return true;
    }
}
