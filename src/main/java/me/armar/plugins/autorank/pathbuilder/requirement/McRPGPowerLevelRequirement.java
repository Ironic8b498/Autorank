package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.McRPGHook;

import java.util.UUID;

public class McRPGPowerLevelRequirement extends AbstractRequirement {
    int powerLevel = -1;
    private McRPGHook hook = null;

    public McRPGPowerLevelRequirement() {
    }

    public String getDescription() {
        return Lang.MCRPG_POWER_LEVEL_REQUIREMENT.getConfigValue(this.powerLevel);
    }

    public String getProgressString(UUID uuid) {
        return this.hook.getPowerLevel(uuid) + "/" + this.powerLevel;
    }

    public boolean meetsRequirement(UUID uuid) {
        if (!this.hook.isHooked()) {
            return false;
        } else {
            return this.hook.getPowerLevel(uuid) >= this.powerLevel;
        }
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.MCRPG);
        this.hook = (McRPGHook)this.getDependencyManager().getLibraryHook(Library.MCRPG).orElse(null);
        if (options.length > 0) {
            this.powerLevel = Integer.parseInt(options[0]);
        }

        if (this.powerLevel < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else if (this.hook != null && this.hook.isHooked()) {
            return true;
        } else {
            this.registerWarningMessage("McRPG is not available");
            return false;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        return (double) this.hook.getPowerLevel(uuid) / (double)this.powerLevel;
    }
}
