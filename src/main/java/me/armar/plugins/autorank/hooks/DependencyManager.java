package me.armar.plugins.autorank.hooks;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.quests.Quests;
import me.armar.plugins.autorank.hooks.quests.QuestsAlternative;
import me.armar.plugins.autorank.hooks.quests.QuestsPlugin;
import me.armar.plugins.autorank.hooks.statzapi.StatzAPIHandler;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.PluginLibrary;
import me.armar.plugins.utils.pluginlibrary.hooks.LibraryHook;
import me.armar.plugins.utils.pluginlibrary.hooks.QuestsHook;
import me.armar.plugins.utils.pluginlibrary.hooks.afkmanager.AFKManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;

public class DependencyManager {
    private final HashMap<DependencyManager.AutorankDependency, DependencyHandler> handlers = new HashMap();
    private final Autorank plugin;
    private PluginLibrary pluginLibrary;

    public DependencyManager(Autorank instance) {
        this.plugin = instance;
        this.handlers.put(DependencyManager.AutorankDependency.STATZ, new StatzAPIHandler(instance));
        this.loadPluginLibrary();
    }

    public DependencyHandler getDependency(DependencyManager.AutorankDependency dep) {
        if (!this.handlers.containsKey(dep)) {
            throw new IllegalArgumentException("Unknown AutorankDependency '" + dep.toString() + "'");
        } else {
            return this.handlers.get(dep);
        }
    }

    public boolean isAFK(Player player) {
        if (!this.plugin.getSettingsConfig().useAFKIntegration()) {
            return false;
        } else {
            Library[] var2 = Library.values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Library library = var2[var4];
                Optional<LibraryHook> optional = this.getLibraryHook(library);
                if (optional.isPresent()) {
                    LibraryHook libraryHook = optional.get();
                    if (libraryHook instanceof AFKManager && libraryHook.isHooked()) {
                        this.plugin.debugMessage("Using " + library.getHumanPluginName() + " for AFK");
                        return ((AFKManager)libraryHook).isAFK(player.getUniqueId());
                    }
                }
            }

            return false;
        }
    }

    public void loadDependencies() throws Exception {
        if (this.plugin.getSettingsConfig().useAdvancedDependencyLogs()) {
            this.plugin.getLogger().info("---------------[Autorank Dependencies]---------------");
            this.plugin.getLogger().info("Searching dependencies...");
        }

        Iterator var1 = this.handlers.values().iterator();

        while(var1.hasNext()) {
            DependencyHandler depHandler = (DependencyHandler)var1.next();
            depHandler.setup(this.plugin.getSettingsConfig().useAdvancedDependencyLogs());
        }

        if (this.plugin.getSettingsConfig().useAdvancedDependencyLogs()) {
            this.plugin.getLogger().info("Searching stats plugin...");
            this.plugin.getLogger().info("");
        }

        this.plugin.getStatisticsManager().loadAvailableStatsPlugins();
        if (this.plugin.getSettingsConfig().useAdvancedDependencyLogs()) {
            this.plugin.getLogger().info("---------------[Autorank Dependencies]---------------");
        }

        this.plugin.getLogger().info("Loaded libraries and dependencies");
        this.plugin.getPermPlugHandler().searchPermPlugin();
    }

    public boolean isAvailable(DependencyManager.AutorankDependency dep) {
        DependencyHandler handler = this.getDependency(dep);
        return handler != null && handler.isAvailable();
    }

    public Optional<LibraryHook> getLibraryHook(Library library) {
        if (library == null) {
            return Optional.empty();
        } else {
            return !this.isPluginLibraryLoaded() ? Optional.empty() : PluginLibrary.getLibrary(library);
        }
    }

    public boolean isAvailable(Library library) {
        if (!this.isPluginLibraryLoaded()) {
            return false;
        } else if (library == null) {
            return false;
        } else {
            Optional<LibraryHook> hook = this.getLibraryHook(library);
            return hook.filter((libraryHook) -> {
                return LibraryHook.isPluginAvailable(library) && libraryHook.isHooked();
            }).isPresent();
        }
    }

    private boolean loadPluginLibrary() {
        this.pluginLibrary = PluginLibrary.getPluginLibrary(this.plugin);
        return this.pluginLibrary.enablePluginLibrary() > 0;
    }

    public boolean isPluginLibraryLoaded() {
        return this.pluginLibrary != null;
    }

    public Optional<QuestsPlugin> getQuestsPlugin() {
        Optional questsPlugin;
        if (this.isAvailable(Library.QUESTS)) {
            questsPlugin = PluginLibrary.getLibrary(Library.QUESTS);
            if (questsPlugin.isPresent()) {
                return Optional.of(new Quests((QuestsHook)questsPlugin.get()));
            }
        } else if (this.isAvailable(Library.QUESTS_ALTERNATIVE)) {
            questsPlugin = PluginLibrary.getLibrary(Library.QUESTS_ALTERNATIVE);
            if (questsPlugin.isPresent()) {
                return Optional.of(new QuestsAlternative((me.armar.plugins.utils.pluginlibrary.hooks.QuestsAlternative)questsPlugin.get()));
            }
        }

        return Optional.empty();
    }

    public enum AutorankDependency {
        AUTORANK,
        ONTIME,
        STATS,
        STATZ;

        AutorankDependency() {
        }
    }
}

