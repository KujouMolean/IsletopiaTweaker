package com.molean.isletopia.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class CustomStatisticsDao {

    public static void checkTable() {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    create table if not exists isletopia_statistics
                    (
                        id    int auto_increment primary key,
                        player text not null,
                        name  text not null,
                        count int  not null,
                        constraint unique_parameter
                            unique (player, name)
                    )""";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static boolean exist(String player, String name) {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "select * from isletopia_statistics where player=? and name=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, player);
            preparedStatement.setString(2, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;

    }

    private static void insert(String player, String name, int count) {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "insert into isletopia_statistics(player, name, count) VALUES (?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, player);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, count);
            preparedStatement.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static void update(String player, String name, int count) {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "update isletopia_statistics set count=? where player=? and name=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, count);
            preparedStatement.setString(2, player);
            preparedStatement.setString(3, name);
            preparedStatement.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public static int query(String player, String name) {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "select count from isletopia_statistics where player=? and name=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, player);
            preparedStatement.setString(2, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return resultSet.getInt(1);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public static void setStatistics(String player, String name, int count) {
        if (exist(player, name)) {
            update(player, name, count);
        } else {
            insert(player, name, count);
        }
    }


    public static int getStatistics(String player, String name) {
        if (exist(player, name)) {
            return query(player, name);
        } else {
            return 0;
        }
    }


    public static void increaseStatistics(String player, String name, int count) {
        if (exist(player, name)) {
            int old = getStatistics(player, name);
            update(player, name, old + count);
        } else {
            insert(player, name, count);
        }
    }
}
