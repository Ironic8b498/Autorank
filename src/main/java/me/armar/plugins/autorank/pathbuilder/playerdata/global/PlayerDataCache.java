package me.armar.plugins.autorank.pathbuilder.playerdata.global;

import io.reactivex.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataCache {
    private final Map<UUID, CachedPlayerData> cache = new HashMap();

    public PlayerDataCache() {
    }

    public CachedPlayerData getCachedPlayerData(@NonNull UUID uuid) {
        CachedPlayerData cachedPlayerData = this.cache.get(uuid);
        if (cachedPlayerData == null) {
            cachedPlayerData = new CachedPlayerData();
            this.setCachedPlayerData(uuid, cachedPlayerData);
        }

        return cachedPlayerData;
    }

    public void setCachedPlayerData(@NonNull UUID uuid, CachedPlayerData cachedPlayerData) {
        this.cache.put(uuid, cachedPlayerData);
    }

    public void removeCachedPlayerData(@NonNull UUID uuid) {
        this.cache.remove(uuid);
    }
}
