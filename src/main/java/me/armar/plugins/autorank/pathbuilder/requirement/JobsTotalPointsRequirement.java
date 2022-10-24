package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.JobsHook;

import java.util.UUID;

public class JobsTotalPointsRequirement extends AbstractRequirement {
    private JobsHook jobsHandler;
    int totalPoints = -1;

    public JobsTotalPointsRequirement() {
    }

    public String getDescription() {
        String lang = Lang.JOBS_TOTAL_POINTS_REQUIREMENT.getConfigValue(this.totalPoints);
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(UUID uuid) {
        double points = 0.0D;
        if (this.jobsHandler != null && !this.jobsHandler.isHooked()) {
            points = this.jobsHandler.getTotalPoints(uuid);
        }

        return points + "/" + this.totalPoints;
    }

    protected boolean meetsRequirement(UUID uuid) {
        this.addDependency(Library.JOBS);
        double points = -1.0D;
        if (this.jobsHandler != null && this.jobsHandler.isHooked()) {
            points = this.jobsHandler.getTotalPoints(uuid);
        } else {
            points = -1.0D;
        }

        return points >= (double)this.totalPoints;
    }

    public boolean initRequirement(String[] options) {
        this.jobsHandler = (JobsHook)this.getAutorank().getDependencyManager().getLibraryHook(Library.JOBS).orElse(null);

        try {
            this.totalPoints = Integer.parseInt(options[0]);
        } catch (NumberFormatException var3) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (this.totalPoints < 0) {
            this.registerWarningMessage("No level is provided or smaller than 0.");
            return false;
        } else if (this.jobsHandler != null && this.jobsHandler.isHooked()) {
            return true;
        } else {
            this.registerWarningMessage("Jobs is not available");
            return false;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        double points = 0.0D;
        if (this.jobsHandler != null && !this.jobsHandler.isHooked()) {
            points = this.jobsHandler.getTotalPoints(uuid);
        }

        return points / (double)this.totalPoints;
    }
}
