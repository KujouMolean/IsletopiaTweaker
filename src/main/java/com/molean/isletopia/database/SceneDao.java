package com.molean.isletopia.database;

import com.molean.isletopia.story.scene.PlayerScene;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SceneDao {
    private static final MysqlConnectionPoolDataSource dataSource;

    static {
        dataSource = new MysqlConnectionPoolDataSource();
        String url = "jdbc:mysql://localhost/minecraft?useSSL=false&characterEncoding=utf8&serverTimezone=UTC";
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
    public static PlayerScene getScene(String player, String namespace, String name) {
        try (Connection connection = getConnection()) {
            String sql = "select id,player,namespace,name,x,y,z from scenes where player=? and namespace=? and name=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, player);
            preparedStatement.setString(2, namespace);
            preparedStatement.setString(3, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                PlayerScene playerScene = new PlayerScene();
                playerScene.setId(resultSet.getInt("id"));
                playerScene.setPlayer(resultSet.getString("player"));
                playerScene.setNamespace(resultSet.getString("namespace"));
                playerScene.setName(resultSet.getString("name"));
                playerScene.setX(resultSet.getInt("x"));
                playerScene.setY(resultSet.getInt("y"));
                playerScene.setZ(resultSet.getInt("z"));
                return playerScene;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private static void insertScene(PlayerScene playerScene) {
        try (Connection connection = getConnection()) {
            String sql = "insert into isletopia_scenes(player,namespace,name,x,y,z) values(?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, playerScene.getPlayer());
            preparedStatement.setString(2, playerScene.getNamespace());
            preparedStatement.setString(3, playerScene.getName());
            preparedStatement.setInt(4, playerScene.getX());
            preparedStatement.setInt(5, playerScene.getY());
            preparedStatement.setInt(6, playerScene.getZ());
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void setScene(PlayerScene playerScene) {
        if (getScene(playerScene.getPlayer(), playerScene.getNamespace(), playerScene.getName()) == null) {
            insertScene(playerScene);
        } else {
            try (Connection connection = getConnection()) {
                String sql = "update isletopia_scenes set x=?,y=?,z=? where player=? and namespace=? and name=?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, playerScene.getX());
                preparedStatement.setInt(2, playerScene.getY());
                preparedStatement.setInt(3, playerScene.getZ());
                preparedStatement.setString(4, playerScene.getPlayer());
                preparedStatement.setString(5, playerScene.getNamespace());
                preparedStatement.setString(6, playerScene.getName());
                preparedStatement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
