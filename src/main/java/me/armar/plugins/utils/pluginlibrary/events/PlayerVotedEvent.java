package me.armar.plugins.utils.pluginlibrary.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerVotedEvent extends Event {
    private Player player;
    private static final HandlerList HANDLERS = new HandlerList();

    public PlayerVotedEvent(Player player) {
        this.player = player;
    }

    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}