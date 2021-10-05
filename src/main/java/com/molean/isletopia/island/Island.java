package com.molean.isletopia.island;

import com.google.gson.Gson;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.IslandDao;
import com.molean.isletopia.event.PlayerIslandChangeEvent;
import com.molean.isletopia.island.obj.CuboidRegion;
import com.molean.isletopia.island.obj.CuboidShape;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.task.PlotChunkTask;
import com.molean.isletopia.utils.ResourceUtils;
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

public class Island {
    private final int id;
    private final int x;
    private final int z;
    @NotNull
    private final String server;
    private double spawnX;
    private double spawnY;
    private double spawnZ;
    private float yaw;
    private float pitch;
    @NotNull
    private String owner;
    @Nullable
    private String name;
    @NotNull
    private Biome biome = Biome.PLAINS;
    @NotNull
    private final Timestamp creation;
    @NotNull
    private final Set<String> members = new HashSet<>();
    @NotNull
    private final Set<String> islandFlags = new HashSet<>();

    public Island(int id, int x, int z, double spawnX, double spawnY, double spawnZ, float yaw, float pitch, @NotNull String server, @NotNull String owner, @Nullable String name, @Nullable Biome biome, @NotNull Timestamp creation, Set<String> members, Set<String> islandFlags) {
        this.id = id;
        this.x = x;
        this.z = z;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.spawnZ = spawnZ;
        this.yaw = yaw;
        this.pitch = pitch;
        this.server = server;
        this.owner = owner;
        this.name = name;
        if (biome != null) {
            this.biome = biome;
        }
        this.creation = creation;
        this.members.addAll(members);
        this.islandFlags.addAll(islandFlags);
    }

    public IslandId getIslandId() {
        return new IslandId(server, x, z);
    }


    @Nullable
    private Location getSafeLandingPosition(@NotNull Location location) {
        for (int i = 0; i < location.getY(); i++) {
            Location add = location.clone().add(0, -i, 0);
            if (add.getBlock().getRelative(BlockFace.DOWN).isSolid() && add.getBlock().getType().isAir() && add.getBlock().getRelative(BlockFace.UP).getType().isAir()) {
                return add;
            }
        }
        return null;
    }

    private Location getHigherLandingPosition(Location location) {
        for (int i = 0; i < 256 - location.getY(); i++) {
            Location add = location.clone().add(0, i, 0);
            if (add.getBlock().getRelative(BlockFace.DOWN).isSolid() && add.getBlock().getType().isAir() && add.getBlock().getRelative(BlockFace.UP).getType().isAir()) {
                return add;
            }
        }
        return null;
    }


