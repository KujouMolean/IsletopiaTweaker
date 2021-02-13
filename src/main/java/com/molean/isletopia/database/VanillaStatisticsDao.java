package com.molean.isletopia.database;

import com.molean.isletopia.statistics.individual.vanilla.Stats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class VanillaStatisticsDao {

    public static void checkTable() {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "create table if not exists vanilla_statistics(\n" +
                    "    id int auto_increment primary key,\n" +
                    "    player varchar(100) not null ,\n" +
                    "    stats longtext not null \n" +
                    ");";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static boolean exist(String player) {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "select * from vanilla_statistics where player=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, player);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;

    }

    private static void insert(String player, String stats) {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "insert into vanilla_statistics(player, stats) VALUES (?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, player);
            preparedStatement.setString(2, stats);
            preparedStatement.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static void update(String player, String stats) {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "update vanilla_statistics set stats=? where player=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, stats);
            preparedStatement.setString(2, player);
            preparedStatement.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public static String query(String player) {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "select stats from vanilla_statistics where player=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, player);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return resultSet.getString(1);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static void setStatistics(String player, Stats stats) {
        if (exist(player)) {
            update(player, stats.toString());
        } else {
            insert(player, stats.toString());
        }
    }


    public static Stats getStatistics(String player) {
        if (exist(player)) {
            return Stats.fromJson(query(player));
        } else {
            return null;
        }
    }

}
