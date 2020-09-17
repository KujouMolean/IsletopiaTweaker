package com.molean.isletopia.story.action;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.sqlite.SQLiteConfig;

import java.util.Arrays;
import java.util.List;

public interface Action {
    void play(Player player);

    static Action parse(String key, String value) {
        List<String> parameter = Arrays.asList(value.split(";"));
        switch (key) {
            case "text":
            case "print":
                return new SendText(value);
            case "println":
            case "textln":
                return new SendTextln(value);
            case "wait":
            case "delay":
                return new PerformDelay(Integer.parseInt(parameter.get(0)));
            case "effect":
            case "potion":
            case "potionEffect":
                return new PlayEffect(
                        PotionEffectType.getByName(parameter.get(0)),
                        Integer.parseInt(parameter.get(1)),
                        Integer.parseInt(parameter.get(2))
                );
            case "specificTitle":
                return new SendSpecificTitle(
                        parameter.get(0),
                        parameter.get(1),
                        Integer.parseInt(parameter.get(2)),
                        Integer.parseInt(parameter.get(3)),
                        Integer.parseInt(parameter.get(4))
                );
            case "scene":
                return new Scene(parameter.get(0),parameter.get(1),Integer.parseInt(parameter.get(2)));
            case "choose":
            case "chooseStory":
                return new Choose(value);
            case "clearEffect":
                return new ClearEffect();
            case "blind":
                return new Blind(Integer.parseInt(value));
            case "title":
                return new SendTitle(value);
            case "subTitle":
            case "subtitle":
                return new SendSubTitle(value);
            case "movable":
            case "setMovable":
                return new Movable(Boolean.parseBoolean(value));
        }
        return null;
    }
}
