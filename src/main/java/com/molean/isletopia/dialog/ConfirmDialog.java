package com.molean.isletopia.dialog;

import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

public class ConfirmDialog extends PlayerDialog {
    private final Component component;
    private Consumer<Player> acceptConsumer;

    public ConfirmDialog(Component component) {
        this.component = component;
    }

    public ConfirmDialog(String text) {
        this(Component.text(text));
    }

    public ConfirmDialog accept(Consumer<Player> consumer) {
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

        UUID uuid2 = UUID.randomUUID();
        CommandListener.register(uuid2.toString(), (key, thePlayer) -> {
            player.closeInventory();
            return true;
        });
        Component newComponent = component.append(Component.text("\n"));
        TextComponent acceptComponent = Component.text(MessageUtils.getMessage(player, "dialog.confirm.accept"))
                .color(TextColor.color(108, 156, 82))
                .clickEvent(ClickEvent.runCommand("/cmd " + uuid1));
        newComponent = newComponent.append(acceptComponent);
        newComponent = newComponent.append(Component.text(MessageUtils.getMessage(player, "dialog.confirm.space", Pair.of("space", ""))));
        TextComponent refuseComponent = Component.text(MessageUtils.getMessage(player, "dialog.confirm.deny"))
                .color(TextColor.color(156, 79, 74))
                .clickEvent(ClickEvent.runCommand("/cmd " + uuid2));
        newComponent = newComponent.append(refuseComponent);
        componentList.clear();
        componentList.add(newComponent);
        super.open(player, "title", "author");
    }
}
