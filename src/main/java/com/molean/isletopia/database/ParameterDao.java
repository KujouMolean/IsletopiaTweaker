package com.molean.isletopia.database;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParameterDao {
    private static final MysqlConnectionPoolDataSource dataSource;

    static {
        dataSource = new MysqlConnectionPoolDataSource();
        String url = "jdbc:mysql://localhost/minecraft?useSSL=false&characterEncoding=utf8&serverTimezone=UTC";
        String username = "molean";
        String password = "123asd";
        dataSource.setUrl(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }

    public static String get(String playerName, String key) {
        try (Connection connection = getConnection()) {
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

    public static void set(String playerName, String key, String value) {
        if (!exist(playerName, key)) {
            insert(playerName, key, value);
        } else {
            try (Connection connection = getConnection()) {
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

    public static void insert(String playerName, String key, String value) {
        try (Connection connection = getConnection()) {
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
        try (Connection connection = getConnection()) {
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
