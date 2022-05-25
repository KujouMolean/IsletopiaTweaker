package com.molean.isletopia.dialog;

import org.bukkit.entity.Player;

import java.util.function.Consumer;

public interface IConfirmDialog {

    void onConfirm(Consumer<Player> consumer);

    Consumer<Player> onConfirm();
}
