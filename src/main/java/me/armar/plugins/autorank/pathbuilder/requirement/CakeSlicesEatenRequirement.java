package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;

import java.util.UUID;

public class CakeSlicesEatenRequirement extends AbstractRequirement {
    int cakeSlicesEaten = -1;

    public CakeSlicesEatenRequirement() {
    }

    public String getDescription() {
        String lang = Lang.CAKESLICES_EATEN_REQUIREMENT.getConfigValue(this.cakeSlicesEaten);
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(UUID uuid) {
        return this.getStatisticsManager().getCakeSlicesEaten(uuid) + "/" + this.cakeSlicesEaten;
    }

    protected boolean meetsRequirement(UUID uuid) {
        return this.getStatisticsManager().getCakeSlicesEaten(uuid) >= this.cakeSlicesEaten;
    }

    public boolean initRequirement(String[] options) {
        try {
            this.cakeSlicesEaten = Integer.parseInt(options[0]);
        } catch (Exception var3) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (this.cakeSlicesEaten < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else {
            return true;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        return (double) this.getStatisticsManager().getCakeSlicesEaten(uuid) / (double)this.cakeSlicesEaten;
    }
}
