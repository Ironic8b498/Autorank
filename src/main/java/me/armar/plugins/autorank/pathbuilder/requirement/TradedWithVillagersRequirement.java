package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;

import java.util.UUID;

public class TradedWithVillagersRequirement extends AbstractRequirement {
    int tradedWithVillagers = -1;

    public TradedWithVillagersRequirement() {
    }

    public String getDescription() {
        String lang = Lang.TIMES_TRADED_WITH_VILLAGERS_REQUIREMENT.getConfigValue(this.tradedWithVillagers);
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(UUID uuid) {
        return this.getStatisticsManager().getTimesTradedWithVillagers(uuid) + "/" + this.tradedWithVillagers;
    }

    protected boolean meetsRequirement(UUID uuid) {
        return this.getStatisticsManager().getTimesTradedWithVillagers(uuid) >= this.tradedWithVillagers;
    }

    public boolean initRequirement(String[] options) {
        try {
            this.tradedWithVillagers = Integer.parseInt(options[0]);
        } catch (Exception var3) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (this.tradedWithVillagers < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else {
            return true;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        return (double)this.getStatisticsManager().getTimesTradedWithVillagers(uuid) * 1.0D / (double)this.tradedWithVillagers;
    }
}
