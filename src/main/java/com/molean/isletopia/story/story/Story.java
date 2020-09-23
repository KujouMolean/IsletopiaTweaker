package com.molean.isletopia.story.story;

import com.molean.isletopia.story.action.Action;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Story {

    private final String namespace;
    private final String name;
    private final String display;
    private final List<Action> actions = new ArrayList<>();

    public Story(String namespace, String name, String display) {
        this.namespace = namespace;
        this.name = name;
        this.display = display;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

    public List<Action> getActions() {
        return actions;
    }

    public static Story parse(ConfigurationSection section) {
        String namespace = section.getString("namespace");
        String name = section.getString("name");
        String display = section.getString("display");
        Story story = new Story(namespace, name, display);
        List<Map<?, ?>> actions = section.getMapList("actions");
        for (Map<?, ?> action : actions) {
            action.forEach((key, value) -> {
                String string = null;
                if (value instanceof Integer) {
                    string = ((Integer) value).toString();
                } else if (value instanceof String) {
                    string = (String) value;
                } else if (value instanceof Boolean) {
                    string = ((Boolean) value).toString();
                }
                assert string != null;
                story.getActions().add(Action.parse((String) key, string));
            });
        }
        return story;
    }
}
