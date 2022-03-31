package me.armar.plugins.autorank.api.events;

import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class RequirementCompleteEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled;
    private final UUID uuid;
    private final CompositeRequirement reqHolder;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public RequirementCompleteEvent(UUID uuid, CompositeRequirement reqHolder) {
        this.uuid = uuid;
        this.reqHolder = reqHolder;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public UUID getPlayer() {
        return this.uuid;
    }

    public CompositeRequirement getRequirement() {
        return this.reqHolder;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }
}
