package me.armar.plugins.utils.pluginlibrary.hooks;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.armar.plugins.utils.pluginlibrary.Library;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Iterator;

public class WorldGuardHook extends LibraryHook {
    private WorldGuardPlugin worldGuard;

    public WorldGuardHook() {
    }

    public boolean isHooked() {
        return this.worldGuard != null;
    }

    public boolean hook() {
        if (!isPluginAvailable(Library.WORLDGUARD)) {
            return false;
        } else {
            this.worldGuard = (WorldGuardPlugin)this.getServer().getPluginManager().getPlugin(Library.WORLDGUARD.getInternalPluginName());
            return this.worldGuard != null;
        }
    }

    public boolean isInRegion(Player player, String regionName) {
        if (!this.isHooked()) {
            return false;
        } else {
            return player != null && regionName != null && this.isInRegion(player.getLocation(), regionName);
        }
    }

    public boolean isInRegion(Location location, String regionName) {
        if (!this.isHooked()) {
            return false;
        } else if (location == null) {
            return false;
        } else {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            Iterator var4 = container.getLoaded().iterator();

            RegionManager regionManager;
            do {
                if (!var4.hasNext()) {
                    return false;
                }

                regionManager = (RegionManager)var4.next();
            } while(!regionManager.hasRegion(regionName));

            ProtectedRegion region = regionManager.getRegion(regionName);
            if (region == null) {
                return false;
            } else {
                return region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            }
        }
    }
}
