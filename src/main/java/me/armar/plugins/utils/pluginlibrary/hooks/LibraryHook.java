package me.armar.plugins.utils.pluginlibrary.hooks;

import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.PluginLibrary;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

public abstract class LibraryHook {
    public LibraryHook() {
    }

    public static boolean isPluginAvailable(Library library) {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(library.getInternalPluginName());
        if (plugin == null) {
            return false;
        } else {
            return !library.hasMainClass() || plugin.getDescription().getMain().equalsIgnoreCase(library.getMainClass());
        }
    }

    protected PluginLibrary getPlugin() {
        return Bukkit.getServer().getServicesManager().load(PluginLibrary.class);
    }

    protected Plugin getProvidedJavaPlugin() {
        return Bukkit.getServer().getServicesManager().getRegistration(PluginLibrary.class).getPlugin();
    }

    protected Server getServer() {
        return Bukkit.getServer();
    }

    public abstract boolean isHooked();

    public abstract boolean hook();
}
