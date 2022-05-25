package com.molean.isletopia.island;

import com.google.gson.Gson;
import com.molean.isletopia.event.PlayerIslandChangeEvent;
import com.molean.isletopia.island.obj.CuboidRegion;
import com.molean.isletopia.island.obj.CuboidShape;
import com.molean.isletopia.shared.database.IslandDao;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.model.Island;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.shared.utils.ResourceUtils;
import com.molean.isletopia.task.PlotAllChunkTask;
import com.molean.isletopia.task.PlotChunkTask;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;


// 每次使用都应该从IslandManager获取, 而不是保存在某处。
// 只能在岛屿所在的服务器上获取LocalIsland, 否则应该使用Island

public class LocalIsland extends Island {

    public LocalIsland(Island island) {
        super(island);
    }

    private boolean changingBiome = false;

    private final Set<Material> blacklistInBlock = Set.of(Material.NETHER_PORTAL);
    private final Set<Material> blacklistOnBlock = Set.of();

    public LocalIsland(int id, int x, int z,String spawnWorld, double spawnX, double spawnY, double spawnZ, float yaw, float pitch, @NotNull String server, @NotNull UUID uuid, @Nullable String name, @NotNull Timestamp creation, Set<UUID> members, Set<String> islandFlags, String icon) {
        super(id, x, z, spawnWorld,spawnX, spawnY, spawnZ, yaw, pitch, server, uuid, name, creation, members, islandFlags, icon);
    }


    @Nullable
    private Location getSafeLandingPosition(@NotNull Location location) {
        for (int i = 0; i < location.getY() - location.getWorld().getMinHeight(); i++) {
            Location add = location.clone().add(0, -i, 0);
            if (add.getBlock().getRelative(BlockFace.DOWN).isPassable()) {
                continue;
            }
            if (add.getBlock().getRelative(BlockFace.DOWN).isPassable()) {
                continue;
            }
            if (blacklistOnBlock.contains(add.getBlock().getRelative(BlockFace.DOWN).getType())) {
                continue;
            }
            if (!add.getBlock().isPassable()) {
                continue;
            }
            if (blacklistInBlock.contains(add.getBlock().getType())) {
                continue;
            }
            if (!add.getBlock().getRelative(BlockFace.UP).isPassable()) {
                continue;
            }
            if (blacklistInBlock.contains(add.getBlock().getRelative(BlockFace.UP).getType())) {
                continue;
            }
            return add;
        }
        return null;
    }

    private Location getHigherLandingPosition(Location location) {
        for (int i = 0; i < location.getWorld().getMaxHeight() - location.getY(); i++) {
            Location add = location.clone().add(0, i, 0);
            if (add.getBlock().getRelative(BlockFace.DOWN).isPassable()) {
                continue;
            }
            if (blacklistOnBlock.contains(add.getBlock().getRelative(BlockFace.DOWN).getType())) {
                continue;
            }
            if (!add.getBlock().isPassable()) {
                continue;
            }
            if (blacklistInBlock.contains(add.getBlock().getType())) {
                continue;
            }
            if (!add.getBlock().getRelative(BlockFace.UP).isPassable()) {
                continue;
            }
            if (blacklistInBlock.contains(add.getBlock().getRelative(BlockFace.UP).getType())) {
                continue;
            }
            return add;
        }
        return null;
    }

    public Location getSpawnLocation() {
        return new Location(Bukkit.getWorld(spawnWorld), (x << 9) + spawnX, spawnY, (z << 9) + spawnZ, yaw, pitch);
    }

    public Location adjustSafeTpLocation(Location location) {
        if (location.getY() < location.getWorld().getMinHeight()) {
            location.setY(location.getWorld().getMinHeight() + 1);
        }
        if (location.getY() > location.getWorld().getMaxHeight()) {
            location.setY(location.getWorld().getMaxHeight());
        }
        Location safeLandingPosition = getSafeLandingPosition(location);
        if (safeLandingPosition == null) {
            Location higherLandingPosition = getHigherLandingPosition(location);
            if (higherLandingPosition == null) {
                Location finalLocation = location;
                Tasks.INSTANCE.sync(() -> {
                    finalLocation.getBlock().getRelative(BlockFace.DOWN).setType(Material.STONE);
                });
            } else {
                location = higherLandingPosition;
            }
        } else {
            location = safeLandingPosition;
        }
        return location;
    }

    public void tpManualSpawnPoint(Entity entity) {
        Tasks.INSTANCE.sync(() -> {
            if (server.equals(ServerInfoUpdater.getServerName())) {
                entity.teleport(adjustSafeTpLocation(getSpawnLocation()));
            } else {
                throw new RuntimeException("Can't teleport to island in other server");
            }
        });
    }

