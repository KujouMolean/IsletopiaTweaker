package com.molean.isletopia.database;

import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.utils.UUIDUtils;
import com.plotsquared.core.plot.PlotId;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class PlotDao {


    public static Integer getPlotID(String server, String name) {
        Integer result = null;
        UUID uuid = UUIDUtils.get(name);
        try (Connection connection = DataSourceUtils.getConnection(server)) {
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
        try (Connection connection = DataSourceUtils.getConnection(server)) {
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
        try (Connection connection = DataSourceUtils.getConnection(server)) {
            String sql = "select user_uuid from plot_helpers where plot_plot_id=?";
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


    public static PlotId getPlotPosition(String server, String name) {
        Integer id = getPlotID(server, name);
        PlotId plotId = null;
        if (id != null) {
            try (Connection connection = DataSourceUtils.getConnection(server)) {
                String sql = "select plot_id_x,plot_id_z from plot where id=?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int x = resultSet.getInt(1);
                    int z = resultSet.getInt(2);
                    plotId = PlotId.of(x,z);

                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return plotId;
    }


    public static UUID getAllUUID() {
        return UUID.fromString("00000001-0001-0003-0003-000000000007");
    }
}