package com.molean.isletopia.statistics;

import com.molean.isletopia.shared.database.DataSourceUtils;
import com.molean.isletopia.shared.message.ServerInfoUpdater;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatisticsDao {
    public static void insertOnlineCount(String server, int count) {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "insert into minecraft.statistics_online_count(server,count) values(?,?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, server);
            preparedStatement.setInt(2, count);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
