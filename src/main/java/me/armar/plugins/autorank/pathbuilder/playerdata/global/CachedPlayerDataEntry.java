package me.armar.plugins.autorank.pathbuilder.playerdata.global;

import io.reactivex.annotations.NonNull;

class CachedPlayerDataEntry {
    private String serverName;
    private String completedPath;

    public CachedPlayerDataEntry(@NonNull String serverName, @NonNull String completedPath) {
        this.serverName = serverName;
        this.completedPath = completedPath;
    }

    public String getServerName() {
        return this.serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getCompletedPath() {
        return this.completedPath;
    }

    public void setCompletedPath(String completedPath) {
        this.completedPath = completedPath;
    }

    public int hashCode() {
        return (this.serverName + this.completedPath).hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof CachedPlayerDataEntry)) {
            return false;
        } else {
            CachedPlayerDataEntry entry = (CachedPlayerDataEntry)obj;
            return entry.getServerName().equalsIgnoreCase(this.getServerName()) && entry.getCompletedPath().equalsIgnoreCase(this.getCompletedPath());
        }
    }
}
