package me.armar.plugins.autorank.hooks;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public abstract class DependencyHandler {
    public DependencyHandler() {
    }

    public abstract Plugin get();

    public abstract boolean isAvailable();

    public abstract boolean isInstalled();

    public abstract boolean setup(boolean var1) throws Exception;

    public Autorank getPlugin() {
        return (Autorank)Bukkit.getPluginManager().getPlugin("Autorank");
    }
}
