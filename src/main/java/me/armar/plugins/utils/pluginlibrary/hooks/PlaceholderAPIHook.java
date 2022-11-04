package me.armar.plugins.utils.pluginlibrary.hooks;

import me.armar.plugins.utils.pluginlibrary.Library;
import me.clip.placeholderapi.PlaceholderAPI;

public class PlaceholderAPIHook extends LibraryHook {
    private PlaceholderAPI placeholderAPI;

    public PlaceholderAPIHook() {

    }

    public boolean isHooked() {
        return isPluginAvailable(Library.PLACEHOLDERAPI);
    }

    public boolean hook() {
        return isPluginAvailable(Library.PLACEHOLDERAPI);
    }

}
