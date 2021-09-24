package com.molean.isletopia.database;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;

public class PlayerDataDao {

    public static void checkTable() throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                         create table if not exists playerdata
                         (
                             id       int primary key auto_increment,
                             owner    varchar(100) not null unique,
                             data     longblob     not null,
                             passwd   varchar(100) null default null
                         );""";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

        }
    }

    public static boolean exist(String owner) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    select owner
                    from minecraft.playerdata
                    where owner = ?;
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, owner);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        }
        return false;
    }

    public static void insert(String owner, byte[] data) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    insert into minecraft.playerdata(owner, data)
                    values (?, ?);
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, owner);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            preparedStatement.setBlob(2, byteArrayInputStream);
            preparedStatement.execute();
        }
    }

    public static boolean update(String owner, byte[] data,String passwd) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    update minecraft.playerdata
                    set data=?
                    where owner = ? and passwd = ?;
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            preparedStatement.setBlob(1, byteArrayInputStream);
            preparedStatement.setString(2, owner);
            preparedStatement.setString(3, passwd);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public static boolean complete(String owner, byte[] data,String passwd) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    update minecraft.playerdata
                    set data=? , passwd=null
                    where owner = ? and passwd = ?;
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            preparedStatement.setBlob(1, byteArrayInputStream);
            preparedStatement.setString(2, owner);
            preparedStatement.setString(3, passwd);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public static @Nullable String getLock(String owner) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    update minecraft.playerdata
                    set passwd = ?
                    where owner = ? and passwd is null;
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            String passwd = UUID.randomUUID().toString();
            preparedStatement.setString(1, passwd);
            preparedStatement.setString(2, owner);
            int i = preparedStatement.executeUpdate();
            if (i > 0) {
                return passwd;
            } else {
                return null;
            }


        }
    }

    public static String getLockForce(String owner) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    update minecraft.playerdata
                    set passwd = ?
                    where owner = ?;
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            String passwd = UUID.randomUUID().toString();
            preparedStatement.setString(1, passwd);
            preparedStatement.setString(2, owner);
            int i = preparedStatement.executeUpdate();
            if (i > 0) {
                return passwd;
            } else {
                return null;
            }
        }
    }

    public static byte[] query(String owner, String passwd) throws SQLException, IOException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    select data
                    from minecraft.playerdata
                    where owner = ? and passwd=?;
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, owner);
            preparedStatement.setString(2, passwd);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Blob blob = resultSet.getBlob(1);
                return blob.getBinaryStream().readAllBytes();
            }
        }
        throw new RuntimeException("Player data not exist, check exist before query!");
    }
}
