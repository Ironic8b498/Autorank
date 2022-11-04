package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.JobsHook;

import java.util.UUID;

public class JobsCurrentPointsRequirement extends AbstractRequirement {
    int currentPoints = -1;
    private JobsHook jobsHandler;

    public JobsCurrentPointsRequirement() {
    }

    public String getDescription() {
        String lang = Lang.JOBS_CURRENT_POINTS_REQUIREMENT.getConfigValue(this.currentPoints);
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(UUID uuid) {
        double points = -1.0D;
        if (this.jobsHandler != null && this.jobsHandler.isHooked()) {
            points = this.jobsHandler.getCurrentPoints(uuid);
        } else {
            points = -1.0D;
        }

        return points + "/" + this.currentPoints;
    }

    protected boolean meetsRequirement(UUID uuid) {
        double points = -1.0D;
        if (this.jobsHandler != null && this.jobsHandler.isHooked()) {
            points = this.jobsHandler.getCurrentPoints(uuid);
        } else {
            points = -1.0D;
        }

        return points >= (double)this.currentPoints;
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.JOBS);
        this.jobsHandler = (JobsHook)this.getAutorank().getDependencyManager().getLibraryHook(Library.JOBS).orElse(null);

        try {
            this.currentPoints = Integer.parseInt(options[0]);
        } catch (NumberFormatException var3) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (this.currentPoints < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
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
        if (this.jobsHandler != null && this.jobsHandler.isHooked()) {
            points = this.jobsHandler.getCurrentPoints(uuid);
        }

        return points / (double)this.currentPoints;
    }
}
