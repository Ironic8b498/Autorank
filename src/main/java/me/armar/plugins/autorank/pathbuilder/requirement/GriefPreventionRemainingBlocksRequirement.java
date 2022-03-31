package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.GriefPreventionHook;

import java.util.UUID;

public class GriefPreventionRemainingBlocksRequirement extends AbstractRequirement {
    private GriefPreventionHook handler = null;
    int remainingBlocks = -1;

    public GriefPreventionRemainingBlocksRequirement() {
    }

    public String getDescription() {
        return Lang.GRIEF_PREVENTION_REMAINING_BLOCKS_REQUIREMENT.getConfigValue(this.remainingBlocks);
    }

    public String getProgressString(UUID uuid) {
        return this.handler.getNumberOfRemainingBlocks(uuid) + "/" + this.remainingBlocks;
    }

    protected boolean meetsRequirement(UUID uuid) {
        if (!this.handler.isHooked()) {
            return false;
        } else {
            return this.handler.getNumberOfRemainingBlocks(uuid) >= this.remainingBlocks;
        }
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.GRIEFPREVENTION);
        this.handler = (GriefPreventionHook)this.getDependencyManager().getLibraryHook(Library.GRIEFPREVENTION).orElse(null);
        if (options.length > 0) {
            try {
                this.remainingBlocks = Integer.parseInt(options[0]);
            } catch (NumberFormatException var3) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (this.remainingBlocks < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else if (this.handler != null && this.handler.isHooked()) {
            return true;
        } else {
            this.registerWarningMessage("GriefPrevention is not available");
            return false;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        return (double)this.handler.getNumberOfRemainingBlocks(uuid) * 1.0D / (double)this.remainingBlocks;
    }
}
