package me.armar.plugins.autorank.migration.implementations;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.migration.MigrationablePlugin;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.StatzHook;
import me.staartvin.statz.datamanager.player.PlayerStat;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class StatzMigration extends MigrationablePlugin {
    public StatzMigration(Autorank instance) {
        super(instance);
    }

    public boolean isReady() {
        return this.getPlugin().getDependencyManager().isAvailable(Library.STATZ);
    }

    public CompletableFuture<Integer> migratePlayTime(List<UUID> uuids) {
        return !uuids.isEmpty() && this.isReady() ? CompletableFuture.supplyAsync(() -> {
            this.getPlugin().debugMessage("Migrating player data from Statz!");
            int playersImported = 0;
            StatzHook statzHook = (StatzHook)this.getPlugin().getDependencyManager().getLibraryHook(Library.STATZ).orElse(null);
            if (statzHook != null) {
                for (UUID uuid : uuids) {
                    double minutesPlayed = statzHook.getSpecificStatistics(PlayerStat.TIME_PLAYED, uuid);
                    if (!(minutesPlayed <= (double) 0.0F)) {
                        this.getPlugin().getPlayTimeStorageManager().addPlayerTime(TimeType.TOTAL_TIME, uuid, (int) Math.round(minutesPlayed));
                        ++playersImported;
                    }
                }

            }
            return playersImported;
        }) : CompletableFuture.completedFuture(0);
    }
}
