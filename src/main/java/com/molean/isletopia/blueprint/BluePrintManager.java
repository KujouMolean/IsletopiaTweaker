package com.molean.isletopia.blueprint;

import com.molean.isletopia.blueprint.obj.BluePrintTemplate;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public enum BluePrintManager implements Listener {
    INSTANCE;


    private final Map<Integer, BluePrintTemplate> bluePrintTemplateMap = new HashMap<>();

    BluePrintManager() {
    }

    public BluePrintTemplate getBluePrintTemplate(int id) {
        return null;
    }

    public void fill() {

    }

}
