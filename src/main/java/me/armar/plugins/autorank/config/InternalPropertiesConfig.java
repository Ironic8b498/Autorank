package me.armar.plugins.autorank.config;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.storage.TimeType;

import java.util.ArrayList;
import java.util.List;

public class InternalPropertiesConfig extends AbstractConfig {
    public InternalPropertiesConfig(Autorank instance) {
        this.setPlugin(instance);
        this.setFileName("internalprops.yml");
    }

    public List<String> getCachedLeaderboard(TimeType type) {
        return this.getConfig().getStringList("leaderboards." + type.toString().toLowerCase() + ".cached leaderboard");
    }

    public long getLeaderboardLastUpdateTime(TimeType type) {
        return this.getConfig().getLong("leaderboards." + type.toString().toLowerCase() + ".last updated", 0L);
    }

    public int getTrackedTimeType(TimeType type) {
        if (type == TimeType.DAILY_TIME) {
            return this.getConfig().getInt("tracked day", 1);
        } else if (type == TimeType.WEEKLY_TIME) {
            return this.getConfig().getInt("tracked week", 1);
        } else {
            return type == TimeType.MONTHLY_TIME ? this.getConfig().getInt("tracked month", 1) : 0;
        }
    }

    public boolean hasTransferredUUIDs() {
        return this.getConfig().getBoolean("has converted uuids", false);
    }

    public void hasTransferredUUIDs(boolean value) {
        this.getConfig().set("has converted uuids", value);
        this.saveConfig();
    }

    public boolean loadConfig() {
        boolean loaded = super.loadConfig();
        if (!loaded) {
            return false;
        } else {
            this.getConfig().options().header("This is the internal properties file of Autorank. \nYou should not touch any values here, unless instructed by a developer.\nAutorank uses these to keep track of certain aspects of the plugin.");
            this.getConfig().addDefault("leaderboard last updated", 0);
            this.getConfig().addDefault("has converted uuids", false);
            this.getConfig().addDefault("tracked month", 1);
            this.getConfig().addDefault("tracked week", 1);
            this.getConfig().addDefault("tracked day", 1);
            List<String> newList = new ArrayList();
            newList.add("&cThis leaderboard wasn't set up yet.");
            this.getConfig().addDefault("leaderboards.total_time.cached leaderboard", newList);
            this.getConfig().addDefault("leaderboards.daily_time.cached leaderboard", newList);
            this.getConfig().addDefault("leaderboards.weekly_time.cached leaderboard", newList);
            this.getConfig().addDefault("leaderboards.monthly_time.cached leaderboard", newList);
            this.getConfig().options().copyDefaults(true);
            this.saveConfig();
            return true;
        }
    }

    public void setCachedLeaderboard(TimeType type, List<String> cachedLeaderboard) {
        this.getConfig().set("leaderboards." + type.toString().toLowerCase() + ".cached leaderboard", cachedLeaderboard);
        this.saveConfig();
    }

    public void setLeaderboardLastUpdateTime(TimeType type, long time) {
        this.getConfig().set("leaderboards." + type.toString().toLowerCase() + ".last updated", time);
        this.saveConfig();
    }

    public void setTrackedTimeType(TimeType type, int value) {
        if (type == TimeType.DAILY_TIME) {
            this.getConfig().set("tracked day", value);
        } else if (type == TimeType.WEEKLY_TIME) {
            this.getConfig().set("tracked week", value);
        } else {
            if (type != TimeType.MONTHLY_TIME) {
                return;
            }

            this.getConfig().set("tracked month", value);
        }

        this.saveConfig();
    }

    public boolean isConvertedToNewFormat() {
        return this.getConfig().getBoolean("is converted to new format", false);
    }

    public void setConvertedToNewFormat(boolean value) {
        this.getConfig().set("is converted to new format", value);
        this.saveConfig();
    }
}
