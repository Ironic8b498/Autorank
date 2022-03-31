package me.armar.plugins.utils.pluginlibrary.hooks;

import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.afkmanager.AFKManager;

import java.util.UUID;

public class UltimateCoreHook extends LibraryHook implements AFKManager {
    public UltimateCoreHook() {
    }

    public boolean isHooked() {
        return isPluginAvailable(Library.ULTIMATECORE);
    }

    public boolean hook() {
        return false;
    }

    public boolean isAFK(UUID uuid) {
        return false;
    }

    public boolean hasAFKData() {
        return true;
    }
}
