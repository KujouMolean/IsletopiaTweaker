package com.molean.isletopia.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerDataSyncCompleteEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public PlayerDataSyncCompleteEvent(@NotNull Player who) {
        super(who);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    private static HandlerList getHandlerList(){
        return HANDLER_LIST;
    }

}
