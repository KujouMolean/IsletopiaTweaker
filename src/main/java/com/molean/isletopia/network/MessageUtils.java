package com.molean.isletopia.network;

public class MessageUtils {

    public static Integer getPlotNumber(String server, String player) {
        Request request = new Request(server, "getParameter");
        request.set("player", player);
        Response response = Client.send(request);
        return Integer.parseInt(response.get("return"));
    }

    public static String getParameter(String player, String key) {
        Request request = new Request("dispatcher", "getParameter");
        request.set("player", player);
        request.set("key", key);
        Response response = Client.send(request);
        return response.get("return");
    }

    public static void setParameter(String player, String key, String value) {
        Request request = new Request("dispatcher", "setParameter");
        request.set("player", player);
        request.set("key", key);
        request.set("value", value);
        Response response = Client.send(request);
    }

    public static void addParameter(String player, String key, String value) {
        Request request = new Request("dispatcher", "addParameter");
        request.set("player", player);
        request.set("key", key);
        request.set("value", value);
        Response response = Client.send(request);
    }

    public static void removeParameter(String player, String key, String value) {
        Request request = new Request("dispatcher", "removeParameter");
        request.set("player", player);
        request.set("key", key);
        request.set("value", value);
        Response response = Client.send(request);
    }

    public static void unsetParameter(String player, String key) {
        Request request = new Request("dispatcher", "unsetParameter");
        request.set("player", player);
        request.set("key", key);
        Response response = Client.send(request);
    }

}
