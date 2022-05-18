package me.armar.plugins.autorank.api.events;

import java.util.UUID;
import me.armar.plugins.autorank.pathbuilder.Path;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PathCompletedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Path internalName;

    private final UUID uuid;

    public PathCompletedEvent(UUID uuid, Path path) {
        super(true);
        this.uuid = uuid;
        this.internalName = path;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Path getPath() {
        return this.internalName;
    }

    public UUID getUuid() {
        return this.uuid;
    }
}
