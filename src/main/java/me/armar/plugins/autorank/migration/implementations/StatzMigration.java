package me.armar.plugins.autorank.migration.implementations;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.migration.MigrationablePlugin;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.StatzHook;
import me.staartvin.statz.datamanager.player.PlayerStat;

import java.util.Iterator;
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
            if (statzHook == null) {
                return playersImported;
            } else {
                Iterator var4 = uuids.iterator();

                while(var4.hasNext()) {
                    UUID uuid = (UUID)var4.next();
                    double minutesPlayed = statzHook.getSpecificStatistics(PlayerStat.TIME_PLAYED, uuid);
                    if (!(minutesPlayed <= 0.0D)) {
                        this.getPlugin().getPlayTimeStorageManager().addPlayerTime(TimeType.TOTAL_TIME, uuid, (int)Math.round(minutesPlayed));
                        ++playersImported;
                    }
                }

                return playersImported;
            }
        }) : CompletableFuture.completedFuture(0);
    }
}
