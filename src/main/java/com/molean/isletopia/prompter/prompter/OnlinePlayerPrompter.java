package com.molean.isletopia.prompter.prompter;

import com.molean.isletopia.network.Client;
import com.molean.isletopia.network.Request;
import com.molean.isletopia.network.Response;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OnlinePlayerPrompter extends PlayerPrompter {

    public OnlinePlayerPrompter(Player argPlayer, String argTtile) {
        super(argPlayer, argTtile, getPlayernames());
    }

    private static List<String> getPlayernames() {
        List<String> names = new ArrayList<>();
        Request request = new Request("dispatcher", "getOnlinePlayers");
        Response response = Client.send(request);
        List<String> respondedNames = Arrays.asList(response.get("players").split(","));
        for (String respondedName : respondedNames) {
            if (!respondedName.trim().equalsIgnoreCase("")) {
                names.add(respondedName);
            }
        }
        return names;
    }
}
