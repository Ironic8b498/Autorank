package me.armar.plugins.autorank.storage.mysql;

import io.reactivex.annotations.NonNull;
import me.armar.plugins.autorank.storage.TimeType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class CachedEntry {
    private final Map<TimeType, Integer> timePerTimeType = new HashMap();
    private final Map<TimeType, Long> lastUpdatedPerTimeType = new HashMap();

    public CachedEntry() {
    }

    public CachedEntry(@NonNull TimeType timeType, int value) {
        this.setCachedTime(timeType, value);
    }

    public Optional<Long> getMinutesSinceLastUpdated(@NonNull TimeType timeType) {
        Long lastUpdatedTime = this.lastUpdatedPerTimeType.get(timeType);
        return lastUpdatedTime == null ? Optional.empty() : Optional.of((System.currentTimeMillis() - lastUpdatedTime) / 60000L);
    }

    public void setCachedTime(@NonNull TimeType timeType, int time) {
        this.timePerTimeType.put(timeType, time);
        this.lastUpdatedPerTimeType.put(timeType, System.currentTimeMillis());
    }

    public Optional<Integer> getCachedTime(@NonNull TimeType timeType) {
        return Optional.ofNullable(this.timePerTimeType.get(timeType));
    }

    public boolean hasCachedTime(@NonNull TimeType timeType) {
        return this.timePerTimeType.containsKey(timeType);
    }

    public boolean isCachedTimeOutdated(@NonNull TimeType timeType) {
        return this.hasCachedTime(timeType) && this.getMinutesSinceLastUpdated(timeType).orElseGet(() -> {
            return (long)MySQLStorageProvider.CACHE_EXPIRY_TIME;
        }) >= (long)MySQLStorageProvider.CACHE_EXPIRY_TIME;
    }

    public int hashCode() {
        return (this.timePerTimeType.toString() + this.lastUpdatedPerTimeType).hashCode();
    }
}
