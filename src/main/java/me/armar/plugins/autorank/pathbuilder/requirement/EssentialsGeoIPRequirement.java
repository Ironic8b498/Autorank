package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.EssentialsXHook;

import java.util.UUID;

public class EssentialsGeoIPRequirement extends AbstractRequirement {
    private EssentialsXHook essHandler = null;
    String location = null;

    public EssentialsGeoIPRequirement() {
    }

    public String getDescription() {
        return Lang.ESSENTIALS_GEOIP_LOCATION_REQUIREMENT.getConfigValue(this.location);
    }

    public String getProgressString(UUID uuid) {
        return this.essHandler.getGeoIPLocation(uuid) + "/" + this.location;
    }

    protected boolean meetsRequirement(UUID uuid) {
        String realLocation = this.essHandler.getGeoIPLocation(uuid);
        if (realLocation == null) {
            return false;
        } else {
            return this.location != null && this.location.equalsIgnoreCase(realLocation);
        }
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.ESSENTIALSX);
        this.essHandler = (EssentialsXHook)this.getDependencyManager().getLibraryHook(Library.ESSENTIALSX).orElse(null);
        if (options.length != 1) {
            return false;
        } else {
            this.location = options[0];
            if (this.location == null) {
                this.registerWarningMessage("No location is provided");
                return false;
            } else if (this.essHandler != null && this.essHandler.isHooked()) {
                return true;
            } else {
                this.registerWarningMessage("EssentialsX is not available");
                return false;
            }
        }
    }
}
