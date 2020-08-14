package com.molean.isletopia.parameter;

public class PDBUtils {

    public static String get(String playerName, String column) {
        return CommonDao.get("parameter", playerName, column, "player_name");
    }

    public static boolean set(String playerName, String column, String value) {
        return CommonDao.set("parameter", playerName, column, value, "player_name");
    }

    public static boolean exist(String playerName) {
        return CommonDao.exist("parameter", playerName, "player_name");
    }

    public static boolean insert(String playerName) {
        return CommonDao.insert("parameter", playerName, "player_name");
    }
}
