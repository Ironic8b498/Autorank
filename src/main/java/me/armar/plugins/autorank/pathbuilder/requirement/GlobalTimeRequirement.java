package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.storage.PlayTimeStorageProvider.StorageType;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GlobalTimeRequirement extends AbstractRequirement {
    private int globalTime = -1;

    public GlobalTimeRequirement() {
    }

    public String getDescription() {
        return Lang.GLOBAL_TIME_REQUIREMENT.getConfigValue(AutorankTools.timeToString(this.globalTime, TimeUnit.MINUTES));
    }

    public String getProgressString(UUID uuid) {
        int playTime = 0;

        try {
            playTime = this.getAutorank().getPlayTimeManager().getGlobalPlayTime(TimeType.TOTAL_TIME, uuid).get();
        } catch (ExecutionException | InterruptedException var4) {
            var4.printStackTrace();
        }

        return AutorankTools.timeToString(playTime, TimeUnit.MINUTES) + "/" + AutorankTools.timeToString(this.globalTime, TimeUnit.MINUTES);
    }

    protected boolean meetsRequirement(UUID uuid) {
        int playTime = 0;

        try {
            playTime = this.getAutorank().getPlayTimeManager().getGlobalPlayTime(TimeType.TOTAL_TIME, uuid).get();
        } catch (ExecutionException | InterruptedException var4) {
            var4.printStackTrace();
        }

        return this.globalTime != -1 && playTime >= this.globalTime;
    }

    public boolean initRequirement(String[] options) {
        this.globalTime = AutorankTools.stringToTime(options[0], TimeUnit.MINUTES);
        if (this.globalTime < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else if (!this.getAutorank().getPlayTimeStorageManager().isStorageTypeActive(StorageType.DATABASE)) {
            this.registerWarningMessage("There is no active storage provider that supports global time!");
            return false;
        } else {
            return true;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        int playTime = 0;

        try {
            playTime = this.getAutorank().getPlayTimeManager().getGlobalPlayTime(TimeType.TOTAL_TIME, uuid).get();
        } catch (ExecutionException | InterruptedException var4) {
            var4.printStackTrace();
        }

        return (double) playTime / (double)this.globalTime;
    }
}
