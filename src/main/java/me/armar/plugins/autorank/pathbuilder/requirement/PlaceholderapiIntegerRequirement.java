package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.PlaceholderAPIHook;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlaceholderapiIntegerRequirement extends AbstractRequirement {
    private int placeholderInt = -1;
    private String placeholderVal = "null";
    private String placeholderDef = "null";

    private PlaceholderAPIHook handler = null;

    public PlaceholderapiIntegerRequirement() {
    }

    public String getDescription() {
        return Lang.PLACEHOLDERAPI_INTEGER_REQUIREMENT.getConfigValue(this.placeholderInt, this.placeholderDef);
    }

    public String getProgressString(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        String placeholderValue = PlaceholderAPI.setPlaceholders(player, placeholderVal);
        return placeholderValue + "/" + this.placeholderInt;
    }


    public boolean meetsRequirement(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        Integer placeholderValue = Integer.valueOf(PlaceholderAPI.setPlaceholders(player, this.placeholderVal));
        return placeholderValue >= this.placeholderInt;
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.PLACEHOLDERAPI);
        this.handler = (PlaceholderAPIHook) this.getAutorank().getDependencyManager().getLibraryHook(Library.PLACEHOLDERAPI).orElse(null);

        try {
            this.placeholderInt = Integer.parseInt(options[0]);
        } catch (NumberFormatException var3) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (options.length > 1) {
            this.placeholderVal = options[1];
        }

        if (options.length > 2) {
            this.placeholderDef = options[2];
        }

        if (this.placeholderInt < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else if (this.handler != null && this.handler.isHooked()) {
            return true;
        } else {
            this.registerWarningMessage("Last Login is not available");
            return false;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        Integer placeholderValue = Integer.valueOf(PlaceholderAPI.setPlaceholders(player, placeholderVal));
        return (double) placeholderValue / (double) this.placeholderInt;
    }
}
