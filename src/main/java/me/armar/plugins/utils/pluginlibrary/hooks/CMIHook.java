package me.armar.plugins.utils.pluginlibrary.hooks;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.afkmanager.AFKManager;

import java.util.UUID;

public class CMIHook extends LibraryHook implements AFKManager {
    public CMIHook() {
    }

    public boolean isHooked() {
        return isPluginAvailable(Library.CMI);
    }

    public boolean hook() {
        return isPluginAvailable(Library.CMI);
    }

    public boolean isAFK(UUID uuid) {
        if (!this.isHooked()) {
            return false;
        } else {
            CMIUser user = CMI.getInstance().getPlayerManager().getUser(uuid);
            return user != null && user.isAfk();
        }
    }

    public boolean hasAFKData() {
        return true;
    }
}
