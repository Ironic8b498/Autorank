package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.PlayerPointsHook;

import java.util.UUID;

public class PlayerPointsPointsRequirement extends AbstractRequirement {
    private PlayerPointsHook handler = null;
    private int requiredPoints = -1;

    public PlayerPointsPointsRequirement() {
    }

    public String getDescription() {
        return Lang.PLAYERPOINTS_POINTS_REQUIREMENT.getConfigValue(this.requiredPoints);
    }

    public String getProgressString(UUID uuid) {
        return this.handler.getPlayerPoints(uuid) + "/" + this.requiredPoints;
    }

    protected boolean meetsRequirement(UUID uuid) {
        if (!this.handler.isHooked()) {
            return false;
        } else {
            return this.handler.getPlayerPoints(uuid) >= this.requiredPoints;
        }
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.PLAYERPOINTS);
        this.handler = (PlayerPointsHook)this.getDependencyManager().getLibraryHook(Library.PLAYERPOINTS).orElse(null);
        if (options.length > 0) {
            try {
                this.requiredPoints = Integer.parseInt(options[0]);
            } catch (NumberFormatException var3) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (this.requiredPoints < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else {
            return this.handler != null;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        return (double) this.handler.getPlayerPoints(uuid) / (double)this.requiredPoints;
    }
}
