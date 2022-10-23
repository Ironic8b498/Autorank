package me.armar.plugins.autorank.updater;

import me.armar.plugins.autorank.Autorank;

public class UpdateHandler {
    private final Autorank plugin;
    private long latestCheck = 0L;
    private final SpigotUpdater updater;
    private boolean lastResult;

    public UpdateHandler(Autorank instance) {
        this.plugin = instance;
        this.updater = new SpigotUpdater(instance, 3239);
    }

    public boolean doCheckForNewVersion() {
        return this.plugin.getSettingsConfig().doCheckForNewerVersion();
    }

    public boolean isUpdateAvailable() {
        if (!this.plugin.isDevVersion() && this.doCheckForNewVersion()) {
            return (System.currentTimeMillis() - this.latestCheck) / 60000L >= 60L ? this.checkForUpdate() : this.lastResult;
        } else {
            return false;
        }
    }

    public SpigotUpdater getUpdater() {
        return this.updater;
    }

    public boolean checkForUpdate() {
        try {
            this.latestCheck = System.currentTimeMillis();
            this.lastResult = this.updater.checkForUpdates();
            return this.lastResult;
        } catch (Exception var2) {
            var2.printStackTrace();
            return false;
        }
    }
}
