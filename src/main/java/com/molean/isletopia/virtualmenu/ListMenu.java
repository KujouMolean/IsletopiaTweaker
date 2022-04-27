package com.molean.isletopia.virtualmenu;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
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
    private int initialPage = 0;


    public ListMenu(Player player, Component title, int initialPage) {
        super(player, 6, title);
        this.itemStackFunction = t -> new ItemStackSheet(Material.PAPER, t.toString()).build();
        this.components = new ArrayList<>();
        this.onCloseSync = this::close;
        this.onCloseAsync = () -> {
        };
        this.onClickSync = t -> {
        };
        this.onClickAsync = t -> {
        };
        this.prevPageItemStack = new ItemStackSheet(Material.SOUL_SAND, MessageUtils.getMessage(player, "menu.list.prev")).build();
        this.nextPageItemStack = new ItemStackSheet(Material.MAGMA_BLOCK, MessageUtils.getMessage(player, "menu.list.next")).build();
        this.closeItemStack = new ItemStackSheet(Material.BARRIER, MessageUtils.getMessage(player, "menu.list.close")).build();
        this.initialPage = initialPage;
    }

    public ListMenu(Player player, Component title) {
        this(player, title, 0);
    }

    public ListMenu<T> components(List<T> components) {
        this.components.clear();
        this.components.addAll(components);
        Tasks.INSTANCE.async(()-> pageUpdate(page));
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
    public void afterOpen() {
        super.afterOpen();
        Tasks.INSTANCE.async(() -> pageUpdate(initialPage));
    }

    @Override
    public void onLeftClick(int slot) {
        super.onLeftClick(slot);
        switch (slot) {
            case 47 -> {
                Tasks.INSTANCE.async(() -> pageUpdate(page - 1));
            }
            case 49 -> {
                if (onCloseSync != null) {
                    onCloseSync.run();
                }
                if (onCloseAsync != null) {
                    Tasks.INSTANCE.async(onCloseAsync);
                }

            }
            case 51 -> {
                Tasks.INSTANCE.async(() -> pageUpdate(page + 1));
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
                T t = components.get(page * 45 + slot);
                try {
                    this.onClickSync.accept(t);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Tasks.INSTANCE.async(() -> onClickAsync.accept(t));

            }
        }
    }

}
