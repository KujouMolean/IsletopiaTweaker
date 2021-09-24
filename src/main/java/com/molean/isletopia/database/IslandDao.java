package com.molean.isletopia.database;

import com.molean.isletopia.island.Island;
import com.molean.isletopia.island.IslandId;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.shared.utils.Pair;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.Nullable;

import java.awt.image.DataBuffer;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IslandDao {

    public static void checkTable() throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String createIslandTable = """
                    create table if not exists island
                       (
                           id     int primary key auto_increment,
                           x      int    not null,
                           z      int    not null,
                           spawnX double not null default 256,
                           spawnY double not null default 256,
                           spawnZ double not null default 128,
                           yaw    float  not null default 0,
                           pitch  float  not null default 0,
                           server   varchar(100) not null,
                           owner    varchar(100) not null,
                           name     text         null,
                           biome    varchar(100) not null default 'PLAINS',
                           creation timestamp    not null default CURRENT_TIMESTAMP,
                           constraint  island_pk   unique (server, x, z)
                       );
                    """;
            String createMemberTable = """
                    create table if not exists island_member
                    (
                        id        int primary key auto_increment,
                        island_id int          not null,
                        member    varchar(100) not null,
                        foreign key (island_id) references minecraft.island (id),
                        constraint unique_member
                            unique (island_id, member)
                    );
                    """;
            String createFlagTable = """
                    create table if not exists island_flag
                    (
                        id        int primary key auto_increment,
                        island_id int  not null,
                        flag      text not null,
                        foreign key (island_id) references minecraft.island (id)
                    );
                    """;
            String createVisitorTable = """
                    create table if not exists island_visit
                    (
                        id        int primary key auto_increment,
                        island_id int          not null,
                        visitor   varchar(100) not null,
                        time      timestamp    not null default CURRENT_TIMESTAMP,
                        foreign key (island_id) references minecraft.island (id)
                    );
                    """;
            connection.prepareStatement(createIslandTable).execute();
            connection.prepareStatement(createMemberTable).execute();
            connection.prepareStatement(createFlagTable).execute();
            connection.prepareStatement(createVisitorTable).execute();
        }
    }

    public static Set<String> getIslandMember(int id) throws SQLException {
        HashSet<String> strings = new HashSet<>();
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    select member
                    from minecraft.island_member
                    where island_id = ?;
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                strings.add(resultSet.getString(1));
            }
        }
        return strings;
    }

    public static void addMember(Island island, String member) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    insert into minecraft.island_member(island_id, member)
                              values (?, ?)
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, island.getId());
            preparedStatement.setString(2, member);
            preparedStatement.execute();
        }
    }

    public static void removeMember(Island island, String member) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    delete
                    from minecraft.island_member
                    where island_id = ?
                      and member = ?
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, island.getId());
            preparedStatement.setString(2, member);
            preparedStatement.execute();
        }
    }

    public static Set<String> getIslandFlag(int id) throws SQLException {
        HashSet<String> strings = new HashSet<>();
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    select flag
                    from minecraft.island_flag
                    where island_id = ?
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                strings.add(resultSet.getString(1));
            }
        }
        return strings;
    }

    public static void removeFlag(Island island, String key) throws SQLException {
        Set<String> islandFlag = getIslandFlag(island.getId());
        for (String s : islandFlag) {
            if (s.split("#")[0].equals(key)) {
                try (Connection connection = DataSourceUtils.getConnection()) {
                    String sql = """
                                delete
                                from minecraft.island_flag
                                where island_id = ?
                                  and flag = ?
                            """;
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setInt(1, island.getId());
                    preparedStatement.setString(2, s);
                    preparedStatement.execute();
                }
            }
        }
    }

    public static void addFlag(Island island, String flag) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    insert into minecraft.island_flag(island_id, flag)
                                values (?, ?)
                      """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, island.getId());
            preparedStatement.setString(2, flag);
            preparedStatement.execute();
        }
    }

    public static void createIsland(Island island) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    insert into minecraft.island(x, z, spawnX, spawnY, spawnZ,yaw,pitch, server, owner, biome, creation)
                                           values (?, ?, ?, ?, ?, ?,?,?, ?, ?, ?)
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, island.getX());

            preparedStatement.setInt(2, island.getZ());
            preparedStatement.setDouble(3, island.getSpawnX());
            preparedStatement.setDouble(4, island.getSpawnY());
            preparedStatement.setDouble(5, island.getSpawnZ());
            preparedStatement.setFloat(6, island.getYaw());
            preparedStatement.setFloat(7, island.getPitch());
            preparedStatement.setString(8, island.getServer());
            preparedStatement.setString(9, island.getOwner());
            String biome = null;
            try {
                biome = island.getBiome().name();
            } catch (Exception ignore) {
            }
            preparedStatement.setString(10, biome);
            preparedStatement.setTimestamp(11, island.getCreation());
            preparedStatement.execute();
            List<String> members = island.getMembers();
            Set<String> islandFlags = island.getIslandFlags();
            Island islandByIslandId = getIslandByIslandId(island.getIslandId());
            if (islandByIslandId == null) {
                throw new RuntimeException("Unexpected database error!");
            }
            for (String member : members) {
                addMember(islandByIslandId, member);
            }
            for (String islandFlag : islandFlags) {
                addFlag(islandByIslandId, islandFlag);
            }

        }

    }

    public static void updateIsland(Island island) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    update minecraft.island
                    set spawnX=?,
                        spawnY=?,
                        spawnZ=?,
                        yaw=?,
                        pitch=?,
                        owner=?,
                        biome=?,
                        name=?,
                        creation=?
                    where id = ?;
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDouble(1, island.getSpawnX());
            preparedStatement.setDouble(2, island.getSpawnY());
            preparedStatement.setDouble(3, island.getSpawnZ());
            preparedStatement.setFloat(4, island.getYaw());
            preparedStatement.setFloat(5, island.getPitch());
            preparedStatement.setString(6, island.getOwner());
            String biome = null;
            try {
                biome = island.getBiome().name();
            } catch (Exception ignored) {
            }
            preparedStatement.setString(7, biome);
            preparedStatement.setString(8, island.getName());
            preparedStatement.setTimestamp(9, island.getCreation());
            preparedStatement.setInt(10, island.getId());
            preparedStatement.execute();
        }


        //update member
        Set<String> oldMember = getIslandMember(island.getId());
        for (String s : oldMember) {
            if (!island.getMembers().contains(s)) {
                removeMember(island, s);
            }
        }

        for (String member : island.getMembers()) {
            if (!oldMember.contains(member)) {
                addMember(island, member);
            }
        }

        //update flag
        Set<String> oldFlags = getIslandFlag(island.getId());
        for (String s : oldFlags) {
            if (!island.getIslandFlags().contains(s)) {
                removeFlag(island, s.split("#")[0]);
            }
        }

        for (String flag : island.getIslandFlags()) {
            if (!oldFlags.contains(flag)) {
                addFlag(island, flag);
            }
        }
    }


    @Nullable
    public static Integer countIslandByPlayer(String owner) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    select count(*)
                    from minecraft.island
                    where owner = ?;
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, owner);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return null;
    }


    public static Integer countIslandByServer(String server) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    select count(*)
                    from minecraft.island
                    where server = ?;
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, server);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return null;
    }

    public static HashSet<Island> parseIsland(ResultSet resultSet) throws SQLException {
        HashSet<Island> islands = new HashSet<>();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            int x = resultSet.getInt("x");
            int z = resultSet.getInt("z");
            double spawnX = resultSet.getDouble("spawnX");
            double spawnY = resultSet.getDouble("spawnY");
            double spawnZ = resultSet.getDouble("spawnZ");
            float yaw = resultSet.getFloat("yaw");
            float pitch = resultSet.getFloat("pitch");
            String server = resultSet.getString("server");
            String owner = resultSet.getString("owner");
            String name = resultSet.getString("name");
            Biome biome = null;
            try {
                biome = Biome.valueOf(resultSet.getString("biome"));
            } catch (Exception ignored) {
            }
            Timestamp creation = resultSet.getTimestamp("creation");
            Set<String> islandMember = getIslandMember(id);
            Island island = new Island(id, x, z, spawnX, spawnY, spawnZ, yaw, pitch, server, owner, name, biome, creation, islandMember, new HashSet<>());
            islands.add(island);
        }
        return islands;
    }


    public static List<IslandId> getPlayerIslandIds(String owner) throws SQLException {
        List<IslandId> islandIds = new ArrayList<>();
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "select server,x,z from minecraft.island where owner = ? order by id";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, owner);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String string = resultSet.getString(1);
                int x = resultSet.getInt(2);
                int z = resultSet.getInt(3);
                islandIds.add(new IslandId(string, x, z));
            }
        }
        return islandIds;
    }

    @Nullable
    public static Island getIslandByIslandId(IslandId islandId) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "select * from minecraft.island where x = ? and z = ? and server = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, islandId.getX());
            preparedStatement.setInt(2, islandId.getZ());
            preparedStatement.setString(3, islandId.getServer());
            ResultSet resultSet = preparedStatement.executeQuery();
            HashSet<Island> islands = parseIsland(resultSet);
            if (!islands.isEmpty()) {
                return new ArrayList<>(islands).get(0);
            }
        }
        return null;
    }


    public static Set<IslandId> getAllIslandId(String owner) throws SQLException {
        HashSet<IslandId> islandIds = new HashSet<>();
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "select server,x,z from minecraft.island where owner = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, owner);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String string = resultSet.getString(1);
                int x = resultSet.getInt(2);
                int z = resultSet.getInt(3);
                islandIds.add(new IslandId(string, x, z));
            }
        }
        return islandIds;
    }

    public static Set<IslandId> getLocalServerIslandIds(String server) throws SQLException {
        HashSet<IslandId> islandIds = new HashSet<>();
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "select server,x,z from minecraft.island where server = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, server);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String string = resultSet.getString(1);
                int x = resultSet.getInt(2);
                int z = resultSet.getInt(3);
                islandIds.add(new IslandId(string, x, z));
            }
        }
        return islandIds;
    }

    public static List<IslandId> getPlayerLocalServerIslands(String owner) throws SQLException {
        List<IslandId> islandIds = new ArrayList<>();
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "select server,x,z from minecraft.island where owner = ? and server=? order by id";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, owner);
            preparedStatement.setString(2, ServerInfoUpdater.getServerName());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String string = resultSet.getString(1);
                int x = resultSet.getInt(2);
                int z = resultSet.getInt(3);
                islandIds.add(new IslandId(string, x, z));
            }
        }
        return islandIds;
    }


    public static IslandId getPlayerLocalServerFirstIsland(String owner) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "select server,x,z from minecraft.island where server = ? and owner = ? order by id";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, ServerInfoUpdater.getServerName());
            preparedStatement.setString(2, owner);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String string = resultSet.getString(1);
                int x = resultSet.getInt(2);
                int z = resultSet.getInt(3);
                return new IslandId(string, x, z);
            }
        }
        return null;
    }

    public static IslandId getPlayerFirstIsland(String owner) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "select server,x,z from minecraft.island where server = ? order by id";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, owner);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String string = resultSet.getString(1);
                int x = resultSet.getInt(2);
                int z = resultSet.getInt(3);
                return new IslandId(string, x, z);
            }
        }
        return null;
    }

    public static void delete(int id) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            {
                //clear member
                String sql = "delete from minecraft.island_member where island_id=?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, id);
                preparedStatement.execute();
            }
            {
                //clear flag
                String sql = "delete from minecraft.island_flag where island_id=?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, id);
                preparedStatement.execute();

            }
            {
                String sql = "delete from minecraft.island_visit where island_id=?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, id);
                preparedStatement.execute();
            }
            {
                //remove record
                String sql = "delete from minecraft.island where id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, id);
                preparedStatement.execute();
            }
        }
    }

    public static void addVisit(int id, String visitor) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    insert into minecraft.island_visit(island_id, visitor)
                    values (?, ?);
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, visitor);
            preparedStatement.execute();
        }
    }

    public static List<Pair<String, Timestamp>> queryVisit(int id, int day) throws SQLException {
        ArrayList<Pair<String, Timestamp>> pairs = new ArrayList<>();
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    select visitor, time
                    from minecraft.island_visit
                    where minecraft.island_visit.island_id = ?
                      and island_visit.time > ?
                    order by time desc;
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now().minusDays(day)));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String visitor = resultSet.getString(1);
                Timestamp timestamp = resultSet.getTimestamp(2);
                pairs.add(new Pair<>(visitor, timestamp));
            }
        }
        return pairs;
    }
}
