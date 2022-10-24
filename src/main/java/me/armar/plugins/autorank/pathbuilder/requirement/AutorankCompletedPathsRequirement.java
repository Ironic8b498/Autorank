package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;

import java.util.UUID;

public class AutorankCompletedPathsRequirement extends AbstractRequirement {
    private int requiredPaths = -1;
    private Path requiredPath;
    private String requiredPathName;

    public AutorankCompletedPathsRequirement() {
    }

    public String getDescription() {
        if (this.requiredPaths > 0) {
            return Lang.AUTORANK_NUMBER_OF_COMPLETED_PATHS_REQUIREMENT.getConfigValue(this.requiredPaths);
        } else {
            if (this.requiredPath == null) {
                this.findMatchingPath();
            }

            return Lang.AUTORANK_SPECIFIC_COMPLETED_PATH_REQUIREMENT.getConfigValue(this.requiredPath.getDisplayName());
        }
    }

    public String getProgressString(UUID uuid) {
        if (this.requiredPaths > 0) {
            return this.getAutorank().getPathManager().getCompletedPaths(uuid).size() + "/" + this.requiredPaths;
        } else {
            if (this.requiredPath == null) {
                this.findMatchingPath();
            }

            return "has completed " + this.requiredPath.getDisplayName() + ": " + this.requiredPath.hasCompletedPath(uuid);
        }
    }

    protected boolean meetsRequirement(UUID uuid) {
        if (this.requiredPaths > 0) {
            return this.getAutorank().getPathManager().getCompletedPaths(uuid).size() >= this.requiredPaths;
        } else {
            if (this.requiredPath == null) {
                this.findMatchingPath();
            }

            return this.requiredPath.hasCompletedPath(uuid);
        }
    }

    public boolean initRequirement(String[] options) {
        if (options.length > 0) {
            try {
                this.requiredPaths = Integer.parseInt(options[0]);
            } catch (NumberFormatException var3) {
                this.requiredPathName = options[0];
                return true;
            }
        }

        if (this.requiredPaths < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else {
            return true;
        }
    }

    public boolean needsOnlinePlayer() {
        return false;
    }

    private void findMatchingPath() {
        this.requiredPath = this.getAutorank().getPathManager().findPathByDisplayName(this.requiredPathName, false);
        if (this.requiredPath == null) {
            this.requiredPath = this.getAutorank().getPathManager().findPathByInternalName(this.requiredPathName, false);
        }

        if (this.requiredPath == null) {
            this.registerWarningMessage("There is no path called " + this.requiredPathName);
        }

    }
}
