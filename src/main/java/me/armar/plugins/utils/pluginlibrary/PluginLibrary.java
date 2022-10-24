package me.armar.plugins.utils.pluginlibrary;

import me.armar.plugins.utils.pluginlibrary.hooks.LibraryHook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class PluginLibrary {
    private static final List<Library> loadedLibraries = new ArrayList();
    public HashMap<UUID, Long> requestTimes = new HashMap();

    public PluginLibrary() {
    }

    public static Optional<LibraryHook> getLibrary(String pluginName) throws IllegalArgumentException {
        return Library.getEnum(pluginName).getHook();
    }

    public static Optional<LibraryHook> getLibrary(Library lib) {
        return lib.getHook();
    }

    public static boolean isLibraryLoaded(Library lib) {
        return loadedLibraries.contains(lib);
    }

    public static PluginLibrary getPluginLibrary(JavaPlugin plugin) {
        boolean loadNewInstance = false;
        PluginLibrary library = null;
        if (Bukkit.getServer().getServicesManager().isProvidedFor(PluginLibrary.class)) {
            library = Bukkit.getServer().getServicesManager().load(PluginLibrary.class);
            if (library == null) {
                loadNewInstance = true;
            } else {
                plugin.getLogger().info("Found PluginLibrary instance and using that");
            }
        } else {
            loadNewInstance = true;
        }

        if (loadNewInstance) {
            library = new PluginLibrary();
            plugin.getLogger().info("Generating new PluginLibrary instance");
            Bukkit.getServer().getServicesManager().register(PluginLibrary.class, library, plugin, ServicePriority.Normal);
        }

        return library;
    }

    public int enablePluginLibrary() {
        PluginLibrary.loadedLibraries.clear();
        this.logMessage(ChatColor.GOLD + "***== Loading libraries ==***");
        int loadedLibraries = this.loadLibraries();
        this.logMessage(ChatColor.GOLD + "***== Loaded " + ChatColor.WHITE + loadedLibraries + ChatColor.GOLD + " libraries! ==***");
        if (loadedLibraries > 0) {
            this.logMessage(ChatColor.GOLD + "Loaded libraries: " + this.getLoadedLibrariesAsString());
        }

        this.logMessage(ChatColor.GREEN + "*** Ready for plugins to send/retrieve data. ***");
        return loadedLibraries;
    }

    public void disablePluginLibrary() {
        loadedLibraries.clear();
        this.logMessage("Unloaded all hooked libraries!");
    }

    public int loadLibraries() {
        int count = 0;
        Library[] var2 = Library.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Library l = var2[var4];
            if (LibraryHook.isPluginAvailable(l)) {
                try {
                    Optional<LibraryHook> libraryHook = l.getHook();
                    if (libraryHook.isPresent() && libraryHook.get().hook()) {
                        loadedLibraries.add(l);
                        ++count;
                    } else {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Obtained error when loading " + l.getHumanPluginName());
                    }
                } catch (NoClassDefFoundError var7) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Obtained error when loading " + l.getHumanPluginName());
                    var7.printStackTrace();
                }
            }
        }

        return count;
    }

    public void logMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[PluginLibrary] " + message);
    }

    public List<Library> getLoadedLibraries() {
        return Collections.unmodifiableList(loadedLibraries);
    }

    private String getLoadedLibrariesAsString() {
        StringBuilder builder = new StringBuilder();
        int i = 0;

        for(int l = loadedLibraries.size(); i < l; ++i) {
            Library library = loadedLibraries.get(i);
            String addedString = ChatColor.DARK_AQUA + library.getHumanPluginName() + ChatColor.DARK_GREEN + " (by " + library.getAuthor() + ")" + ChatColor.RESET;
            if (i == 0) {
                builder.append(addedString);
            } else if (i == l - 1) {
                builder.append(ChatColor.GRAY).append(" and ").append(addedString);
            } else {
                builder.append(ChatColor.GRAY).append(", ").append(addedString);
            }
        }

        return builder.toString();
    }
}
