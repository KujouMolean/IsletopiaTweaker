import com.molean.isletopia.database.DataSourceUtils;
import com.molean.isletopia.database.PlotDao;
import com.molean.isletopia.distribute.individual.ServerInfoUpdater;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MyTest {
    public static void main(String[] args) {
        String names = "lanstudy";
        String[] split = names.split("\n");
        for (String name : split) {
            List<String> servers = new ArrayList<>();
            for (int i = 1; i <= 8; i++) {
                servers.add("server" + i);
            }
            for (String server : servers) {
                Integer id = PlotDao.getPlotID(server, name);
                if (id != null) {
                    try {
                        Connection connection = DataSourceUtils.getConnection(server);
                        String sql = "select plot_id_x,plot_id_z from plot where owner=?";
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);
                        preparedStatement.setString(1, ServerInfoUpdater.getUUID(name).toString());
                        ResultSet resultSet = preparedStatement.executeQuery();
                        if (resultSet.next()) {
                            int x = resultSet.getInt(1) - 1;
                            int z = resultSet.getInt(2) - 1;
                            String cmd = "copy C:\\Users\\Molean\\Desktop\\MCSManager_8.6.15_Win64\\server\\server_core_bak\\"+server+"\\SkyWorld\\region\\r."+x+"."+z+".mca \"C:\\Users\\Molean\\Desktop\\新建文件夹 (3)\\"+server+"\"";
                            System.out.println(server + " " + (x+1) + " " + (z+1));
                            System.out.println(cmd);
                        }


                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }

            }
        }


    }

}
