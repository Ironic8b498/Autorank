package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;

import java.util.UUID;

public class FishCaughtRequirement extends AbstractRequirement {
    int fishCaught = -1;

    public FishCaughtRequirement() {
    }

    public String getDescription() {
        String lang = Lang.FISH_CAUGHT_REQUIREMENT.getConfigValue(this.fishCaught + "");
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(UUID uuid) {
        int progressBar = this.getStatisticsManager().getFishCaught(uuid, this.getWorld());
        return progressBar + "/" + this.fishCaught;
    }

    protected boolean meetsRequirement(UUID uuid) {
        return this.getStatisticsManager().getFishCaught(uuid, this.getWorld()) >= this.fishCaught;
    }

    public boolean initRequirement(String[] options) {
        try {
            this.fishCaught = Integer.parseInt(options[0]);
        } catch (NumberFormatException var3) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (this.fishCaught < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else {
            return true;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        int progressBar = this.getStatisticsManager().getFishCaught(uuid, this.getWorld());
        return (double)progressBar * 1.0D / (double)this.fishCaught;
    }
}
