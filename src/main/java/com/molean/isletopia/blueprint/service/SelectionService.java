package com.molean.isletopia.blueprint.service;

import com.molean.isletopia.blueprint.obj.BluePrintData;
import org.bukkit.entity.Player;

import javax.xml.stream.Location;

public class SelectionService {
    public static boolean check(Location loc1, Location loc2) {
        return true;
    }

    public static BluePrintData create(Player player, Location loc1, Location loc2) {
        assert check(loc1, loc2);
        return null;
    }
}
