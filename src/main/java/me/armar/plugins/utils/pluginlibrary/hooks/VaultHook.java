package me.armar.plugins.utils.pluginlibrary.hooks;

import me.armar.plugins.utils.pluginlibrary.Library;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook extends LibraryHook {
    private static Economy econ = null;
    private static Permission perms = null;

    public VaultHook() {
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public boolean isHooked() {
        return isPluginAvailable(Library.VAULT);
    }

    public boolean hook() {
        if (!isPluginAvailable(Library.VAULT)) {
            return false;
        } else {
            boolean setupEco = this.setupEconomy();
            boolean setupChat = this.setupChat();
            boolean setupPerm = this.setupPermissions();
            return setupEco && setupChat && setupPerm;
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        assert rsp != null;
        return true;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        assert rsp != null;
        perms = rsp.getProvider();
        return true;
    }
}
