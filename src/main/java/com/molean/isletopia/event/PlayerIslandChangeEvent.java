package com.molean.isletopia.event;

import com.molean.isletopia.island.Island;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class PlayerIslandChangeEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private static HandlerList getHandlerList(){
        return HANDLER_LIST;
    }

    @Nullable
    private final Island from;
    @Nullable
    private final Island to;
    private boolean canceled = false;

    public PlayerIslandChangeEvent(@NotNull Player player, @Nullable Island from, @Nullable Island to) {
        super(player);
        this.from = from;
        this.to = to;

    }

    @Nullable
    public Island getFrom() {
        return from;
    }

    @Nullable
    public Island getTo() {
        return to;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.canceled = b;

    }
}
