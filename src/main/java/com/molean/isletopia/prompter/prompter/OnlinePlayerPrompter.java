package com.molean.isletopia.prompter.prompter;

import com.molean.isletopia.network.Client;
import com.molean.isletopia.network.Request;
import com.molean.isletopia.network.Response;
import com.molean.isletopia.tweakers.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class OnlinePlayerPrompter extends PlayerPrompter {

    public OnlinePlayerPrompter(Player argPlayer, String argTtile) {
        super(argPlayer, argTtile, getPlayerNames());
    }

    private static List<String> getPlayerNames() {
        List<String> names = new ArrayList<>();
        Request request = new Request("dispatcher", "getOnlinePlayers");
        Response response = Client.send(request);
        if (response != null) {
            String[] respondedNames = response.get("players").split(",");
            for (String respondedName : respondedNames) {
                if (!respondedName.trim().equalsIgnoreCase("")) {
                    names.add(respondedName);
                }
            }
        } else {
            Bukkit.getLogger().severe("Failed get player from dispatcher server.");
        }
        return names;
    }
}
