package com.molean.isletopia.database;

import com.alibaba.druid.pool.DruidDataSource;
import com.molean.isletopia.distribute.individual.ServerInfoUpdater;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DataSourceUtils {
    private static final Map<String, DruidDataSource> dataSourceMap = new HashMap<>();

    public static void checkDatabase() {
       try(Connection connection = DataSourceUtils.getConnection("minecraft")) {
           String sql = "create database if not exists " + ServerInfoUpdater.getServerName() + " default charset utf8;";
           PreparedStatement preparedStatement = connection.prepareStatement(sql);
           preparedStatement.execute();
       } catch (SQLException throwables) {
           throwables.printStackTrace();
       }
    }

    public static Connection getConnection() {
        return getConnection("minecraft");
    }

    public static Connection getConnection(String server) {
        if (!dataSourceMap.containsKey(server)) {
            DruidDataSource dataSource = new DruidDataSource();
            Properties properties = null;
            try {
                InputStream inputStream = DataSourceUtils.class.getClassLoader().getResourceAsStream("mysql.properties");
                properties = new Properties();
                properties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String url = properties.getProperty("url").replace("%server%", server);
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");
            dataSource.setUrl(url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSourceMap.put(server, dataSource);
        }
        Connection connection = null;
        try {
            connection = dataSourceMap.get(server).getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }
}
