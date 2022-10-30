package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;

import java.util.UUID;

public class AnimalsBredRequirement extends AbstractRequirement {
    int animalsBred = -1;

    public AnimalsBredRequirement() {
    }

    public String getDescription() {
        String lang = Lang.ANIMALS_BRED_REQUIREMENT.getConfigValue(this.animalsBred);
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(UUID uuid) {
        return this.getStatisticsManager().getAnimalsBred(uuid) + "/" + this.animalsBred;
    }

    protected boolean meetsRequirement(UUID uuid) {
        return this.getStatisticsManager().getAnimalsBred(uuid) >= this.animalsBred;
    }

    public boolean initRequirement(String[] options) {
        try {
            this.animalsBred = Integer.parseInt(options[0]);
        } catch (Exception var3) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (this.animalsBred < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else {
            return true;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        return (double) this.getStatisticsManager().getAnimalsBred(uuid) / (double)this.animalsBred;
    }
}
