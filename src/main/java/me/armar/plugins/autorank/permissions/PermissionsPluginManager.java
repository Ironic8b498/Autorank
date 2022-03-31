package me.armar.plugins.autorank.permissions;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.permissions.handlers.DummyPermissionsHandler;
import me.armar.plugins.autorank.permissions.handlers.GroupManagerHandler;
import me.armar.plugins.autorank.permissions.handlers.LuckPermsHandler;
import me.armar.plugins.autorank.permissions.handlers.VaultPermissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PermissionsPluginManager {
    private final Autorank plugin;
    private PermissionsHandler permissionPlugin;

    public PermissionsPluginManager(Autorank plugin) {
        this.plugin = plugin;
    }

    private boolean isPluginAvailable(String pluginName) {
        Plugin x = Bukkit.getServer().getPluginManager().getPlugin(pluginName);
        return x != null;
    }

    public PermissionsHandler getPermissionPlugin() {
        return this.permissionPlugin;
    }

    public boolean searchPermPlugin() {
        boolean loadedPermPlugin = false;
        if (this.isPluginAvailable("GroupManager")) {
            try {
                this.permissionPlugin = new GroupManagerHandler(this.plugin);
                this.plugin.debugMessage("Using GroupManager as permissions plugin");
                loadedPermPlugin = true;
            } catch (Throwable var6) {
                var6.printStackTrace();
            }
        } else if (this.isPluginAvailable("LuckPerms")) {
            try {
                this.permissionPlugin = new LuckPermsHandler(this.plugin);
                this.plugin.debugMessage("Using LuckPerms as permissions plugin");
                loadedPermPlugin = true;
            } catch (Throwable var4) {
                var4.printStackTrace();
            }
        } else {
            try {
                this.permissionPlugin = new VaultPermissionsHandler(this.plugin);
                this.plugin.debugMessage("Using Vault as permissions plugin");
                loadedPermPlugin = true;
            } catch (Throwable var3) {
                var3.printStackTrace();
            }
        }

        if (!loadedPermPlugin) {
            this.permissionPlugin = new DummyPermissionsHandler(this.plugin);
            this.plugin.debugMessage("Using DummyPermissions handler.");
            this.plugin.getLogger().severe("Could not find a permissions handler. Are you sure you have a compatible permissions plugin installed?");
        }

        return loadedPermPlugin;
    }
}
