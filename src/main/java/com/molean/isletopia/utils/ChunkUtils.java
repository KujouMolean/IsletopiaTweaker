package com.molean.isletopia.utils;

import com.molean.isletopia.IsletopiaTweakers;
import net.querz.mca.MCAFile;
import net.querz.mca.MCAUtil;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ChunkUtils {

    public static long pair(int chunkX, int chunkZ) {
        return (long) chunkX & 4294967295L | ((long) chunkZ & 4294967295L) << 32;
    }

    public static void unloadRegion(final int x, final int z) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

//        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
//            onlinePlayer.kick(Component.text("1"));
//        }
//
//        //remove region file cache
//        CraftWorld world = (CraftWorld) IsletopiaTweakers.getWorld();
//        WorldServer worldServer = world.getHandle();
//
//        ChunkProviderServer chunkProvider = worldServer.getChunkProvider();
//        PlayerChunkMap playerChunkMap = chunkProvider.a;
//        playerChunkMap.updatingChunks.performUpdates();
//        world.save();
//
//
//        for (int i = 0; i < 32; i++) {
//            for (int j = 0; j < 32; j++) {
//                int chunkX = (x << 5) + i;
//                int chunkZ = (z << 5) + j;
//                worldServer.asyncChunkTaskManager.cancelChunkLoad(chunkX, chunkZ);
//                world.unloadChunk(chunkX, z << 5 + i, true);
//
//
//                playerChunkMap.u.clear();
//
//
//                Method unloadChunks = playerChunkMap.getClass().getDeclaredMethod("unloadChunks", BooleanSupplier.class);
//                unloadChunks.setAccessible(true);
//                unloadChunks.invoke(playerChunkMap, (BooleanSupplier) () -> false);
//
//
//                IsletopiaTweakers.getWorld().unloadChunk(chunkX, chunkZ);
//                Chunk chunk = chunkProvider.getChunkAtIfLoadedImmediately(chunkX, chunkZ);
//                if (chunk != null) {
//                    chunkProvider.removeLoadedChunk(chunk);
//                }
//                IsletopiaTweakers.getWorld().unloadChunk(chunkX, chunkZ);
//                SingleThreadChunkRegionManager.RegionSection regionSection = playerChunkMap.dataRegionManager.getRegionSection(chunkX, chunkZ);
//                try {
//                    playerChunkMap.dataRegionManager.removeChunk(chunkX, chunkZ);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                for (SingleThreadChunkRegionManager regionManager : playerChunkMap.regionManagers) {
//                    try {
//                        regionManager.removeChunk(chunkX, chunkZ);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                playerChunkMap.updatingChunks.getUpdatingMap().remove(pair(chunkX, chunkZ));
//                playerChunkMap.updatingChunks.getVisibleMap().remove(pair(chunkX, chunkZ));
//            }
//        }
//        for (PlayerChunk value : playerChunkMap.updatingChunks.getUpdatingMap().values()) {
//            System.out.println(value);
//        }


    }

    public static void loadRegion(int x, int z) throws IOException {
//        CraftWorld world = (CraftWorld) IsletopiaTweakers.getWorld();
//        WorldServer worldServer = world.getHandle();
//
//        ChunkProviderServer chunkProvider = worldServer.getChunkProvider();
//        PlayerChunkMap playerChunkMap = chunkProvider.a;
//        RegionFileCache regionFileCache = playerChunkMap.regionFileCache;
//        regionFileCache.getFile(new ChunkCoordIntPair(x, z), false);
//
//        for (int i = 0; i < 32; i++) {
//            for (int j = 0; j < 32; j++) {
//                int chunkX = (x << 5) + i;
//                int chunkZ = (z << 5) + j;
////                world.loadChunk(chunkX, chunkZ);
//                Chunk chunkAt = chunkProvider.getChunkAtMainThread(chunkX, chunkZ);
//
//                chunkProvider.addLoadedChunk(chunkAt);
//            }
//        }
    }

    public static void hotSwapRegionFile(int x, int z, File file) throws Exception {
        unloadRegion(x, z);
        World world = IsletopiaTweakers.getWorld();
        File target = new File(world.getWorldFolder() + "/region/r." + x + "." + z + ".mca");
        MCAFile read = MCAUtil.read(file);

        Field regionX = read.getClass().getDeclaredField("regionX");
        regionX.setAccessible(true);
        regionX.set(read, x);

        Field regionY = read.getClass().getDeclaredField("regionZ");
        regionY.setAccessible(true);
        regionY.set(read, z);

        MCAUtil.write(read, target);
//        loadRegion(x, z);

//        unloadRegion(x, z);
    }
}
