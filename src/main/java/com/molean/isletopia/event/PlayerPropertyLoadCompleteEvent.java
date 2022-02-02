package com.molean.isletopia.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerPropertyLoadCompleteEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public PlayerPropertyLoadCompleteEvent(@NotNull Player who) {
        super(who, true);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    private static HandlerList getHandlerList(){
        return HANDLER_LIST;
    }
}
