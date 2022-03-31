package me.armar.plugins.utils.pluginlibrary.hooks;

import com.vexsoftware.votifier.NuVotifierBukkit;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.listeners.VoteListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class NuVotifierHook extends LibraryHook {
    private NuVotifierBukkit api;

    public NuVotifierHook() {
    }

    public boolean isHooked() {
        return this.api != null;
    }

    public boolean hook() {
        if (!isPluginAvailable(Library.NUVOTIFIER)) {
            return false;
        } else {
            Plugin plugin = this.getServer().getPluginManager().getPlugin(Library.NUVOTIFIER.getInternalPluginName());

            try {
                if (plugin == null || !(plugin instanceof NuVotifierBukkit)) {
                    return false;
                }
            } catch (NoClassDefFoundError var3) {
                return false;
            }

            this.api = (NuVotifierBukkit)plugin;
            this.setupVoteListener();
            return true;
        }
    }

    private boolean setupVoteListener() {
        Bukkit.getPluginManager().registerEvents(new VoteListener(), this.getProvidedJavaPlugin());
        return true;
    }
}
