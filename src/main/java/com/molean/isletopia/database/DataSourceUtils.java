package com.molean.isletopia.database;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DataSourceUtils {
    private static final Map<String, MysqlConnectionPoolDataSource> dataSourceMap = new HashMap<>();


    public static Connection getConnection() {
        return getConnection("minecraft");
    }

    public static Connection getConnection(String server) {
        if (!dataSourceMap.containsKey(server)) {
            MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
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
            dataSource.setUser(username);
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
