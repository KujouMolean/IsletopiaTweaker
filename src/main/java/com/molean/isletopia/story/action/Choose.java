package com.molean.isletopia.story.action;

import com.molean.isletopia.story.StoryManager;
import com.molean.isletopia.story.story.Story;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class Choose implements Action {
    private final String text;

    public Choose(String text) {
        this.text = text;
    }

    @Override
    public void play(Player player) {
        ComponentBuilder componentBuilder = new ComponentBuilder();
        char[] chars = text.toCharArray();
        int pos1 = -1, pos2 = -1;
        boolean hasPos1 = false;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '%') {
                if (hasPos1) {
                    hasPos1 = false;
                    pos2 = i;
                    String storyId = text.substring(pos1 + 1, pos2);

                    Story story = StoryManager.getStory(storyId);
                    TextComponent textComponent = new TextComponent(story.getName());
                    String command = "/story play " + storyId;
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
                    componentBuilder.append(textComponent);
                } else {
                    hasPos1 = true;
                    pos1 = i;
                    String otherText = text.substring(pos2 + 1, i);
                    TextComponent textComponent = new TextComponent(otherText);
                    componentBuilder.append(textComponent);
                }
            }
        }
        String otherText = text.substring(pos2 + 1);
        TextComponent textComponent = new TextComponent(otherText);
        componentBuilder.append(textComponent);
        player.spigot().sendMessage(componentBuilder.create());
    }
}
