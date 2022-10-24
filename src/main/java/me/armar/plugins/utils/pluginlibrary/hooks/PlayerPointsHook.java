package me.armar.plugins.utils.pluginlibrary.hooks;

import me.armar.plugins.utils.pluginlibrary.Library;
import org.black_ixx.playerpoints.PlayerPoints;

import java.util.UUID;

public class PlayerPointsHook extends LibraryHook {
    private PlayerPoints api;

    public PlayerPointsHook() {
    }

    public boolean isHooked() {
        return this.api != null;
    }

    public boolean hook() {
        if (!isPluginAvailable(Library.PLAYERPOINTS)) {
            return false;
        } else {
            this.api = (PlayerPoints)this.getServer().getPluginManager().getPlugin(Library.PLAYERPOINTS.getInternalPluginName());
            return this.api != null;
        }
    }

    public int getPlayerPoints(UUID uuid) {
        return !this.isHooked() ? -1 : this.api.getAPI().look(uuid);
    }

    public boolean setPlayerPoints(UUID uuid, int value) {
        return this.isHooked() && this.api.getAPI().set(uuid, value);
    }

    public boolean givePlayerPoints(UUID uuid, int value) {
        return this.api.getAPI().give(uuid, value);
    }

    public boolean takePlayerPoints(UUID uuid, int value) {
        return this.api.getAPI().take(uuid, value);
    }
}
