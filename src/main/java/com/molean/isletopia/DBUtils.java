package com.molean.isletopia;

import java.sql.*;

public class DBUtils {
    public static Connection getConnection() {
        Connection connection = null;
        try {
            String url = "jdbc:mysql://localhost/minecraft?useSSL=false";
            String username = "molean";
            String password = "123asd";
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }

    public static String get(String playerName, String column) {
        String result = null;
        if(!exist(playerName)){
            return null;
        }
        try {
            Connection connection = getConnection();
            String sql = "select " + column + " from parameter where player_name='" + playerName + "'";
            Statement statement = connection.createStatement();
            statement.executeQuery(sql);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                result = resultSet.getString(1);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public static boolean set(String playerName, String column, String value) {
        if (!exist(playerName)) insert(playerName);

        boolean execute = false;
        try {
            Connection connection = getConnection();
            String sql = "update parameter set " + column + "='" + value + "' where player_name='" + playerName + "'";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            execute = preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return execute;
    }

    public static boolean exist(String playerName) {
        String string = null;
        try {
            Connection connection = getConnection();
            String sql = "select player_name from parameter where player_name='" + playerName + "'";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery(sql);
            if (resultSet.next()) {
                string = resultSet.getString(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return playerName.equals(string);
    }

    public static boolean insert(String playerName) {
        boolean execute = false;
        try {
            Connection connection = getConnection();
            String sql = "insert into parameter(player_name) value('" + playerName + "') ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            execute = preparedStatement.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return execute;
    }
}
