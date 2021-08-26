
package com.molean.isletopia.database;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.utils.PlotUtils;
import com.molean.isletopia.utils.SaveUtils;
import com.molean.isletopia.utils.UUIDUtils;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.plot.Plot;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DownloadDao {

    public static void checkTable() {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    create table if not exists isletopia_save
                    (
                        id     int primary key auto_increment,
                        player text     not null,
                        data   longblob not null,
                        time   long     not null,
                        token  text     not null
                    );""";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void deleteOld(String player) {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "delete from minecraft.isletopia_save where player=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, player);
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static String uploadSave(Plot plot) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String player = PlotSquared.get().getImpromptuUUIDPipeline().getSingle(plot.getOwner(), 100L);
        deleteOld(player);
        UUID owner = plot.getOwner();
        File plotRegionFile = PlotUtils.getPlotRegionFile(plot);
        File playerStatsFile = SaveUtils.getPlayerStatsFile(owner);
        File playerDataFile = SaveUtils.getPlayerDataFile(owner);
        File levelFile = SaveUtils.getLevelFile();
        if (!plotRegionFile.exists() && !plotRegionFile.createNewFile()) {
            throw new IOException();
        }
        if (!playerStatsFile.exists() && !playerStatsFile.createNewFile()) {
            throw new IOException();
        }
        if (!playerDataFile.exists() && playerDataFile.createNewFile()) {
            throw new IOException();
        }
        if (!levelFile.exists() && !levelFile.createNewFile()) {
            throw new IOException();
        }

        FileInputStream levelInputStream = new FileInputStream(levelFile);
        FileInputStream plotRegionInputStream = new FileInputStream(plotRegionFile);
        FileInputStream playerStatsInputStream = new FileInputStream(playerStatsFile);
        FileInputStream playerDataInputStream = new FileInputStream(playerDataFile);
        byte[] levelBytes = levelInputStream.readAllBytes();
        byte[] regionBytes = plotRegionInputStream.readAllBytes();
        byte[] playerStatsBytes = playerStatsInputStream.readAllBytes();
        byte[] playerDataBytes = playerDataInputStream.readAllBytes();
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        zipOutputStream.putNextEntry(new ZipEntry("SkyWorld/" + levelFile.getName()));
        zipOutputStream.write(levelBytes);
        zipOutputStream.putNextEntry(new ZipEntry("SkyWorld/region/" + plotRegionFile.getName()));
        zipOutputStream.write(regionBytes);
        zipOutputStream.putNextEntry(new ZipEntry("SkyWorld/stats/" + playerStatsFile.getName()));
        zipOutputStream.write(playerStatsBytes);
        zipOutputStream.putNextEntry(new ZipEntry("SkyWorld/playerdata/" + playerDataFile.getName()));
        zipOutputStream.write(playerDataBytes);
        zipOutputStream.close();

        byte[] data = outputStream.toByteArray();
        String token = UUID.randomUUID().toString().substring(0, 8);
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "insert into minecraft.isletopia_save(player, data, time, token) values (?,?,?,?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, player);
            preparedStatement.setBytes(2, data);
            preparedStatement.setLong(3, System.currentTimeMillis());
            preparedStatement.setString(4, token);
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return token;
    }
}
