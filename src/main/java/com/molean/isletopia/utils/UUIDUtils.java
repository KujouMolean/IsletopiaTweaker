package com.molean.isletopia.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class UUIDUtils {

    @Nullable
    public static String get(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer.getName() != null) {
            return offlinePlayer.getName();
        } else {
            return null;
        }
    }

    @NotNull
    public static UUID get(String player) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + player).getBytes(StandardCharsets.UTF_8));
    }


    public static void getOnlineUUID(String player,Consumer<UUID> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            try {
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + player);
                InputStream inputStream = url.openStream();
                byte[] bytes = inputStream.readAllBytes();
                JsonParser jsonParser = new JsonParser();
                JsonElement parse = jsonParser.parse(new String(bytes));
                JsonObject asJsonObject = parse.getAsJsonObject();
                JsonElement id = asJsonObject.get("id");
                String asString = id.getAsString();
                UUID uuid = UUID.fromString(asString.replaceFirst(
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                        "$1-$2-$3-$4-$5"));
                consumer.accept(uuid);
            } catch (Exception ignore) {
                consumer.accept(null);
            }
        });

    }
}
