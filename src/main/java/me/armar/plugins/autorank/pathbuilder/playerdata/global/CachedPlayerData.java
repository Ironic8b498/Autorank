package me.armar.plugins.autorank.pathbuilder.playerdata.global;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class CachedPlayerData {
    List<CachedPlayerDataEntry> cachedEntries = new ArrayList();

    CachedPlayerData() {
    }

    public void addCachedEntry(String completedPath, String serverName) {
        if (!this.hasCachedEntry(completedPath, serverName)) {
            this.cachedEntries.add(new CachedPlayerDataEntry(serverName, completedPath));
        }

    }

    public void removeCachedEntry(String completedPath, String serverName) {
        this.cachedEntries.remove(new CachedPlayerDataEntry(serverName, completedPath));
    }

    public boolean hasCachedEntry(String completedPath, String serverName) {
        return this.getCachedEntriesByPath(completedPath).stream().anyMatch((e) -> {
            return e.getServerName().equalsIgnoreCase(serverName);
        });
    }

    public List<CachedPlayerDataEntry> getCachedEntriesByPath(String completedPath) {
        return this.cachedEntries.stream().filter((entry) -> {
            return entry.getCompletedPath().equalsIgnoreCase(completedPath);
        }).collect(Collectors.toList());
    }

    public List<CachedPlayerDataEntry> getCachedEntriesByServer(String serverName) {
        return this.cachedEntries.stream().filter((entry) -> {
            return entry.getServerName().equalsIgnoreCase(serverName);
        }).collect(Collectors.toList());
    }
}
