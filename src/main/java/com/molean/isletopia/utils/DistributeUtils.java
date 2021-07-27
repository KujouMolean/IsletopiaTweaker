package com.molean.isletopia.utils;

import com.molean.isletopia.message.handler.ServerInfoUpdater;

public class DistributeUtils {
    public static String getPlayerServerName(String player){
        return ServerInfoUpdater.getPlayerServerMap().get(player);
    }
}
