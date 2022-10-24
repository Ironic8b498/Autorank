package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;

import java.util.UUID;

public class DamageTakenRequirement extends AbstractRequirement {
    int damageTaken = -1;

    public DamageTakenRequirement() {
    }

    public String getDescription() {
        String lang = Lang.DAMAGE_TAKEN_REQUIREMENT.getConfigValue(this.damageTaken + "");
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(UUID uuid) {
        int damTaken = this.getStatisticsManager().getDamageTaken(uuid, this.getWorld());
        return damTaken + "/" + this.damageTaken;
    }

    protected boolean meetsRequirement(UUID uuid) {
        return this.getStatisticsManager().getDamageTaken(uuid, this.getWorld()) >= this.damageTaken;
    }

    public boolean initRequirement(String[] options) {
        try {
            this.damageTaken = Integer.parseInt(options[0]);
        } catch (NumberFormatException var3) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (this.damageTaken < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else {
            return true;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        int damTaken = this.getStatisticsManager().getDamageTaken(uuid, this.getWorld());
        return (double)damTaken * 1.0D / (double)this.damageTaken;
    }
}
