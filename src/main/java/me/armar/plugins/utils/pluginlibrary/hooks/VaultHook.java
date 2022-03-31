package me.armar.plugins.utils.pluginlibrary.hooks;

import me.armar.plugins.utils.pluginlibrary.Library;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook extends LibraryHook {
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;

    public VaultHook() {
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public static Chat getChat() {
        return chat;
    }

    public boolean isHooked() {
        return perms != null;
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
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        } else {
            RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                return false;
            } else {
                econ = rsp.getProvider();
                return econ != null;
            }
        }
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = this.getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp == null) {
            return false;
        } else {
            chat = rsp.getProvider();
            return chat != null;
        }
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = this.getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            return false;
        } else {
            perms = rsp.getProvider();
            return perms != null;
        }
    }
}
