package com.molean.isletopia.other;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class NoticeDialog extends PlayerDialog {
    private Consumer<Player> acceptConsumer;

    public NoticeDialog(String ...text) {
        for (String s : text) {
            componentList.add(Component.text(s));
        }
    }

    public NoticeDialog accept(Consumer<Player> consumer) {
        this.acceptConsumer = consumer;
        return this;
    }

    @Override
    public void open(Player player) {

        UUID uuid1 = UUID.randomUUID();
        CommandListener.register(uuid1.toString(), (key, thePlayer) -> {
            if (acceptConsumer != null) {
                acceptConsumer.accept(thePlayer);
            }
            return true;
        });
        Component acceptComponent = componentList.get(componentList.size()-1);
        acceptComponent = acceptComponent.append( Component.text("【知道了，我会参加的】")
                .color(TextColor.color(108, 156, 82))
                .clickEvent(ClickEvent.runCommand("/cmd " + uuid1)));

        componentList.set(componentList.size() - 1, acceptComponent);
        super.open(player, "title", "author");
    }
}
