package com.molean.isletopia.database;

import com.molean.isletopia.statistics.vanilla.Stats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VanillaStatisticsDao {

    public static void checkTable() {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    create table if not exists vanilla_statistics(
                        id int auto_increment primary key,
                        player varchar(100) not null unique,
                        stats longtext not null\s
                    );""";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static void insert(String player, String stats) {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "insert into minecraft.vanilla_statistics(player, stats) VALUES (?,?)";
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
            String sql = "update minecraft.vanilla_statistics set stats=? where player=?";
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
            String sql = "select stats from minecraft.vanilla_statistics where player=?";
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

    public static List<String> players(){
        List<String> list = new ArrayList<>();
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "select player from minecraft.vanilla_statistics where true";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                list.add(resultSet.getString(1));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return list;
    }

    public static void setStatistics(String player, Stats stats) {
        if (query(player) != null) {
            update(player, stats.toString());
        } else {
            insert(player, stats.toString());
        }
    }


    public static Stats getStatistics(String player) {
        if (query(player) != null) {
            return Stats.fromJson(query(player));
        } else {
            return null;
        }
    }

    public static Map<String, Stats> getAllPlayerStats() {
        Map<String, Stats> map =new HashMap<>();
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "select player,stats from minecraft.vanilla_statistics where true";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                try {
                    String player = resultSet.getString(1);
                    String statsString = resultSet.getString(2);
                    Stats stats = Stats.fromJson(statsString);
                    map.put(player, stats);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return map;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

}
