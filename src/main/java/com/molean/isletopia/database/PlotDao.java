package com.molean.isletopia.database;

import com.molean.isletopia.tweakers.IsletopiaTweakers;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PlotDao {
    private static final Map<String, SQLiteConnectionPoolDataSource> dataSourceMap = new HashMap<>();

    private static Connection getConnection(String server) {
        if (!dataSourceMap.containsKey(server)) {
            SQLiteConnectionPoolDataSource dataSource = new SQLiteConnectionPoolDataSource();
            Path pluginPath = Paths.get(IsletopiaTweakers.getPlugin().getDataFolder().getAbsolutePath());
            String root = pluginPath.getParent().getParent().getParent().toString();
            String url = "jdbc:sqlite:" + root + "/" + server + "/plugins/PlotSquared/storage.db";
            dataSource.setUrl(url);
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

    public static Integer getPlotID(String server, String name) {
        Integer result = null;
        UUID uuid = IsletopiaTweakers.getUUID(name);
        try (Connection connection = getConnection(server)) {
            String sql = "select id from plot where owner=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public static List<UUID> getDenied(String server, String name) {
        Integer plotID = getPlotID(server, name);
        List<UUID> denied = new ArrayList<>();
        if (plotID == null)
            return denied;
        try (Connection connection = getConnection(server)) {
            String sql = "select user_uuid from plot_denied where plot_plot_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, plotID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                denied.add(UUID.fromString(resultSet.getString(1)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return denied;
    }

    public static List<UUID> getTrusted(String server, String name) {
        Integer plotID = getPlotID(server, name);
        List<UUID> trusted = new ArrayList<>();
        if (plotID == null)
            return trusted;
        try (Connection connection = getConnection(server)) {
            String sql = "select user_uuid from plot_trusted where plot_plot_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, plotID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                trusted.add(UUID.fromString(resultSet.getString(1)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return trusted;
    }

    public static boolean addTrusted(String server, String source, String target) {
        Integer plotID = getPlotID(server, source);
        UUID targetUUID = IsletopiaTweakers.getUUID(target);
        if (plotID == null)
            return false;
        if (getTrusted(server, source).contains(targetUUID)) {
            return false;
        }

        boolean execute = false;
        try (Connection connection = getConnection(server)) {
            String sql = "insert into plot_trusted(plot_plot_id,user_uuid) values(?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, plotID);
            preparedStatement.setString(2, targetUUID.toString());
            execute = preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return execute;
    }

    public static boolean removeTrusted(String server, String source, String target) {
        Integer plotID = getPlotID(server, source);
        UUID targetUUID = IsletopiaTweakers.getUUID(target);
        if (plotID == null)
            return false;
        boolean execute = false;
        try (Connection connection = getConnection(server)) {
            String sql = "delete from plot_trusted where plot_plot_id=? and user_uuid=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, plotID);
            preparedStatement.setString(2, targetUUID.toString());
            execute = preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return execute;
    }

    public static boolean addDenied(String server, String source, String target) {
        Integer plotID = getPlotID(server, source);
        UUID targetUUID = IsletopiaTweakers.getUUID(target);
        if (target.equalsIgnoreCase("*")) {
            targetUUID = getAllUUID();
        }
        if (plotID == null)
            return false;
        if (getDenied(server, source).contains(targetUUID)) {
            return false;
        }

        boolean execute = false;
        try (Connection connection = getConnection(server)) {
            String sql = "insert into plot_denied(plot_plot_id,user_uuid) values(?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, plotID);
            preparedStatement.setString(2, targetUUID.toString());
            execute = preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return execute;
    }

    public static boolean removeDenied(String server, String source, String target) {
        Integer plotID = getPlotID(server, source);
        UUID targetUUID = IsletopiaTweakers.getUUID(target);
        if (target.equalsIgnoreCase("*")) {
            targetUUID = getAllUUID();
        }
        if (plotID == null)
            return false;
        boolean execute = false;
        try (Connection connection = getConnection(server)) {
            String sql = "delete from plot_denied where plot_plot_id=? and user_uuid=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, plotID);
            preparedStatement.setString(2, targetUUID.toString());
            execute = preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return execute;
    }

    public static UUID getAllUUID() {
        return UUID.fromString("00000001-0001-0003-0003-000000000007");
    }
}
