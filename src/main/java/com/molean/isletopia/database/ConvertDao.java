package com.molean.isletopia.database;

import com.molean.isletopia.island.Island;
import com.molean.isletopia.utils.UUIDUtils;
import org.bukkit.block.Biome;

import java.sql.*;
import java.util.*;

public class ConvertDao {

    public static Map<UUID, String> getUUIDMap() throws SQLException {

        HashMap<UUID, String> uuidStringHashMap = new HashMap<>();

        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    select distinct player from minecraft.isletopia_parameters
                        where p_key='server'
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String string = resultSet.getString(1);
                uuidStringHashMap.put(UUIDUtils.get(string), string);
            }
        }
        return uuidStringHashMap;
    }

    public static Set<Island> getIslandFromPlot(String server) throws SQLException {
        Map<UUID, String> uuidMap = getUUIDMap();

        HashSet<Island> islands = new HashSet<>();


        try (Connection connection = DataSourceUtils.getConnection(server)) {
            @SuppressWarnings("all")
            String sql = """
                    select id, plot_id_x, plot_id_z, owner, timestamp
                    from plot;
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                int x = resultSet.getInt(2);
                int z = resultSet.getInt(3);
                String ownerUUIDString = resultSet.getString(4);
                UUID ownerUUID = UUID.fromString(ownerUUIDString);
                String owner;
                if (uuidMap.containsKey(ownerUUID)) {
                    owner = uuidMap.get(ownerUUID);
                } else {
                    System.out.println("Unknown owner uuid: " + ownerUUID + ", skip island " + id + "(" + x + "," + z + ")");
                    continue;

                }
                Timestamp timestamp = resultSet.getTimestamp(5);
                HashSet<String> members = new HashSet<>();
                @SuppressWarnings("all")
                String sql1 = """
                        select user_uuid
                        from plot_helpers
                        where plot_plot_id = ?;
                        """;
                PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
                preparedStatement1.setInt(1, id);
                ResultSet resultSet1 = preparedStatement1.executeQuery();
                while (resultSet1.next()) {
                    String string = resultSet1.getString(1);
                    UUID uuid = UUID.fromString(string);
                    if (uuidMap.containsKey(uuid)) {
                        members.add(uuidMap.get(uuid));
                    } else {
                        System.out.println("Unknown uuid: " + string);
                    }
                }
                @SuppressWarnings("all")
                String sql2 = """
                        select biome,alias,position
                        from plot_settings
                        where plot_plot_id = ?;
                        """;
                PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);
                preparedStatement2.setInt(1, id);
                ResultSet resultSet2 = preparedStatement2.executeQuery();

                double spawnX;
                double spawnY;
                double spawnZ;
                float yaw;
                float pitch;
                String biomeString;
                String alias = null;
                String position;
                Biome biome = null;

                if (resultSet2.next()) {

                    biomeString = resultSet2.getString(1);
                    alias = resultSet2.getString(2);
                    position = resultSet2.getString(3);
                    try {
                        biome = Biome.valueOf(biomeString);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Unable parse biome string:" + biomeString);
                    }

                    if (biome == null) {
                        biome = Biome.PLAINS;
                    }

                    if (position == null || position.isEmpty() || position.equalsIgnoreCase("DEFAULT")) {
                        spawnX = 256;
                        spawnY = 128;
                        spawnZ = 256;
                        yaw = 0.0f;
                        pitch = 0.0f;
                    } else {
                        String[] split = position.split(",");
                        if (split.length != 5) {
                            System.out.println("Unable parse position string:" + position);
                            spawnX = 256;
                            spawnY = 128;
                            spawnZ = 256;
                            yaw = 0.0f;
                            pitch = 0.0f;
                        } else {
                            try {
                                spawnX = Double.parseDouble(split[0]);
                                spawnY = Double.parseDouble(split[1]);
                                spawnZ = Double.parseDouble(split[2]);
                                yaw = Float.parseFloat(split[3]);
                                pitch = Float.parseFloat(split[4]);
                            } catch (NumberFormatException e) {
                                System.out.println("Unable parse position string:" + position);
                                spawnX = 256;
                                spawnY = 128;
                                spawnZ = 256;
                                yaw = 0.0f;
                                pitch = 0.0f;
                            }
                        }
                    }
                } else {
                    spawnX = 256;
                    spawnY = 128;
                    spawnZ = 256;
                    yaw = 0.0f;
                    pitch = 0.0f;
                    biome = Biome.PLAINS;
                }

                Island island = new Island(0, x - 1, z - 1, spawnX, spawnY, spawnZ, yaw, pitch, server, owner, alias, biome, timestamp, members, new HashSet<>());
                islands.add(island);
            }
        }
        return islands;
    }


    public static void importFromPlot(String server) throws SQLException {
        Set<Island> islandFromPlot = getIslandFromPlot(server);

        int success = 0;
        int failed = 0;
        int skip = 0;
        for (Island island : islandFromPlot) {

            Island islandByIslandId = IslandDao.getIslandByIslandId(island.getIslandId());
            if (islandByIslandId != null) {
                System.out.println(islandByIslandId + " already exist! (skip import)");
                skip++;
                continue;
            }
            IslandDao.createIsland(island);

            if (IslandDao.getIslandByIslandId(island.getIslandId()) != null) {
                System.out.println(island + " import successfully!");
                success++;
            } else {
                System.out.println(island+" import failed!");
                failed++;
            }

        }

        System.out.println("Import " + success + " island from " + server + "!");
        System.out.println(failed + " failed and " + skip + "skip!");
    }
}
