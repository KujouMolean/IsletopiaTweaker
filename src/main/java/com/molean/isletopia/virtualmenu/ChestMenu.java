package com.molean.isletopia.virtualmenu;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.virtualmenu.internal.AbstractChestMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChestMenu extends AbstractChestMenu {

    private final List<Consumer<Pair<Integer, ClickType>>> clickConsumerSync = new ArrayList<>();
    private final List<Consumer<Pair<Integer, ClickType>>> clickConsumerAsync = new ArrayList<>();

    public ChestMenu(Player player, int rows, Component title) {
        super(player, rows, title);
    }

    public ChestMenu clickEventSync(Consumer<Pair<Integer, ClickType>> consumerSync) {
        clickConsumerSync.add(consumerSync);
        return this;
    }

    public ChestMenu clickEventSync(ClickType clickType, Consumer<Integer> consumerSync) {
        clickConsumerSync.add(integerClickTypePair -> {
            if (integerClickTypePair.getValue().equals(clickType)) {
                consumerSync.accept(integerClickTypePair.getKey());
            }
        });
        return this;
    }

    public ChestMenu clickEventSync(int slot, Consumer<ClickType> consumerSync) {
        clickConsumerSync.add(integerClickTypePair -> {
            if (slot == integerClickTypePair.getKey()) {
                consumerSync.accept(integerClickTypePair.getValue());
            }
        });
        return this;
    }

    public ChestMenu clickEventSync(ClickType clickType, int slot, Runnable runnableSync) {
        clickConsumerSync.add(integerClickTypePair -> {
            if (integerClickTypePair.getValue().equals(clickType) && slot == integerClickTypePair.getKey()) {
                runnableSync.run();
            }
        });
        return this;
    }

    public ChestMenu clickEventAsync(Consumer<Pair<Integer, ClickType>> consumerAsync) {
        clickConsumerAsync.add(consumerAsync);
        return this;
    }

    public ChestMenu clickEventAsync(ClickType clickType, Consumer<Integer> consumerAsync) {
        clickConsumerAsync.add(integerClickTypePair -> {
            if (integerClickTypePair.getValue().equals(clickType)) {
                consumerAsync.accept(integerClickTypePair.getKey());
            }
        });
        return this;
    }

    public ChestMenu clickEventAsync(int slot, Consumer<ClickType> consumerAsync) {
        clickConsumerAsync.add(integerClickTypePair -> {
            if (slot == integerClickTypePair.getKey()) {
                consumerAsync.accept(integerClickTypePair.getValue());
            }
        });
        return this;
    }

    public ChestMenu clickEventAsync(ClickType clickType, int slot, Runnable runnableAsync) {
        clickConsumerAsync.add(integerClickTypePair -> {
            if (integerClickTypePair.getValue().equals(clickType) && slot == integerClickTypePair.getKey()) {
                runnableAsync.run();
            }
        });
        return this;
    }


    public ChestMenu item(int slot, ItemStack itemStack, Runnable leftClickRunnableSync) {
        item(slot, itemStack);
        clickEventSync(ClickType.LEFT, slot, leftClickRunnableSync);
        return this;
    }

    public ChestMenu itemWithAsyncClickEvent(int slot, ItemStack itemStack, Runnable leftClickRunnableAsync) {
        item(slot, itemStack);
        clickEventAsync(ClickType.LEFT, slot, leftClickRunnableAsync);
        return this;
    }


    @Override
    public void afterClick(ClickType clickType, int slot) {
        super.afterClick(clickType, slot);
        for (Consumer<Pair<Integer, ClickType>> pairConsumer : clickConsumerSync) {
            pairConsumer.accept(new Pair<>(slot, clickType));
        }
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            for (Consumer<Pair<Integer, ClickType>> pairConsumer : clickConsumerAsync) {
                pairConsumer.accept(new Pair<>(slot, clickType));
            }
        });
    }

    @Override
    public ChestMenu item(int slot, ItemStack itemStack) {
        return (ChestMenu) super.item(slot, itemStack);
    }

    @Override
    public void destroy() {
        super.destroy();
        clickConsumerAsync.clear();;
        clickConsumerSync.clear();
    }
}
