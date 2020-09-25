package com.molean.isletopia._menu;

import com.molean.isletopia.utils.Pos;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class Component {
    private final Map<ClickType, BiConsumer<Component, Pos>> events = new HashMap<>();

    private final Player player;

    private final int length;
    private final int width, height;
    private int interval;
    private Integer left, top;
    private ItemStack[][][] itemStacks;

    public Component(@NotNull Player player, int length, int width, int height, int interval) {
        this.player = player;
        this.length = length;
        this.width = width;
        this.height = height;
        this.interval = interval;
        left = null;
        top = null;
    }

    public void setLeft(@Nullable Integer left) {
        this.left = left;
    }

    public void setTop(@Nullable Integer top) {
        this.top = top;
    }

    @Nullable
    public Integer getLeft() {
        return left;
    }

    @Nullable
    public Integer getTop() {
        return top;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setItemStack(@Nullable ItemStack itemStack) {
        itemStacks[0][0][0] = itemStack;
    }

    public void setItemStacks(int x, int y, @Nullable ItemStack itemStack) {
        itemStacks[0][x][y] = itemStack;
    }

    public void setItemStacks(int f, int x, int y, @Nullable ItemStack itemStack) {
        itemStacks[f][x][y] = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStacks[0][0][0];
    }

    public ItemStack getItemStack(int x, int y) {
        return itemStacks[0][x][y];
    }

    public ItemStack getItemStack(int f, int x, int y) {
        return itemStacks[f][x][y];
    }

    public @NotNull ItemStack[][] drawFrame(int f) {
        ItemStack[][] frame = new ItemStack[width][height];
        for (ItemStack[][] layer : itemStacks) {
            for (int j = 0; j < layer.length; j++) {
                for (int k = 0; k < layer[j].length; k++) {
                    if (layer[j][k] != null) {
                        frame[j][k] = layer[j][k].clone();
                    }
                }
            }
        }
        return frame;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public int getInterval() {
        return interval;
    }

    public Player getPlayer() {
        return player;
    }

    void triggerEvent(ClickType clickType) {
        BiConsumer<Component, Pos> componentConsumer = events.get(clickType);

        if (componentConsumer != null) {
            componentConsumer.accept(this, new Pos(0, 0));
        }
    }

    void triggerEvent(ClickType clickType, Pos pos) {
        BiConsumer<Component, Pos> componentConsumer = events.get(clickType);
        if (componentConsumer != null) {
            componentConsumer.accept(this, pos);
        }
    }

    void registerEvent(ClickType clickType, @Nullable BiConsumer<Component, Pos> biConsumer) {
        events.put(clickType, biConsumer);
    }
}
