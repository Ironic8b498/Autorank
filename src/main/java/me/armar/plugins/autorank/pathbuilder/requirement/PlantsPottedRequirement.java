package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;

import java.util.UUID;

public class PlantsPottedRequirement extends AbstractRequirement {
    int plantsPotted = -1;

    public PlantsPottedRequirement() {
    }

    public String getDescription() {
        String lang = Lang.PLANTS_POTTED_REQUIREMENT.getConfigValue(this.plantsPotted);
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(UUID uuid) {
        return this.getStatisticsManager().getPlantsPotted(uuid) + "/" + this.plantsPotted;
    }

    protected boolean meetsRequirement(UUID uuid) {
        return this.getStatisticsManager().getPlantsPotted(uuid) >= this.plantsPotted;
    }

    public boolean initRequirement(String[] options) {
        try {
            this.plantsPotted = Integer.parseInt(options[0]);
        } catch (Exception var3) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (this.plantsPotted < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else {
            return true;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        return (double)this.getStatisticsManager().getPlantsPotted(uuid) * 1.0D / (double)this.plantsPotted;
    }
}
