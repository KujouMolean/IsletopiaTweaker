package com.molean.isletopia.database;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class VanillaStatisticsDao {

    public static void checkTable() {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "create table if not exists vanilla_statistics\n" +
                    "(\n" +
                    "    id     int auto_increment primary key,\n" +
                    "    server varchar(100) not null,\n" +
                    "    player varchar(100) not null,\n" +
                    "    stats  longtext     not null,\n" +
                    "    unique uk_server_player (server, player)\n" +
                    ")";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static boolean exist(String server, String player) {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "select * from vanilla_statistics where player=? and server=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, player);
            preparedStatement.setString(2, server);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;

    }

    private static void insert(String server, String player, String stats) {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "insert into vanilla_statistics(server, player, stats) VALUES (?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, server);
            preparedStatement.setString(2, player);
            preparedStatement.setString(3, stats);
            preparedStatement.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static void update(String server, String player, String stats) {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "update vanilla_statistics set stats=? where server=? and player=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, stats);
            preparedStatement.setString(2, server);
            preparedStatement.setString(3, player);
            preparedStatement.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public static String query(String player, String server) {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "select stats from vanilla_statistics where server=? and player=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, server);
            preparedStatement.setString(2, player);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return resultSet.getString(1);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static void setStatistics(String server, String player, String stats) {
        if (exist(server, player)) {
            update(server, player, stats);
        } else {
            insert(server, player, stats);
        }
    }


    public static String getStatistics(String server, String player) {
        if (exist(server, player)) {
            return query(server, player);
        } else {
            return null;
        }
    }

}
