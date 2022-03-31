package me.armar.plugins.autorank.playtimes;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.storage.PlayTimeStorageProvider;
import me.armar.plugins.autorank.storage.TimeType;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PlayTimeManager {
    public static int INTERVAL_MINUTES = 5;
    private final Autorank plugin;

    public PlayTimeManager(Autorank plugin) {
        this.plugin = plugin;
        INTERVAL_MINUTES = plugin.getSettingsConfig().getIntervalTime();
        plugin.getLogger().info("Interval check every " + INTERVAL_MINUTES + " minutes.");
    }

    public CompletableFuture<Integer> getGlobalPlayTime(TimeType timeType, UUID uuid) {
        return !this.plugin.getPlayTimeStorageManager().isStorageTypeActive(PlayTimeStorageProvider.StorageType.DATABASE) ? CompletableFuture.completedFuture(-1) : this.plugin.getPlayTimeStorageManager().getStorageProvider(PlayTimeStorageProvider.StorageType.DATABASE).getPlayerTime(timeType, uuid);
    }

    public void setGlobalPlayTime(TimeType timeType, UUID uuid, int value) {
        if (this.plugin.getPlayTimeStorageManager().isStorageTypeActive(PlayTimeStorageProvider.StorageType.DATABASE)) {
            this.plugin.getPlayTimeStorageManager().getStorageProvider(PlayTimeStorageProvider.StorageType.DATABASE).setPlayerTime(timeType, uuid, value);
        }
    }

    public void addGlobalPlayTime(TimeType timeType, UUID uuid, int valueToAdd) {
        if (this.plugin.getPlayTimeStorageManager().isStorageTypeActive(PlayTimeStorageProvider.StorageType.DATABASE)) {
            this.plugin.getPlayTimeStorageManager().getStorageProvider(PlayTimeStorageProvider.StorageType.DATABASE).addPlayerTime(timeType, uuid, valueToAdd);
        }
    }

    public CompletableFuture<Integer> getLocalPlayTime(TimeType timeType, UUID uuid) {
        return !this.plugin.getPlayTimeStorageManager().isStorageTypeActive(PlayTimeStorageProvider.StorageType.FLAT_FILE) ? CompletableFuture.completedFuture(-1) : this.plugin.getPlayTimeStorageManager().getStorageProvider(PlayTimeStorageProvider.StorageType.FLAT_FILE).getPlayerTime(timeType, uuid);
    }

    public void setLocalPlayTime(TimeType timeType, UUID uuid, int value) {
        if (this.plugin.getPlayTimeStorageManager().isStorageTypeActive(PlayTimeStorageProvider.StorageType.FLAT_FILE)) {
            this.plugin.getPlayTimeStorageManager().getStorageProvider(PlayTimeStorageProvider.StorageType.FLAT_FILE).setPlayerTime(timeType, uuid, value);
        }
    }

    public void addLocalPlayTime(TimeType timeType, UUID uuid, int valueToAdd) {
        if (this.plugin.getPlayTimeStorageManager().isStorageTypeActive(PlayTimeStorageProvider.StorageType.FLAT_FILE)) {
            this.plugin.getPlayTimeStorageManager().getStorageProvider(PlayTimeStorageProvider.StorageType.FLAT_FILE).addPlayerTime(timeType, uuid, valueToAdd);
        }
    }

    /** @deprecated */
    @Deprecated
    public CompletableFuture<Integer> getPlayTime(TimeType timeType, UUID uuid) {
        return this.plugin.getPlayTimeStorageManager().getPrimaryStorageProvider().getPlayerTime(timeType, uuid);
    }

    public CompletableFuture<Long> getPlayTime(TimeType timeType, UUID uuid, TimeUnit timeUnit) {
        return CompletableFuture.supplyAsync(() -> {
            int minutes = 0;

            try {
                minutes = this.plugin.getPlayTimeStorageManager().getPrimaryStorageProvider().getPlayerTime(timeType, uuid).get();
            } catch (ExecutionException | InterruptedException var6) {
                var6.printStackTrace();
            }

            return timeUnit.convert(minutes, TimeUnit.MINUTES);
        });
    }
}
