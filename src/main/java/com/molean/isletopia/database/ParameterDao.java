package com.molean.isletopia.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ParameterDao {
    public static void checkTable() {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "create table if not exists isletopia_parameters\n" +
                    "(\n" +
                    "    id      int auto_increment\n" +
                    "        primary key,\n" +
                    "    player  varchar(100) not null,\n" +
                    "    p_key   varchar(100) not null,\n" +
                    "    p_value text         null,\n" +
                    "    constraint unique_parameter\n" +
                    "        unique (player, p_key)\n" +
                    ");\n";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static String get(String playerName, String key) {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "select p_value from isletopia_parameters where player=? and p_key=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, playerName);
            preparedStatement.setString(2, key);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static ArrayList<String> keys(String playerName) {
        ArrayList<String> keyList = new ArrayList<>();
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "select p_key from isletopia_parameters where player=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, playerName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                keyList.add(resultSet.getString(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
        return keyList;
    }


    public static void set(String playerName, String key, String value) {
        if (value == null || value.isEmpty()) {
            unset(playerName, key);
            return;
        }
        if (!exist(playerName, key)) {
            insert(playerName, key, value);
        } else {
            try (Connection connection = DataSourceUtils.getConnection()) {
                String sql = "update isletopia_parameters set p_value=? where player=? and p_key=?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, value);
                preparedStatement.setString(2, playerName);
                preparedStatement.setString(3, key);
                preparedStatement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public static void unset(String playerName, String key) {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "delete from isletopia_parameters where player=? and p_key=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, playerName);
            preparedStatement.setString(2, key);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void insert(String playerName, String key, String value) {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "insert into isletopia_parameters(player,p_key,p_value) values(?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, playerName);
            preparedStatement.setString(2, key);
            preparedStatement.setString(3, value);
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static boolean exist(String playerName, String key) {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "select * from isletopia_parameters where player=? and p_key=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, playerName);
            preparedStatement.setString(2, key);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

}
