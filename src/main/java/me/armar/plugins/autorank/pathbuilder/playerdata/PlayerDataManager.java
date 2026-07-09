package me.armar.plugins.autorank.pathbuilder.playerdata;

import io.reactivex.annotations.NonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.armar.plugins.autorank.Autorank;

public class PlayerDataManager {
    private final Autorank plugin;
    private final List<PlayerDataStorage> activeDataStorage = new ArrayList<>();

    public PlayerDataManager(Autorank instance) {
        this.plugin = instance;
    }

    public void addDataStorage(@NonNull PlayerDataStorage storage) {
        if (this.activeDataStorage.stream().noneMatch((stored) -> stored.getDataStorageType() == storage.getDataStorageType())) {
            this.plugin.debugMessage("Registered player data storage (" + storage.getDataStorageType() + ")");
            this.activeDataStorage.add(storage);
        }

    }

    public List<PlayerDataStorage> getActiveDataStorages() {
        return this.activeDataStorage;
    }

    public Optional<PlayerDataStorage> getDataStorage(@NonNull PlayerDataStorageType type) {
        return this.activeDataStorage.stream().filter((stored) -> stored.getDataStorageType() == type).findFirst();
    }

    public Optional<PlayerDataStorage> getPrimaryDataStorage() {
        return this.getDataStorage(PlayerDataManager.PlayerDataStorageType.LOCAL);
    }

    public enum PlayerDataStorageType {
        LOCAL,
        GLOBAL;

        PlayerDataStorageType() {
        }
    }
}
