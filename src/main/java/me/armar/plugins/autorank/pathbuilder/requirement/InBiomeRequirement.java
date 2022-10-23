package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class InBiomeRequirement extends AbstractRequirement {
    String biome = null;

    public InBiomeRequirement() {
    }

    public String getDescription() {
        String lang = Lang.IN_BIOME_REQUIREMENT.getConfigValue(this.biome);
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(Player player) {
        String currentBiome = player.getLocation().getBlock().getBiome().toString();
        return currentBiome + "/" + this.biome;
    }

    public boolean meetsRequirement(Player player) {
        if (this.isWorldSpecific() && !this.getWorld().equals(player.getWorld().getName())) {
            return false;
        } else {
            Location pLocation = player.getLocation();
            return pLocation.getBlock().getBiome().toString().equals(this.biome);
        }
    }

    public boolean initRequirement(String[] options) {
        if (options.length != 1) {
            return false;
        } else {
            this.biome = options[0].toUpperCase().replace(" ", "_");
            if (this.biome == null) {
                this.registerWarningMessage("No biome is provided");
                return false;
            } else {
                return true;
            }
        }
    }

    public boolean needsOnlinePlayer() {
        return true;
    }
}
