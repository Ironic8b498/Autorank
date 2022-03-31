package me.armar.plugins.utils.pluginlibrary.hooks;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.afkmanager.AFKManager;

import java.util.UUID;

public class EssentialsXHook extends LibraryHook implements AFKManager {
    private Essentials essentials;

    public EssentialsXHook() {
    }

    public boolean isHooked() {
        return this.essentials != null;
    }

    public boolean hook() {
        if (!isPluginAvailable(Library.ESSENTIALSX)) {
            return false;
        } else {
            this.essentials = (Essentials)this.getServer().getPluginManager().getPlugin(Library.ESSENTIALSX.getInternalPluginName());
            return this.essentials != null;
        }
    }

    public boolean isJailed(UUID uuid) {
        if (!this.isHooked()) {
            return false;
        } else {
            User user = this.essentials.getUser(uuid);
            return user != null && user.isJailed();
        }
    }

    public String getGeoIPLocation(UUID uuid) {
        if (!this.isHooked()) {
            return null;
        } else {
            User user = this.essentials.getUser(uuid);
            return user == null ? null : user.getGeoLocation();
        }
    }

    public boolean isAFK(UUID uuid) {
        if (!this.isHooked()) {
            return false;
        } else {
            User user = this.essentials.getUser(uuid);
            return user != null && user.isAfk();
        }
    }

    public boolean hasAFKData() {
        return true;
    }
}
