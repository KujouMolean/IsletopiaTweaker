package com.molean.isletopia.database;

import com.molean.isletopia.bungee.individual.ServerInfoUpdater;
import com.molean.isletopia.message.core.ServerMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

public class ServerMessageDao {

    public static void checkTable() {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "create table if not exists isletopia_message\n" +
                    "(\n" +
                    "    id int auto_increment primary key ,\n" +
                    "    source text not null ,\n" +
                    "    target text not null ,\n" +
                    "    channel text not null ,\n" +
                    "    message text not null ,\n" +
                    "    status text not null ,\n" +
                    "    time long not null\n" +
                    ");";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void addMessage(ServerMessage serverMessage) {
        removeExpire();
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "insert into minecraft.isletopia_message(source, target, channel, message, status, time) " +
                    "VALUES (?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, serverMessage.getSource());
            preparedStatement.setString(2, serverMessage.getTarget());
            preparedStatement.setString(3, serverMessage.getChannel());
            preparedStatement.setString(4, serverMessage.getMessage());
            preparedStatement.setString(5, serverMessage.getStatus());
            preparedStatement.setLong(6, serverMessage.getTime());
            preparedStatement.execute();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    public static void removeExpire() {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "delete from minecraft.isletopia_message where time < ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, System.currentTimeMillis() - 1000 * 10 * 60L);
            preparedStatement.execute();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static Set<ServerMessage> fetchMessage() {

        Set<ServerMessage> serverMessages = new HashSet<>();
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "select id, source, target, channel, message, status, time from minecraft.isletopia_message\n" +
                    "where target=? and status!='expire' and status!='done' and status!='invalid'";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, ServerInfoUpdater.getServerName());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ServerMessage serverMessage = new ServerMessage();
                serverMessage.setId(resultSet.getInt(1));
                serverMessage.setSource(resultSet.getString(2));
                serverMessage.setTarget(resultSet.getString(3));
                serverMessage.setChannel(resultSet.getString(4));
                serverMessage.setMessage(resultSet.getString(5));
                serverMessage.setStatus(resultSet.getString(6));
                serverMessage.setTime(resultSet.getLong(7));
                serverMessages.add(serverMessage);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return serverMessages;
    }

    public static void updateStatus(int messageId, String status) {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "update minecraft.isletopia_message set status=? where id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, messageId);
            preparedStatement.execute();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
