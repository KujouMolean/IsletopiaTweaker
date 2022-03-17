package com.molean.isletopia.dialog;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class PlayerDialog {
    protected List<Component> componentList = new ArrayList<>();

    public PlayerDialog() {

    }

    public PlayerDialog(List<Component> componentList) {
        this.componentList = componentList;

    }

    public void open(Player player) {
        open(player, "title", "author");
    }

    public void open(Player player, String title, String author) {
        Component bookTitle = Component.text(title);
        Component bookAuthor = Component.text(author);
        player.openBook(Book.book(bookTitle, bookAuthor, componentList));
    }
}
