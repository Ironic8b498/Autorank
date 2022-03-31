package me.armar.plugins.autorank.leaderboard;

import me.armar.plugins.autorank.storage.TimeType;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class AutorankLeaderboard {
    private TimeType timeType;
    private final Map<String, Integer> leaderboard = new LinkedHashMap();
    private boolean isSorted = false;

    public AutorankLeaderboard(TimeType timeType) {
        this.setTimeType(timeType);
    }

    public TimeType getTimeType() {
        return this.timeType;
    }

    public void setTimeType(TimeType timeType) {
        this.timeType = timeType;
    }

    public void addAll(Map<String, Integer> map) {
        this.leaderboard.putAll(map);
        this.isSorted = false;
    }

    public void add(String playerName, int value) {
        this.leaderboard.put(playerName, value);
        this.isSorted = false;
    }

    public void sortLeaderboard() {
        if (!this.isSorted()) {
            LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();
            leaderboard.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
            this.isSorted = true;
        }
    }

    public Map<String, Integer> getLeaderboard() {
        return Collections.unmodifiableMap(this.leaderboard);
    }

    public boolean isSorted() {
        return this.isSorted;
    }

    public void setSorted(boolean sorted) {
        this.isSorted = sorted;
    }

    public int size() {
        return this.leaderboard.size();
    }
}
