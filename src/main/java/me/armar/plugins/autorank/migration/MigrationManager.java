package me.armar.plugins.autorank.migration;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.migration.implementations.StatzMigration;
import me.armar.plugins.autorank.migration.implementations.VanillaMigration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MigrationManager {
    private final Autorank plugin;
    private final Map<Migrationable, MigrationablePlugin> migrationablePlugins;

    public MigrationManager(Autorank instance) {
        this.plugin = instance;
        migrationablePlugins = new HashMap();
        this.migrationablePlugins.put(MigrationManager.Migrationable.PLAYTIME, new VanillaMigration(instance));
        this.migrationablePlugins.put(MigrationManager.Migrationable.VANILLA, new VanillaMigration(instance));
        this.migrationablePlugins.put(MigrationManager.Migrationable.STATZ, new StatzMigration(instance));
    }

    public Optional<MigrationablePlugin> getMigrationablePlugin(MigrationManager.Migrationable type) {
        return !this.migrationablePlugins.containsKey(type) ? Optional.empty() : Optional.ofNullable(this.migrationablePlugins.get(type));
    }

    public enum Migrationable {
        PLAYTIME,
        STATZ,
        VANILLA;

        Migrationable() {
        }
    }
}
