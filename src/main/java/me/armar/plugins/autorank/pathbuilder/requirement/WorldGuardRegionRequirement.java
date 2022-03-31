package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.WorldGuardHook;
import org.bukkit.entity.Player;

public class WorldGuardRegionRequirement extends AbstractRequirement {
    private WorldGuardHook handler;
    String regionName = null;

    public WorldGuardRegionRequirement() {
    }

    public String getDescription() {
        String lang = Lang.WORLD_GUARD_REGION_REQUIREMENT.getConfigValue(this.regionName);
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(Player player) {
        return "Cannot show progress";
    }

    public boolean meetsRequirement(Player player) {
        return (!this.isWorldSpecific() || this.getWorld().equals(player.getWorld().getName())) && this.handler.isInRegion(player, this.regionName);
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.WORLDGUARD);
        this.handler = (WorldGuardHook)this.getAutorank().getDependencyManager().getLibraryHook(Library.WORLDGUARD).orElse(null);
        if (options.length > 0) {
            this.regionName = options[0].trim();
        }

        if (this.regionName == null) {
            this.registerWarningMessage("Region is not specified");
            return false;
        } else if (this.handler != null && this.handler.isHooked()) {
            return true;
        } else {
            this.registerWarningMessage("WorldGuard is not available");
            return false;
        }
    }

    public boolean needsOnlinePlayer() {
        return true;
    }
}
