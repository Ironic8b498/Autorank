package me.armar.plugins.autorank.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerPromoteEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled;
    private final Player player;
    private final String worldName;
    private final String groupFrom;
    private final String groupTo;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public PlayerPromoteEvent(Player player, String worldName, String groupFrom, String groupTo) {
        this.player = player;
        this.worldName = worldName;
        this.groupFrom = groupFrom;
        this.groupTo = groupTo;
    }

    public String getGroupFrom() {
        return this.groupFrom;
    }

    public String getGroupTo() {
        return this.groupTo;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getWorld() {
        return this.worldName;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }
}
