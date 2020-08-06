package com.molean.isletopia.network;

import org.bukkit.Bukkit;

public class UniversalParameter {
    public static String getParameter(String player, String key) {
        Request request = new Request("dispatcher", "getParameter");
        request.set("player", player);
        request.set("key", key);
        Response response = Client.send(request);
        if (response == null) {
            Bukkit.getLogger().severe("Failed get parameter from dispatcher server.");
        }
        return response.get("return");
    }

    public static void setParameter(String player, String key, String value) {
        Request request = new Request("dispatcher", "setParameter");
        request.set("player", player);
        request.set("key", key);
        request.set("value", value);
        Response response = Client.send(request);
        if (response == null) {
            Bukkit.getLogger().severe("Failed set parameter to dispatcher server.");
        }
    }

    public static void addParameter(String player, String key, String value) {
        Request request = new Request("dispatcher", "addParameter");
        request.set("player", player);
        request.set("key", key);
        request.set("value", value);
        Response response = Client.send(request);
        if (response == null) {
            Bukkit.getLogger().severe("Failed add parameter to dispatcher server.");
        }
    }

    public static void removeParameter(String player, String key, String value) {
        Request request = new Request("dispatcher", "removeParameter");
        request.set("player", player);
        request.set("key", key);
        request.set("value", value);
        Response response = Client.send(request);
        if (response == null) {
            Bukkit.getLogger().severe("Failed remove parameter from dispatcher server.");
        }
    }

    public static void unsetParameter(String player, String key) {
        Request request = new Request("dispatcher", "unsetParameter");
        request.set("player", player);
        request.set("key", key);
        Response response = Client.send(request);
        if (response == null) {
            Bukkit.getLogger().severe("Failed unset parameter to dispatcher server.");
        }
    }

}
