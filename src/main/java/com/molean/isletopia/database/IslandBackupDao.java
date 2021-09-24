package com.molean.isletopia.database;

import com.molean.isletopia.island.IslandId;
import com.molean.isletopia.shared.utils.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class IslandBackupDao {
    public static void checkTable() {
        try (Connection connection = DataSourceUtils.getConnection("backup")) {
            String sql = """
                    create table if not exists island_backup
                       (
                           id int primary key auto_increment,
                           server varchar(100) not null,
                           x int not null ,
                           z int not null ,
                           region longblob not null ,
                           poi longblob not null ,
                           entities longblob not null ,
                           time timestamp not null default CURRENT_TIMESTAMP
                       );
                     """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
            trim();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void trim() {
        try (Connection connection = DataSourceUtils.getConnection("backup")) {
            String sql = "delete from island_backup where time<?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime localDateTime = now.minusDays(1);
            Timestamp timestamp = Timestamp.valueOf(localDateTime);
            preparedStatement.setTimestamp(1, timestamp);
            preparedStatement.execute();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static boolean upload(IslandId islandId, InputStream region, InputStream poi, InputStream entities) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection("backup");) {
            String sql = """
                    insert into island_backup(server, x, z, region, poi, entities)
                    values (?, ?, ?, ?, ?, ?);
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, islandId.getServer());
            preparedStatement.setInt(2, islandId.getX());
            preparedStatement.setInt(3, islandId.getZ());
            preparedStatement.setBlob(4, region);
            preparedStatement.setBlob(5, poi);
            preparedStatement.setBlob(6, entities);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public static List<Pair<Integer, Timestamp>> list(IslandId islandId) {
        List<Pair<Integer, Timestamp>> list = new ArrayList<>();
        try (Connection connection = DataSourceUtils.getConnection("backup");) {
            String sql = """
                    select id,time
                    from island_backup
                    where server=? and x=? and z =?
                    order by time desc;
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, islandId.getServer());
            preparedStatement.setInt(2, islandId.getX());
            preparedStatement.setInt(3, islandId.getZ());

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                Timestamp timestamp = resultSet.getTimestamp(2);
                list.add(new Pair<>(id, timestamp));
            }


        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return list;
    }

    public static long download(int backupId) throws SQLException, IOException {
        long l = System.currentTimeMillis();

        File regionFolder = new File("backup/" + l + "/region");
        if (!regionFolder.exists()) {
            boolean mkdir = regionFolder.mkdirs();
        }


        File poiFolder = new File("backup/" + l + "/region");
        if (!poiFolder.exists()) {
            boolean mkdir = poiFolder.mkdirs();
        }

        File entitiesFolder = new File("backup/" + l + "/region");
        if (!entitiesFolder.exists()) {
            boolean mkdir = entitiesFolder.mkdirs();
        }


        try (Connection connection = DataSourceUtils.getConnection("backup")) {
            String sql = "select x,z,region,poi,entities from island_backup where id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, backupId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int x = resultSet.getInt(1);
                int z = resultSet.getInt(2);
                String filename = "r." + x + "." + z + ".mca";
                Blob regionBlob = resultSet.getBlob(3);
                Blob poiBlob = resultSet.getBlob(4);
                Blob entitiesBlob = resultSet.getBlob(5);

                try (FileOutputStream region = new FileOutputStream(regionFolder + "/" + filename);
                     InputStream regionInputStream = regionBlob.getBinaryStream();
                     FileOutputStream poi = new FileOutputStream(poiFolder + "/" + filename);
                     InputStream poiInputStream = poiBlob.getBinaryStream();
                     FileOutputStream entities = new FileOutputStream(entitiesFolder + "/" + filename);
                     InputStream entitiesInputStream = entitiesBlob.getBinaryStream()) {
                    region.write(regionInputStream.readAllBytes());
                    poi.write(poiInputStream.readAllBytes());
                    entities.write(entitiesInputStream.readAllBytes());
                }
            }
        }
        return l;
    }
}
