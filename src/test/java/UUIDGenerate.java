import com.molean.isletopia.shared.database.DataSourceUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UUIDGenerate {

    public static Map<String,UUID> uuidMap(){
        HashMap<String, UUID> stringUUIDHashMap = new HashMap<>();
        try (Connection connection = DataSourceUtils.getConnection()){
            String sql = """
                        select name,uuid from minecraft.uuid where uuid is not null;
                        """;

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString(1);
                UUID uuid = UUID.fromString(resultSet.getString(2));
                stringUUIDHashMap.put(name, uuid);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return stringUUIDHashMap;
    }


    public static void main(String[] args) {
        Map<String, UUID> stringUUIDMap = uuidMap();
        String beacon = "Yuba,SouthStu,sssshzr,echoff_3,leeseventeen,Loyisa_qwq,xswl,Sirin_,YanChen_AL,Kafosp,Molean,Flandre_Scarlet,ju_zi,echofff,lan_gou,Steve_5566,Biandye,Lifg,keade02,__FlandreScarlet,danbai1213,ParrotHU,Keade02,charashaobao,buliding,Kox_sun,Atan,SAVE,SAVE_AH,dojo233,Aviator20030402,JefferisPan,Nongfu_Spring,infinite,153077,Jiangwer,FreakOutHysker,Bingzai,cnmrqll,Mr_Liu,loveleo777,forfourmeals,xia_xue,T001s,bai_lu_yu_ge,kbaijin_,Ye_Huang,Ace_lufi,lightliushui,Godwei,GodWei,Mihawk,Mmoyu,chaoshen,XJ_brother,tonghua2333,Plumed2003,xtxyny,GreenApple2596,QTVQ,nov,M1ngQAQ,lixiaoshuai008,Xue_yo,lao8,Melancholy_Taier,Vods,ju_zi_ju_zi,Star_falls,TsumikiKoi,O5_CENSORED,Bee_thoven";
                String[] split = beacon.split(",");
        for (int i = 0; i < split.length; i++) {
            if (!stringUUIDMap.containsKey(split[i])) {
                split[i] = "#" + split[i];
            }
        }
        System.out.println(String.join(",", split));
    }

}
