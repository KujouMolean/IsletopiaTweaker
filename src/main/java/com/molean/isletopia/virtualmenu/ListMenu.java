package com.molean.isletopia.virtualmenu;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.ItemStackSheet;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ListMenu<T> extends ChestMenu {

    private final List<T> components;
    private Function<T, ItemStack> itemStackFunction;
    private ItemStack nextPageItemStack;
    private ItemStack prevPageItemStack;
    private ItemStack closeItemStack;
    private Runnable onCloseSync;
    private Runnable onCloseAsync;
    private int page = 0;
    private Consumer<T> onClickSync;
    private Consumer<T> onClickAsync;

    @Override
    public void destroy() {
        super.destroy();
        components.clear();
        itemStackFunction = null;
        nextPageItemStack = null;
        prevPageItemStack = null;
        closeItemStack =null;
        onCloseSync = null;
        onCloseAsync = null;
        onClickSync = null;
        onClickAsync = null;
    }

    public ListMenu(Player player, Component title) {
        super(player, 6, title);
        this.itemStackFunction = t -> new ItemStackSheet(Material.PAPER, t.toString()).build();
        this.components = new ArrayList<>();
        this.onCloseSync = this::close;
        this.onCloseAsync =()->{};
        this.onClickSync = t -> {};
        this.onClickAsync = t -> {};
        this.prevPageItemStack = new ItemStackSheet(Material.SOUL_SAND, "§f上一页").build();
        this.nextPageItemStack = new ItemStackSheet(Material.MAGMA_BLOCK, "§f下一页").build();
        this.closeItemStack = new ItemStackSheet(Material.BARRIER, "§f关闭").build();
    }

    public ListMenu<T> components(List<T> components) {
        this.components.clear();
        this.components.addAll(components);
        pageUpdate(page);
        return this;
    }

    public List<T> components() {
        return this.components;
    }


    public ListMenu<T> onClickSync(Consumer<T> consumer) {
        this.onClickSync = consumer;
        return this;
    }
    public ListMenu<T> onClickAsync(Consumer<T> consumer) {
        this.onClickAsync = consumer;
        return this;
    }

    public ListMenu<T> convertFunction(Function<T, ItemStack> itemStackFunction) {
        this.itemStackFunction = itemStackFunction;
        return this;
    }

    public ListMenu<T> nextPageItemStack(ItemStack nextPageItemStack) {
        this.nextPageItemStack = nextPageItemStack;
        return this;
    }

    public ListMenu<T> prevPageItemStack(ItemStack prevPageItemStack) {
        this.prevPageItemStack = prevPageItemStack;
        return this;
    }

    public ListMenu<T> closeItemStack(ItemStack closeItemStack) {
        this.closeItemStack = closeItemStack;
        return this;
    }

    public ListMenu<T> onCloseSync(Runnable runnable) {
        this.onCloseSync = runnable;
        return this;
    }
    public ListMenu<T> onCloseAsync(Runnable runnable) {
        this.onCloseAsync = runnable;
        return this;
    }

    public void pageUpdate(int target) {
        int maxPage = components.size() / 45;
        if (components.size() % 45 != 0) {
            maxPage = maxPage + 1;
        }
        if (target >= maxPage) {
            target = maxPage - 1;
        }
        if (target < 0) {
            target = 0;
        }
        page = target;
        for (int i = 0; i < 54; i++) {
            item(i, null);
        }
        item(47, prevPageItemStack);
        item(49, closeItemStack);
        item(51, nextPageItemStack);
        for (int i = 0; page * 45 + i < components.size() && i < 45; i++) {
            item(i, itemStackFunction.apply(components.get(page * 45 + i)));
        }
    }

    @Override
    public void beforeOpen() {
        super.beforeOpen();
        pageUpdate(0);
    }

    @Override
    public void onLeftClick(int slot) {
        super.onLeftClick(slot);
        switch (slot) {
            case 47 -> {
                pageUpdate(page - 1);
            }
            case 49 -> {
                onCloseSync.run();
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), onCloseAsync);

            }
            case 51 -> {
                pageUpdate(page + 1);
            }
            default -> {
                if (slot < 0) {
                    return;
                }
                if (slot >= 45) {
                    return;
                }
                if (page * 45 + slot >= components.size()) {
                    return;
                }
                this.onClickSync.accept(components.get(page * 45 + slot));
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                    onClickAsync.accept(components.get(page * 45 + slot));
                });

            }
        }
    }

}
