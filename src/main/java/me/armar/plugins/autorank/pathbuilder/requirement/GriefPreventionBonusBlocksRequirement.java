package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.GriefPreventionHook;

import java.util.UUID;

public class GriefPreventionBonusBlocksRequirement extends AbstractRequirement {
    int bonusBlocks = -1;
    private GriefPreventionHook handler = null;

    public GriefPreventionBonusBlocksRequirement() {
    }

    public String getDescription() {
        return Lang.GRIEF_PREVENTION_BONUS_BLOCKS_REQUIREMENT.getConfigValue(this.bonusBlocks);
    }

    public String getProgressString(UUID uuid) {
        return this.handler.getNumberOfBonusBlocks(uuid) + "/" + this.bonusBlocks;
    }

    protected boolean meetsRequirement(UUID uuid) {
        if (!this.handler.isHooked()) {
            return false;
        } else {
            return this.handler.getNumberOfBonusBlocks(uuid) >= this.bonusBlocks;
        }
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.GRIEFPREVENTION);
        this.handler = (GriefPreventionHook)this.getDependencyManager().getLibraryHook(Library.GRIEFPREVENTION).orElse(null);
        if (options.length > 0) {
            try {
                this.bonusBlocks = Integer.parseInt(options[0]);
            } catch (NumberFormatException var3) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (this.bonusBlocks < 0) {
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
        return (double)this.handler.getNumberOfBonusBlocks(uuid) * 1.0D / (double)this.bonusBlocks;
    }
}
