package me.armar.plugins.autorank.permissions;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.entity.Player;

import java.util.Collection;

public abstract class PermissionsHandler {
    private final Autorank plugin;

    public PermissionsHandler(Autorank plugin) {
        this.plugin = plugin;
        if (!this.setupPermissionsHandler()) {
            plugin.getServer().getLogger().severe("Could not connect to permissions plugin. Is it up to date?");
        }

    }

    public abstract Collection<String> getGroups();

    public abstract String getName();

    public abstract Collection<String> getPlayerGroups(Player var1);

    public abstract Collection<String> getWorldGroups(Player var1, String var2);

    public abstract boolean replaceGroup(Player var1, String var2, String var3, String var4);

    public abstract boolean setupPermissionsHandler();

    public Autorank getPlugin() {
        return this.plugin;
    }
}
