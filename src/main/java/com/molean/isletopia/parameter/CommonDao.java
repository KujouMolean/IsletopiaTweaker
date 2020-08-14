package com.molean.isletopia.parameter;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CommonDao {
    private static MysqlDataSource dataSource;

    static {
        dataSource = new MysqlDataSource();
        String url = "jdbc:mysql://localhost/minecraft?useSSL=false";
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

    public static String get(String table, String key_value, String column, String key) {
        String result = null;
        if (!exist(table, key_value, key)) {
            return null;
        }
        try (Connection connection = getConnection()) {
            String sql = "select " + column + " from " + table + " where " + key + "=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, key_value);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getString(1);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public static boolean set(String table, String key_value, String column, String value, String key) {
        if (!exist(table, key_value, key)) insert(table, key_value, key);

        boolean execute = false;
        try (Connection connection = getConnection()) {
            String sql = "update " + table + " set " + column + "=? where " + key + "=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, value);
            preparedStatement.setString(2, key_value);
            execute = preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return execute;
    }

    public static boolean exist(String table, String key_value, String key) {
        String string = null;
        try (Connection connection = getConnection()) {
            String sql = "select " + key + " from " + table + " where " + key + "=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, key_value);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                string = resultSet.getString(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return key_value.equals(string);
    }

    public static boolean insert(String table, String key_value, String key) {
        boolean execute = false;
        try (Connection connection = getConnection()) {
            String sql = "insert into " + table + "(" + key + ") value(?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, key_value);
            execute = preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return execute;
    }

    public static boolean delete(String table, String key_value, String key) {
        boolean execute = false;
        try (Connection connection = getConnection()) {
            String sql = "delete from " + table + " where " + key + "=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, key_value);
            execute = preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return execute;
    }
}
