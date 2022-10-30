package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;

import java.util.UUID;

public class TimesDiedRequirement extends AbstractRequirement {
    int timesDied = -1;

    public TimesDiedRequirement() {
    }

    public String getDescription() {
        String lang = Lang.TIMES_DIED_REQUIREMENT.getConfigValue(this.timesDied);
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(UUID uuid) {
        return this.getStatisticsManager().getTimesDied(uuid) + "/" + this.timesDied;
    }

    protected boolean meetsRequirement(UUID uuid) {
        return this.getStatisticsManager().getTimesDied(uuid) >= this.timesDied;
    }

    public boolean initRequirement(String[] options) {
        try {
            this.timesDied = Integer.parseInt(options[0]);
        } catch (Exception var3) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (this.timesDied < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else {
            return true;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        return (double) this.getStatisticsManager().getTimesDied(uuid) / (double)this.timesDied;
    }
}
