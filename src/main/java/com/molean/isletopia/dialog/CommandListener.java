package com.molean.isletopia.dialog;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.molean.isletopia.shared.annotations.Singleton;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

@Singleton
@CommandAlias("cmd")
public class CommandListener extends BaseCommand {

    private static final Map<String, BiPredicate<String, Player>> map = new HashMap<>();

    public static void register(String key, BiPredicate<String, Player> biFunction) {
        map.put(key, biFunction);
    }


    @Default
    public void onDefault(Player player, String uuid) {
        if (map.containsKey(uuid)) {
            if (map.get(uuid).test(uuid, player)) {
                map.remove(uuid);
            }
        }
    }
}
