package com.molean.isletopia.story.story;

import com.molean.isletopia.story.action.Action;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Story {

    private final String name;
    private final String id;
    private final List<Action> actions = new ArrayList<>();

    public Story(String id, String name) {
        this.id=id;
        this.name = name;
    }


    public Story(List<Action> actions, String id, String name) {
        this.id = id;
        this.name = name;
        this.actions.addAll(actions);
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<Action> getActions() {
        return actions;
    }

    @Override
    public String toString() {
        return "Story{" +
                "actions=" + actions +
                '}';
    }

    public static Story parse(String id, ConfigurationSection section) {
        String name = section.getString("name");
        Story story = new Story(id, name);
        List<Map<?, ?>> actions = section.getMapList("actions");
        for (Map<?, ?> action : actions) {
            action.forEach((key, value) -> {
                String string = null;
                if (value instanceof Integer) {
                    string = ((Integer) value).toString();
                } else if (value instanceof String) {
                    string = (String) value;
                }
                assert string != null;
                story.getActions().add(Action.parse((String) key, string));
            });
        }
        return story;
    }
}