    public void tp(Entity entity) {
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
            if (server.equals(ServerInfoUpdater.getServerName())) {
                World skyWorld = Bukkit.getWorld("SkyWorld");
                Location location = new Location(skyWorld, (x << 9) + spawnX, spawnY, (z << 9) + spawnZ, yaw, pitch);

                Location safeLandingPosition = getSafeLandingPosition(location);
                if (safeLandingPosition == null) {
                    Location higherLandingPosition = getHigherLandingPosition(location);
                    if (higherLandingPosition == null) {
                        location.getBlock().getRelative(BlockFace.DOWN).setType(Material.STONE);
                    }else{
                        location = higherLandingPosition;
                    }
                } else {
                    location = safeLandingPosition;
                }

                entity.teleport(location);


            } else {
                throw new RuntimeException("Can't teleport to island in other server");
            }
        });
    }

    public void applyIsland(Runnable runnable) {
        String structure = ResourceUtils.getResourceAsString("island.json");
        CuboidShape cuboidShape = new Gson().fromJson(structure, CuboidShape.class);
        CuboidRegion cuboidRegion = getCuboidRegion();
        cuboidRegion.getBot().setY(64);
        cuboidRegion.getTop().setY(64);
        cuboidRegion.applyCenter(cuboidShape, 10000, runnable);
    }

    public void clear(Runnable runnable, int timeoutTicks) {
        boolean disableLiquidFlow = containsFlag("DisableLiquidFlow");

        if (!disableLiquidFlow) {
            addIslandFlag("DisableLiquidFlow");

        }
        new PlotChunkTask(this, (chunk -> {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 256; j++) {
                    for (int k = 0; k < 16; k++) {
                        chunk.getBlock(i, j, k).setType(Material.AIR);
                        chunk.getBlock(i, j, k).setBiome(biome);
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

        clear(() -> {
            applyIsland(runnable);
        }, timeoutTicks);
    }


    public double getSpawnX() {
        return spawnX;
    }

    public void setSpawnX(double spawnX) {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't edit other servers island!");
        }
        this.spawnX = spawnX;
        IslandManager.INSTANCE.update(this);
    }

    public double getSpawnY() {
        return spawnY;
    }

    public void setSpawnY(double spawnY) {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't edit other servers island!");
        }
        this.spawnY = spawnY;
        IslandManager.INSTANCE.update(this);
    }

    public double getSpawnZ() {
        return spawnZ;
    }

    public void setSpawnZ(double spawnZ) {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't edit other servers island!");
        }
        this.spawnZ = spawnZ;
        IslandManager.INSTANCE.update(this);
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't edit other servers island!");
        }
        this.yaw = yaw;
        IslandManager.INSTANCE.update(this);
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't edit other servers island!");
        }
        this.pitch = pitch;
        IslandManager.INSTANCE.update(this);
    }

    public Location getBottomLocation() {
        World skyWorld = Bukkit.getWorld("SkyWorld");
        int blockX = x << 9;
        int blockZ = z << 9;
        return new Location(skyWorld, blockX, 0, blockZ);
    }

    public Location getTopLocation() {
        World skyWorld = Bukkit.getWorld("SkyWorld");
        int blockX = (x + 1) << 9;
        int blockZ = (z + 1) << 9;
        return new Location(skyWorld, blockX, 256, blockZ);
    }

    public boolean hasPermission(String target) {
        if (target.equals(owner)) {
            return true;
        }
        return members.contains(target);
    }

    public boolean hasPermission(Player target) {
        return hasPermission(target.getName());
    }

    public @NotNull String getServer() {
        return server;
    }

    public @Nullable String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't edit other servers island!");
        }
        this.name = name;
        IslandManager.INSTANCE.update(this);
    }


    public @NotNull Biome getBiome() {
        return biome;
    }

    public void setBiome(@Nullable Biome biome) {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't edit other servers island!");
        }
        this.biome = biome;
        IslandManager.INSTANCE.update(this);
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }


    public @NotNull String getOwner() {
        return owner;
    }

    public void setOwner(@NotNull String owner) {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't edit other servers island!");
        }
        this.owner = owner;
        IslandManager.INSTANCE.update(this);
    }

    public @NotNull Timestamp getCreation() {
        return creation;
    }

    public @NotNull List<String> getMembers() {
        return new ArrayList<>(members);
    }

    public void addMember(String member) {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't edit other servers island!");
        }
        members.add(member);
        IslandManager.INSTANCE.update(this);


        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
            Player player = Bukkit.getPlayerExact(member);
            if (player != null && Objects.equals(IslandManager.INSTANCE.getCurrentIsland(player), this)) {
                PlayerIslandChangeEvent playerIslandChangeEvent = new PlayerIslandChangeEvent(player, this, this);
                Bukkit.getPluginManager().callEvent(playerIslandChangeEvent);
            }
        });

    }

    public void removeMember(String member) {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't edit other servers island!");
        }
        members.remove(member);
        IslandManager.INSTANCE.update(this);

        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
            Player player = Bukkit.getPlayerExact(member);
            if (player != null && Objects.equals(IslandManager.INSTANCE.getCurrentIsland(player), this)) {
                PlayerIslandChangeEvent playerIslandChangeEvent = new PlayerIslandChangeEvent(player, this, this);
                Bukkit.getPluginManager().callEvent(playerIslandChangeEvent);
            }
        });
    }


    public @NotNull Set<String> getIslandFlags() {
        return new HashSet<>(islandFlags);
    }

    public void addIslandFlag(@NotNull String islandFlag) {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't edit other servers island!");
        }
        this.islandFlags.add(islandFlag);
        IslandFlagManager.INSTANCE.addFlag(this, islandFlag);
        IslandManager.INSTANCE.update(this);
    }

    public boolean containsFlag(String key) {
        for (String islandFlag : islandFlags) {
            String[] split = islandFlag.split("#");
            if (split[0].equals(key)) {
                return true;
            }
        }
        return false;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Island island = (Island) o;

        if (x != island.x) return false;
        if (z != island.z) return false;
        return server.equals(island.server);
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + z;
        result = 31 * result + server.hashCode();
        return result;
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

    public CuboidRegion getCuboidRegion() {
        if (!ServerInfoUpdater.getServerName().equals(server)) {
            throw new RuntimeException("Can't get cuboid region from other servers island!");
        }
        Location bot = getBottomLocation();
        Location top = getTopLocation();
        return new CuboidRegion(bot, top);
    }

    @Override
    public String toString() {
        return "Island{" +
                "id=" + id +
                ", x=" + x +
                ", z=" + z +
                ", server='" + server + '\'' +
                '}';
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
}
