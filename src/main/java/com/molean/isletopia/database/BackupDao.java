package com.molean.isletopia.database;

import com.molean.isletopia.bungee.individual.ServerInfoUpdater;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class BackupDao {
    public static void checkTable() {
        try (Connection connection = DataSourceUtils.getConnection("backup")) {
            String sql = "create table if not exists " + ServerInfoUpdater.getServerName() + "\n" +
                    "(\n" +
                    "    id       int auto_increment primary key,\n" +
                    "    filename text     not null,\n" +
                    "    data     longblob not null,\n" +
                    "    time     long     not null\n" +
                    ");";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void trim() {
        try (Connection connection = DataSourceUtils.getConnection("backup")) {
            String sql = "delete from " + ServerInfoUpdater.getServerName() + " where time < ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, System.currentTimeMillis() - 8 * 60 * 60 * 1000);
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void upload(String path) {
        trim();
        File file = new File(path);
        try (Connection connection = DataSourceUtils.getConnection("backup");
             FileInputStream inputStream = new FileInputStream(file)) {
            String sql = "insert into " + ServerInfoUpdater.getServerName() + "(filename, data, time) VALUES (?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, file.getName());
            preparedStatement.setBlob(2, inputStream);
            preparedStatement.setLong(3, System.currentTimeMillis());
            preparedStatement.execute();
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }

    public static Map<Integer, Long> list(String filename) {
        Map<Integer, Long> map = new HashMap<>();
        try (Connection connection = DataSourceUtils.getConnection("backup")) {
            String sql = "select id,time from " + ServerInfoUpdater.getServerName() + " where filename=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, filename);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                long time = resultSet.getLong(2);
                map.put(id, time);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return map;
    }

    public static void download(int id) {
        File backupFolder = new File("backup");
        if (!backupFolder.exists()) {
            boolean mkdir = backupFolder.mkdir();
        }
        try (Connection connection = DataSourceUtils.getConnection("backup")) {
            String sql = "select filename,data from " + ServerInfoUpdater.getServerName() + " where id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String filename = resultSet.getString(1);
                Blob blob = resultSet.getBlob(2);
                try (FileOutputStream fileOutputStream = new FileOutputStream(backupFolder + "/" + filename);
                     InputStream inputStream = blob.getBinaryStream()) {
                    fileOutputStream.write(inputStream.readAllBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }


}