    public void tp(Entity entity) {
        if (containsFlag("RandomRespawnPoint")) {
            tpRandom(entity);
        } else {
            tpManualSpawnPoint(entity);
        }
    }

    public void tpRandom(Entity entity) {
        Tasks.INSTANCE.async(() -> {
            if (server.equals(ServerInfoUpdater.getServerName())) {
                World spawnBukkitWorld = Bukkit.getWorld(spawnWorld);
                assert spawnBukkitWorld != null;
                Random random = new Random();
                for (int i = 0; i < 1024; i++) {
                    int blockX = (x << 9) + random.nextInt(512);
                    int blockZ = (z << 9) + random.nextInt(512);
                    int y = spawnBukkitWorld.getHighestBlockYAt(blockX, blockZ);
                    if (y > spawnBukkitWorld.getMinHeight()) {
                        y = random.nextInt(spawnBukkitWorld.getMaxHeight() - spawnBukkitWorld.getMinHeight()) + spawnBukkitWorld.getMinHeight();
                        Location location = new Location(spawnBukkitWorld, blockX, y, blockZ);
                        Tasks.INSTANCE.sync(() -> {
                            entity.teleport(adjustSafeTpLocation(location));
                        });
                        return;
                    }
                }
                tpManualSpawnPoint(entity);
            } else {
                throw new RuntimeException("Can't teleport to island in other server");
            }
        });
    }

    public void applyIsland(Runnable runnable) {
        String structure = ResourceUtils.getResourceAsString("island.json");
        CuboidShape cuboidShape = new Gson().fromJson(structure, CuboidShape.class);
        CuboidRegion cuboidRegion = getCuboidRegion(Bukkit.getWorld("SkyWorld"));
        cuboidRegion.getBot().setY(64);
        cuboidRegion.getTop().setY(64);
        cuboidRegion.applyCenter(cuboidShape, 10000, runnable);
    }

    public void clear(Runnable runnable, int timeoutTicks) {
        boolean disableLiquidFlow = containsFlag("DisableLiquidFlow");
        if (!disableLiquidFlow) {
            addIslandFlag("DisableLiquidFlow");
        }
        new PlotAllChunkTask(this, (chunk -> {
            int minHeight = chunk.getWorld().getMinHeight();
            int maxHeight = chunk.getWorld().getMaxHeight();
            for (int i = 0; i < 16; i++) {
                for (int j = minHeight; j < maxHeight; j++) {
                    for (int k = 0; k < 16; k++) {
                        chunk.getBlock(i, j, k).setType(Material.AIR);
                        chunk.getBlock(i, j, k).setBiome(Biome.PLAINS);
                    }
                }
            }

            for (Entity entity : chunk.getEntities()) {
                try {
                    entity.remove();
                } catch (Exception ignore) {
                }
            }

        }), () -> {
            if (!disableLiquidFlow) {
                removeIslandFlag("DisableLiquidFlow");
            }
            runnable.run();
        }, timeoutTicks).run();
    }

    public void clearAndApplyNewIsland(Runnable runnable, int timeoutTicks) {
        clear(() -> applyIsland(runnable), timeoutTicks);
    }

