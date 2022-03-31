package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class TimeWeeklyRequirement extends AbstractRequirement {
    int timeNeeded = -1;

    public TimeWeeklyRequirement() {
    }

    public String getDescription() {
        return Lang.TIME_WEEKLY_REQUIREMENT.getConfigValue(AutorankTools.timeToString(this.timeNeeded, TimeUnit.MINUTES));
    }

    public String getProgressString(UUID uuid) {
        int playtime = 0;

        try {
            playtime = this.getAutorank().getPlayTimeManager().getPlayTime(TimeType.WEEKLY_TIME, uuid).get();
        } catch (ExecutionException | InterruptedException var4) {
            var4.printStackTrace();
        }

        return AutorankTools.timeToString(playtime, TimeUnit.MINUTES) + "/" + AutorankTools.timeToString(this.timeNeeded, TimeUnit.MINUTES);
    }

    protected boolean meetsRequirement(UUID uuid) {
        int playTime = 0;

        try {
            playTime = this.getAutorank().getPlayTimeManager().getPlayTime(TimeType.WEEKLY_TIME, uuid).get();
        } catch (ExecutionException | InterruptedException var4) {
            var4.printStackTrace();
        }

        return this.timeNeeded != -1 && playTime >= this.timeNeeded;
    }

    public boolean initRequirement(String[] options) {
        if (options.length > 0) {
            this.timeNeeded = AutorankTools.stringToTime(options[0], TimeUnit.MINUTES);
        }

        if (this.timeNeeded < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else {
            return true;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        int playTime = 0;

        try {
            playTime = this.getAutorank().getPlayTimeManager().getPlayTime(TimeType.WEEKLY_TIME, uuid).get();
        } catch (ExecutionException | InterruptedException var4) {
            var4.printStackTrace();
        }

        return (double)playTime * 1.0D / (double)this.timeNeeded;
    }
}
