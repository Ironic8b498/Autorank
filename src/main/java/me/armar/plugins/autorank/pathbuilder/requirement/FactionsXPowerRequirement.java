package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.FactionXHook;

import java.text.DecimalFormat;
import java.util.UUID;

public class FactionsXPowerRequirement extends AbstractRequirement {
    double factionPower = -1.0D;
    private FactionXHook handler;

    public FactionsXPowerRequirement() {
    }

    public String getDescription() {
        String lang = Lang.FACTIONS_POWER_REQUIREMENT.getConfigValue(this.factionPower + "");
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(UUID uuid) {
        DecimalFormat df = new DecimalFormat("#.##");
        String doubleRounded = df.format(this.handler.getFactionPower(uuid));
        return doubleRounded + "/" + this.factionPower;
    }

    protected boolean meetsRequirement(UUID uuid) {
        return this.handler.getFactionPower(uuid) >= this.factionPower;
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.FACTIONSX);
        this.handler = (FactionXHook)this.getAutorank().getDependencyManager().getLibraryHook(Library.FACTIONSX).orElse(null);

        try {
            this.factionPower = Double.parseDouble(options[0]);
        } catch (NumberFormatException var3) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (this.factionPower < 0.0D) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else if (this.handler != null && this.handler.isHooked()) {
            return true;
        } else {
            this.registerWarningMessage("FactionX is not available");
            return false;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        return this.handler.getFactionPower(uuid) * 1.0D / this.factionPower;
    }
}
