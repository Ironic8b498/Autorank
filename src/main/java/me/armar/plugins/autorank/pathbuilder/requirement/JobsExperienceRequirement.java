package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.JobsHook;
import org.bukkit.entity.Player;

public class JobsExperienceRequirement extends AbstractRequirement {
    int experience = -1;
    String jobName;
    private JobsHook jobsHandler;

    public JobsExperienceRequirement() {
    }

    public String getDescription() {
        String lang = Lang.JOBS_EXPERIENCE_REQUIREMENT.getConfigValue(this.experience, this.jobName);
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(Player player) {
        double points = 0.0D;
        if (this.jobsHandler != null && this.jobsHandler.isHooked()) {
            points = this.jobsHandler.getCurrentXP(player, this.jobName);
        }

        return points + "/" + this.experience;
    }

    public boolean meetsRequirement(Player player) {
        double points = -1.0D;
        if (this.jobsHandler != null && this.jobsHandler.isHooked()) {
            points = this.jobsHandler.getCurrentXP(player, this.jobName);
        } else {
            points = -1.0D;
        }

        return points >= (double)this.experience;
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.JOBS);
        this.jobsHandler = (JobsHook)this.getAutorank().getDependencyManager().getLibraryHook(Library.JOBS).orElse(null);

        try {
            this.experience = Integer.parseInt(options[0]);
        } catch (NumberFormatException var3) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (options.length > 1) {
            this.jobName = options[1];
        }

        if (this.experience < 0) {
            this.registerWarningMessage("No experience level is provided or smaller than 0.");
            return false;
        } else if (this.jobsHandler != null && this.jobsHandler.isHooked()) {
            if (this.jobName == null) {
                this.registerWarningMessage("No job name is provided");
                return false;
            } else {
                return true;
            }
        } else {
            this.registerWarningMessage("Jobs is not available");
            return false;
        }
    }

    public boolean needsOnlinePlayer() {
        return true;
    }
}
