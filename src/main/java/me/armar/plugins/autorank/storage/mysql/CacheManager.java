package me.armar.plugins.autorank.storage.mysql;

import io.reactivex.annotations.NonNull;
import me.armar.plugins.autorank.storage.TimeType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CacheManager {
    private final Map<UUID, CachedEntry> cachedTimeValues = new HashMap();

    public CacheManager() {
    }

    public void registerCachedTime(@NonNull TimeType timeType, @NonNull UUID uuid, int value) {
        CachedEntry entry = this.cachedTimeValues.get(uuid);
        if (entry != null) {
            entry.setCachedTime(timeType, value);
        } else {
            entry = new CachedEntry(timeType, value);
        }

        this.cachedTimeValues.put(uuid, entry);
    }

    public int getCachedTime(@NonNull TimeType timeType, @NonNull UUID uuid) {
        CachedEntry entry = this.cachedTimeValues.get(uuid);
        return entry == null ? 0 : entry.getCachedTime(timeType).orElse(0);
    }

    public boolean hasCachedTime(@NonNull TimeType timeType, @NonNull UUID uuid) {
        if (!this.cachedTimeValues.containsKey(uuid)) {
            return false;
        } else {
            CachedEntry entry = this.cachedTimeValues.get(uuid);
            return entry != null && entry.hasCachedTime(timeType);
        }
    }

    public boolean shouldUpdateCachedEntry(@NonNull TimeType timeType, @NonNull UUID uuid) {
        return this.hasCachedTime(timeType, uuid) && this.cachedTimeValues.get(uuid).isCachedTimeOutdated(timeType);
    }

    public Set<UUID> getCachedUUIDs() {
        return this.cachedTimeValues.keySet();
    }
}
