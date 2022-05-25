package com.molean.isletopia.dialog;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class BookDialog implements IPlayerDialog {
    protected List<Component> componentList = new ArrayList<>();
    protected Player player;

    public BookDialog(Player player) {
        this.player = player;
    }

    public List<Component> componentList() {
        return componentList;
    }

    public void componentList(List<Component> componentList) {
        this.componentList = componentList;
    }

    public void open() {
        open("title", "author");
    }

    public void open(String title, String author) {
        Component bookTitle = Component.text(title);
        Component bookAuthor = Component.text(author);
        player.openBook(Book.book(bookTitle, bookAuthor, componentList));
    }
}
