package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.hooks.PlaceholderAPIHook;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlaceholderapiStringRequirement extends AbstractRequirement {
    private String placeholderStr = "null";
    private String placeholderVal = "null";
    private String placeholderDef = "null";

    private final PlaceholderAPIHook handler = null;

    public PlaceholderapiStringRequirement() {
    }

    public String getDescription() {
        return Lang.PLACEHOLDERAPI_STRING_REQUIREMENT.getConfigValue(this.placeholderStr, this.placeholderDef);
    }

    public String getProgressString(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        String placeholderValue = PlaceholderAPI.setPlaceholders(player, placeholderVal);
        return placeholderValue + "/" + this.placeholderStr;
    }


    public boolean meetsRequirement(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        String placeholderValue = PlaceholderAPI.setPlaceholders(player, placeholderVal);
        return placeholderValue.equalsIgnoreCase(this.placeholderStr);
    }

    public boolean initRequirement(String[] options) {
        if (options.length > 0) {
            this.placeholderStr = options[0];
        }
        if (options.length > 1) {
            this.placeholderVal = options[1];
        }

        if (options.length > 2) {
            this.placeholderDef = options[2];
        }

        if (this.placeholderStr == null) {
            this.registerWarningMessage("No string is specified");
            return false;
        } else {
            return true;
        }
    }
}