    public void setSpawnWorld(String spawnWorld) {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't edit other servers island!");
        }
        this.spawnWorld = spawnWorld;
        IslandManager.INSTANCE.update(this);
    }

    public void setSpawnX(double spawnX) {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't edit other servers island!");
        }
        this.spawnX = spawnX;
        IslandManager.INSTANCE.update(this);
    }

    public void setSpawnY(double spawnY) {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't edit other servers island!");
        }
        this.spawnY = spawnY;
        IslandManager.INSTANCE.update(this);
    }

    public void setSpawnZ(double spawnZ) {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't edit other servers island!");
        }
        this.spawnZ = spawnZ;
        IslandManager.INSTANCE.update(this);
    }

    public void setYaw(float yaw) {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't edit other servers island!");
        }
        this.yaw = yaw;
        IslandManager.INSTANCE.update(this);
    }


    public void setPitch(float pitch) {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't edit other servers island!");
        }
        this.pitch = pitch;
        IslandManager.INSTANCE.update(this);
    }

    public Location getBottomLocation(World world) {
        int blockX = x << 9;
        int blockZ = z << 9;
        assert world != null;
        return new Location(world, blockX, world.getMinHeight(), blockZ);
    }

    public Location getTopLocation(World world) {
        int blockX = (x + 1) << 9;
        int blockZ = (z + 1) << 9;
        return new Location(world, blockX, world.getMaxHeight(), blockZ);
    }

    public boolean hasPermission(Player target) {
        if (target.getUniqueId().equals(uuid)) {
            return true;
        }
        if (containsFlag("TrustEveryone")) {
            return true;
        }

        return members.contains(target.getUniqueId());
    }


    public void setName(@Nullable String name) {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't edit other servers island!");
        }
        this.name = name;
        IslandManager.INSTANCE.update(this);
    }


    public void addMember(UUID uuid) {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't edit other servers island!");
        }
        members.add(uuid);
        Tasks.INSTANCE.async(() -> IslandManager.INSTANCE.update(this));


        Tasks.INSTANCE.sync(() -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && Objects.equals(IslandManager.INSTANCE.getCurrentIsland(player), this)) {
                PlayerIslandChangeEvent playerIslandChangeEvent = new PlayerIslandChangeEvent(player, this, this);
                Bukkit.getPluginManager().callEvent(playerIslandChangeEvent);
            }
        });

    }

    public void removeMember(UUID uuid) {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't edit other servers island!");
        }
        members.remove(uuid);
        IslandManager.INSTANCE.update(this);

        Tasks.INSTANCE.sync(() -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && Objects.equals(IslandManager.INSTANCE.getCurrentIsland(player), this)) {
                PlayerIslandChangeEvent playerIslandChangeEvent = new PlayerIslandChangeEvent(player, this, this);
                Bukkit.getPluginManager().callEvent(playerIslandChangeEvent);
            }
        });
    }

    public boolean isChangingBiome() {
        return changingBiome;
    }

    public void setBiome(World world, Biome biome, Runnable then) {
        if (changingBiome) {
            return;
        }
        changingBiome = true;
        new PlotChunkTask(world, this, chunk -> {
            int minHeight = chunk.getWorld().getMinHeight();
            int maxHeight = chunk.getWorld().getMaxHeight();
            for (int i = 0; i < 16; i++) {
                for (int j = minHeight; j < maxHeight; j++) {
                    for (int k = 0; k < 16; k++) {
                        chunk.getBlock(i, j, k).setBiome(biome);
                    }
                }
            }
        }, () -> {
            changingBiome = false;
            if (then != null) {
                then.run();
            }
        }, 1024).tickRate(12).run();
    }


    public void addIslandFlag(@NotNull String islandFlag) {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't edit other servers island!");
        }
        this.islandFlags.add(islandFlag);
        IslandFlagManager.INSTANCE.addFlag(this, islandFlag);
        IslandManager.INSTANCE.update(this);
    }


    public void removeIslandFlag(@NotNull String islandFlag) {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't edit other servers island!");
        }
        islandFlags.removeIf((flag) -> {
            String[] split = flag.split("#");
            return split[0].equals(islandFlag);
        });
        IslandFlagManager.INSTANCE.removeFlag(this, islandFlag);
        IslandManager.INSTANCE.update(this);
    }

    public void persist() {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't persist from other server");
        }

        IslandManager.INSTANCE.persist(this);
    }


    public HashSet<Player> getPlayersInIsland() {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't get player in other island!");
        }
        HashSet<Player> players = new HashSet<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (Objects.equals(IslandManager.INSTANCE.getCurrentIsland(onlinePlayer), this)) {
                players.add(onlinePlayer);
            }
        }
        return players;
    }

    public CuboidRegion getCuboidRegion(World world) {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't get cuboid region from other servers island!");
        }
        Location bot = getBottomLocation(world);
        Location top = getTopLocation(world);
        return new CuboidRegion(bot, top);
    }


    public void addVisitRecord(String player) {
        try {
            IslandDao.addVisit(id, player);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected database error!");
        }
    }

    public String getFilename() {
        return "r." + x + "." + z + ".mca";
    }

    private File getOrCreateFile(String filename) {
        File regionSource = new File(filename);
        if (!regionSource.exists()) {
            try {
                boolean newFile = regionSource.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return regionSource;
    }

    public File getRegionFile() {
        return getOrCreateFile("SkyWorld/region/" + getFilename());
    }

    public File getPoiFile() {
        return getOrCreateFile("SkyWorld/poi/" + getFilename());
    }

    public File getEntitiesFile() {

        return getOrCreateFile("SkyWorld/entities/" + getFilename());
    }

    public void setUuid(@NotNull UUID uuid) {
        this.uuid = uuid;
        IslandManager.INSTANCE.update(this);
    }

    public void setIcon(@NotNull String icon) {
        this.icon = icon;
        IslandManager.INSTANCE.update(this);
    }
}
