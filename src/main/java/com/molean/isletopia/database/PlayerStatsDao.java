package com.molean.isletopia.database;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;

public class PlayerStatsDao {
    public static void checkTable() throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                        create table if not exists player_stats
                       (
                           id       int primary key auto_increment,
                           owner    varchar(100) not null unique,
                           stats    longtext     not null,
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
                    from minecraft.player_stats
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

    public static void insert(String owner, String stats) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    insert into minecraft.player_stats(owner, stats)
                    values (?, ?);
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, owner);

            preparedStatement.setString(2, stats);
            preparedStatement.execute();
        }
    }

    public static boolean update(String owner, String stats, String passwd) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    update minecraft.player_stats
                    set stats=?
                    where owner = ? and passwd = ?;
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, stats);
            preparedStatement.setString(2, owner);
            preparedStatement.setString(3, passwd);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public static boolean complete(String owner, String stats, String passwd) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    update minecraft.player_stats
                    set stats=? , passwd=null
                    where owner = ? and passwd = ?;
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, stats);
            preparedStatement.setString(2, owner);
            preparedStatement.setString(3, passwd);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public static @Nullable String getLock(String owner) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    update minecraft.player_stats
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
                    update minecraft.player_stats
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

    public static String query(String owner, String passwd) throws SQLException, IOException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    select stats
                    from minecraft.player_stats
                    where owner = ? and passwd=?;
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, owner);
            preparedStatement.setString(2, passwd);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        }
        throw new RuntimeException("Player stats not exist, check exist before query!");
    }
}
