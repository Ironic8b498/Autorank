package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.GriefPreventionHook;

import java.util.UUID;

public class GriefPreventionClaimedBlocksRequirement extends AbstractRequirement {
    int claimedBlocks = -1;
    private GriefPreventionHook handler = null;

    public GriefPreventionClaimedBlocksRequirement() {
    }

    public String getDescription() {
        return Lang.GRIEF_PREVENTION_CLAIMED_BLOCKS_REQUIREMENT.getConfigValue(this.claimedBlocks);
    }

    public String getProgressString(UUID uuid) {
        return this.handler.getNumberOfClaimedBlocks(uuid) + "/" + this.claimedBlocks;
    }

    protected boolean meetsRequirement(UUID uuid) {
        if (!this.handler.isHooked()) {
            return false;
        } else {
            return this.handler.getNumberOfClaimedBlocks(uuid) >= this.claimedBlocks;
        }
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.GRIEFPREVENTION);
        this.handler = (GriefPreventionHook)this.getDependencyManager().getLibraryHook(Library.GRIEFPREVENTION).orElse(null);
        if (options.length > 0) {
            try {
                this.claimedBlocks = Integer.parseInt(options[0]);
            } catch (NumberFormatException var3) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (this.claimedBlocks < 0) {
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
        return (double)this.handler.getNumberOfClaimedBlocks(uuid) * 1.0D / (double)this.claimedBlocks;
    }
}
