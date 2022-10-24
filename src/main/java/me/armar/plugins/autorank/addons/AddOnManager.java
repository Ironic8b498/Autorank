package me.armar.plugins.autorank.addons;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class AddOnManager {
    private final HashMap<String, JavaPlugin> loadedAddons = new HashMap();
    private final Autorank plugin;

    public AddOnManager(Autorank instance) {
        this.plugin = instance;
    }

    public JavaPlugin getLoadedAddon(String addonName) {
        return !this.isAddonLoaded(addonName) ? null : this.loadedAddons.get(addonName);
    }

    public Set<String> getLoadedAddons() {
        return this.loadedAddons.keySet();
    }

    public boolean isAddonLoaded(String addonName) {
        return this.loadedAddons.containsKey(addonName);
    }

    public void loadAddon(String addonName, JavaPlugin addon) {
        if (!this.isAddonLoaded(addonName)) {
            this.loadedAddons.put(addonName, addon);
            this.plugin.getLogger().info("Loaded addon " + addonName);
        }
    }

    public void unloadAddon(String addonName) {
        if (this.isAddonLoaded(addonName)) {
            this.loadedAddons.remove(addonName);
            this.plugin.getLogger().info("Unloaded addon " + addonName);
        }
    }

    public void unloadAllAddons() {
        Iterator var1 = this.loadedAddons.keySet().iterator();

        while(var1.hasNext()) {
            String addon = (String)var1.next();
            this.unloadAddon(addon);
        }

    }
}
