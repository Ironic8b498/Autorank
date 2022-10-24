package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.FactionXHook;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class FactionPowerRequirement extends AbstractRequirement {
    double factionPower = -1.0D;
    private FactionXHook handler;

    public FactionPowerRequirement() {
    }

    public String getDescription() {
        String lang = Lang.FACTIONS_POWER_REQUIREMENT.getConfigValue(this.factionPower + "");
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(Player player) {
        DecimalFormat df = new DecimalFormat("#.##");
        String doubleRounded = df.format(this.handler.getFactionPower(player.getUniqueId()));
        return doubleRounded + "/" + this.factionPower;
    }

    public boolean meetsRequirement(Player player) {
        if (this.isWorldSpecific() && !this.getWorld().equals(player.getWorld().getName())) {
            return false;
        } else {
            double factionPower = this.handler.getFactionPower(player.getUniqueId());
            return factionPower >= this.factionPower;
        }
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
            this.registerWarningMessage("Factions is not available");
            return false;
        }
    }

    public boolean needsOnlinePlayer() {
        return true;
    }
}
