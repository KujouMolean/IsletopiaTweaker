package com.molean.isletopia.database;

import com.molean.isletopia.blueprint.obj.BluePrintData;
import com.molean.isletopia.shared.database.DataSourceUtils;
import com.molean.isletopia.shared.utils.ObjectUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BluePrintDao {
    public static void checkTable() throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = """
                    create table  if not exists blueprint(
                        id int primary key auto_increment,
                        name text,
                        owner varchar(100),
                        template longblob,
                        time timestamp);""";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        }
    }

    public static int countBluePrint(UUID owner) throws SQLException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "select count(*) from minecraft.blueprint where owner=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, owner.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return -1;
    }

    public static Map<String, BluePrintData> getBluePrints(UUID owner) throws SQLException, IOException {
        Map<String, BluePrintData> bluePrintTemplateMap = new HashMap<>();
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "select blueprint.name,blueprint.template from minecraft.blueprint where owner=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, owner.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString(1);
                byte[] data = resultSet.getBlob(2).getBinaryStream().readAllBytes();
                BluePrintData bluePrintData = (BluePrintData) ObjectUtils.deserialize(data);
                if (bluePrintData == null) {
                    continue;
                }
                bluePrintTemplateMap.put(name, bluePrintData);
            }
        }
        return bluePrintTemplateMap;
    }

    public static void uploadBluePrintTemplate(UUID owner, String name, BluePrintData bluePrintData) throws SQLException, IOException {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "insert into minecraft.blueprint(name, owner, template, time) VALUES (?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, owner.toString());
            byte[] serialize = ObjectUtils.serialize(bluePrintData);
            assert serialize != null;
            preparedStatement.setBlob(3, new ByteArrayInputStream(serialize));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.execute();
        }
    }
}
