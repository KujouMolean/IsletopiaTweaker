package com.molean.isletopia.database;

import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.utils.UUIDUtils;
import org.bukkit.entity.Player;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerBackupDao {
    public static void checkTable() {
        try (Connection connection = DataSourceUtils.getConnection("backup")) {
            String sql = """
                    create table if not exists player_backup
                    (
                        id     int auto_increment
                            primary key,
                        player varchar(20) not null,
                        data   longblob    not null,
                        time   timestamp  null
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
            String sql = "delete from player_backup where time<?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            LocalDateTime localDateTime = LocalDateTime.now().minusDays(7);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(localDateTime));
            preparedStatement.execute();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void upload(String player) {
        UUID uuid = UUIDUtils.get(player);
        File file = new File(String.format("SkyWorld/playerdata/%s.dat", uuid));
        if (!file.exists()) {
            try {
                boolean newFile = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (Connection connection = DataSourceUtils.getConnection("backup");
             FileInputStream fileInputStream = new FileInputStream(file)
        ) {
            String sql = "insert into player_backup(player, data, time) values(?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, player);
            preparedStatement.setBlob(2, fileInputStream);
            preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            preparedStatement.execute();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static List<Pair<Integer, Timestamp>> list(String player) {
        List<Pair<Integer, Timestamp>> list = new ArrayList<>();
        try (Connection connection = DataSourceUtils.getConnection("backup");) {
            String sql = "select id,time from player_backup where player=? order by time desc";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, player);
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

    public static void restore(Player player, int id) {
        UUID uuid = UUIDUtils.get(player.getName());
        File file = new File(String.format("SkyWorld/playerdata/%s.dat", uuid));
        try (Connection connection = DataSourceUtils.getConnection("backup");
             FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            String sql = "select data from player_backup where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                InputStream binaryStream = resultSet.getBinaryStream(1);
                byte[] bytes = binaryStream.readAllBytes();
                fileOutputStream.write(bytes);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
