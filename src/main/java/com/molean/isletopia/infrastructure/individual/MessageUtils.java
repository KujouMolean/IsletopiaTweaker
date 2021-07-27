package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.message.handler.ServerInfoUpdater;

public class MessageUtils {
    public static String getLocalServerName() {
        switch (ServerInfoUpdater.getServerName()) {
            case "server1":
                return "衔心岠";
            case "server2":
                return "梦华沢";
            case "server3":
                return "凌沧州";
            case "server4":
                return "净琉璃世界";
            case "server5":
                return "东海蓬莱";
            case "server6":
                return "佳和苑";
            case "server7":
                return "楠故㟓";
            case "server8":
                return "胧月花栞";
        }
        return "未知";
    }
}
