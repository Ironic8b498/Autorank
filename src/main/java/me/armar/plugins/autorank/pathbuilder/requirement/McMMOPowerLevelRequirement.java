package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.McMMOHook;
import org.bukkit.entity.Player;

public class McMMOPowerLevelRequirement extends AbstractRequirement {
    private McMMOHook handler = null;
    int powerLevel = -1;

    public McMMOPowerLevelRequirement() {
    }

    public String getDescription() {
        return Lang.MCMMO_POWER_LEVEL_REQUIREMENT.getConfigValue(this.powerLevel + "");
    }

    public String getProgressString(Player player) {
        int level = this.handler.getPowerLevel(player);
        return level + "/" + this.powerLevel;
    }

    public boolean meetsRequirement(Player player) {
        if (!this.handler.isHooked()) {
            return false;
        } else {
            int level = this.handler.getPowerLevel(player);
            return level >= this.powerLevel;
        }
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.MCMMO);
        this.handler = (McMMOHook)this.getDependencyManager().getLibraryHook(Library.MCMMO).orElse(null);
        if (options.length > 0) {
            this.powerLevel = Integer.parseInt(options[0]);
        }

        if (this.powerLevel < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else if (this.handler != null && this.handler.isHooked()) {
            return this.handler != null;
        } else {
            this.registerWarningMessage("mcMMO is not available");
            return false;
        }
    }

    public boolean needsOnlinePlayer() {
        return true;
    }
}
