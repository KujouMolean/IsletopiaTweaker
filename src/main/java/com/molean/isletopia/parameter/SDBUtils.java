package com.molean.isletopia.parameter;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class SDBUtils {

    public static UUID getUUID(String player) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + player.toLowerCase()).getBytes(StandardCharsets.UTF_8));
    }
}