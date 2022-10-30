package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;

import java.util.UUID;

public class TimesShearedRequirement extends AbstractRequirement {
    int timesShorn = -1;

    public TimesShearedRequirement() {
    }

    public String getDescription() {
        String lang = Lang.TIMES_SHEARED_REQUIREMENT.getConfigValue(this.timesShorn + "");
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(UUID uuid) {
        int progressBar = this.getStatisticsManager().getSheepShorn(uuid, this.getWorld());
        return progressBar + "/" + this.timesShorn;
    }

    protected boolean meetsRequirement(UUID uuid) {
        return this.getStatisticsManager().getSheepShorn(uuid, this.getWorld()) >= this.timesShorn;
    }

    public boolean initRequirement(String[] options) {
        try {
            this.timesShorn = Integer.parseInt(options[0]);
        } catch (Exception var3) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (this.timesShorn < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else {
            return true;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        return (double) this.getStatisticsManager().getSheepShorn(uuid, this.getWorld()) / (double)this.timesShorn;
    }
}
