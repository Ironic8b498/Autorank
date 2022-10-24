package me.armar.plugins.autorank.storage;

import me.armar.plugins.autorank.Autorank;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class PlayTimeStorageProvider {
    public Autorank plugin;

    public abstract PlayTimeStorageProvider.StorageType getStorageType();

    public PlayTimeStorageProvider(Autorank instance) {
        this.plugin = instance;
    }

    public abstract void setPlayerTime(TimeType var1, UUID var2, int var3);

    public abstract CompletableFuture<Integer> getPlayerTime(TimeType var1, UUID var2);

    public abstract void resetData(TimeType var1);

    public abstract void addPlayerTime(TimeType var1, UUID var2, int var3);

    public abstract String getName();

    public abstract CompletableFuture<Boolean> initialiseProvider();

    public abstract int purgeOldEntries(int var1);

    public int purgeOldEntries() {
        return this.purgeOldEntries(60);
    }

    public abstract CompletableFuture<Integer> getNumberOfStoredPlayers(TimeType var1);

    public abstract List<UUID> getStoredPlayers(TimeType var1);

    public abstract void saveData();

    public abstract boolean canImportData();

    public abstract void importData();

    public abstract boolean canBackupData();

    public abstract boolean backupData();

    public abstract int clearBackupsBeforeDate(LocalDate var1);

    public abstract boolean isLoaded();

    public enum StorageType {
        FLAT_FILE,
        DATABASE,
        HYBRID,
        OTHER;

        StorageType() {
        }
    }
}
