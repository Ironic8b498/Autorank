package me.armar.plugins.autorank.migration;

import me.armar.plugins.autorank.Autorank;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class MigrationablePlugin {
    private final Autorank plugin;

    public MigrationablePlugin(Autorank instance) {
        this.plugin = instance;
    }

    public Autorank getPlugin() {
        return this.plugin;
    }

    public abstract boolean isReady();

    public abstract CompletableFuture<Integer> migratePlayTime(List<UUID> var1);
}
