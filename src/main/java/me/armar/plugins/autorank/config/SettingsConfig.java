package me.armar.plugins.autorank.config;

import me.armar.plugins.autorank.Autorank;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class SettingsConfig extends AbstractConfig {
    public String getMySQLSetting(SettingsConfig.MySQLSettings option) {
        switch(option) {
            case HOSTNAME:
                return this.getConfig().getString("sql.hostname");
            case USERNAME:
                return this.getConfig().getString("sql.username");
            case PASSWORD:
                return this.getConfig().getString("sql.password");
            case DATABASE:
                return this.getConfig().getString("sql.database");
            case TABLE_PREFIX:
                return this.getConfig().getString("sql.table prefix", "");
            case SERVER_NAME:
                return this.getConfig().getString("sql.server name", "").replace("%ip%", this.getPlugin().getServer().getIp()).replace("%port%", this.getPlugin().getServer().getPort() + "").replace("%name%", this.getPlugin().getServer().getName());
            case USESSL:
                return String.valueOf(this.getConfig().getBoolean("sql.usessl", false));
            default:
                throw new IllegalArgumentException(option + " is not a valid MySQL settings option");
        }
    }

    public SettingsConfig(Autorank instance) {
        this.setFileName("Settings.yml");
        this.setPlugin(instance);
    }

    public boolean updateConfigWithNewOptions() {
        if (!this.shouldAutoUpdateConfig()) {
            return false;
        } else {
            File settingsFile = new File(this.getPlugin().getDataFolder(), "Settings.yml");

            try {
                ConfigUpdater.update(this.getPlugin(), "Settings.yml", settingsFile, Collections.emptyList());
                this.reloadConfig();
                return true;
            } catch (IOException var3) {
                var3.printStackTrace();
                return false;
            }
        }
    }

    public void reloadConfig() {
        this.loadConfig();
    }

    public boolean doBaseHelpPageOnPermissions() {
        return this.getConfig().getBoolean("show help command based on permission", false);
    }

    public boolean doCheckForNewerVersion() {
        return this.getConfig().getBoolean("auto-updater.check-for-new-versions", true);
    }

    public String getCheckCommandLayout() {
        return this.getConfig().getString("check command layout", "&p has played for &time and is on path '&path'. Requirements to be ranked up: &reqs");
    }

    public int getIntervalTime() {
        return this.getConfig().getInt("interval check", 5);
    }

    public String getLeaderboardLayout() {
        return this.getConfig().getString("leaderboard layout", "&6&r | &b&p - &7&d day(s), &h hour(s) and &m minute(s).");
    }

    public int getLeaderboardLength() {
        return this.getConfig().getInt("leaderboard length", 10);
    }

    public boolean shouldBroadcastDataReset() {
        return this.getConfig().getBoolean("broadcast resetting of data files", true);
    }

    public boolean isAutomaticPathDisabled() {
        return this.getConfig().getBoolean("disable automatic path checking", false);
    }

    public boolean onlyUsePrimaryGroupVault() {
        return this.getConfig().getBoolean("use primary group for vault", true);
    }

    public boolean showWarnings() {
        return this.getConfig().getBoolean("show warnings", true);
    }

    public boolean useAdvancedDependencyLogs() {
        return this.getConfig().getBoolean("advanced dependency output", false);
    }

    public boolean useAFKIntegration() {
        return this.getConfig().getBoolean("afk integration", true);
    }

    public boolean useDebugOutput() {
        return this.getConfig().getBoolean("use debug", false);
    }

    public boolean useGlobalTimeInLeaderboard() {
        return this.getConfig().getBoolean("use global time in leaderboard", false);
    }

    public boolean useMySQL() {
        return this.getConfig().getBoolean("sql.enabled");
    }

    public boolean shouldRemoveOldEntries() {
        return this.getConfig().getBoolean("automatically archive old data", true);
    }

    public String getPrimaryStorageProvider() {
        return this.getConfig().getString("primary storage provider", "flatfile");
    }

    public int getBackupRemovalTime() {
        return this.getConfig().getInt("automatically remove backups if older than", 14);
    }

    public boolean shouldAutoUpdateConfig() {
        return this.getConfig().getBoolean("automatically update this file with new options", true);
    }

    public boolean isLoggingEnabled() {
        return this.getConfig().getBoolean("enable logging", true);
    }

    public boolean showDetailsOfPathWithOneActivePath() {
        return this.getConfig().getBoolean("show detailed path when only one active path", false);
    }

    public enum MySQLSettings {
        DATABASE,
        HOSTNAME,
        PASSWORD,
        SERVER_NAME,
        TABLE_PREFIX,
        USERNAME,
        USESSL;

        MySQLSettings() {
        }
    }
}
