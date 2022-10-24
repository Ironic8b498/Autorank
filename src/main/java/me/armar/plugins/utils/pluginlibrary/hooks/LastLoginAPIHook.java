package me.armar.plugins.utils.pluginlibrary.hooks;

import com.alessiodp.lastloginapi.api.LastLogin;
import com.alessiodp.lastloginapi.api.interfaces.LastLoginAPI;
import com.alessiodp.lastloginapi.api.interfaces.LastLoginPlayer;
import me.armar.plugins.utils.pluginlibrary.Library;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class LastLoginAPIHook extends LibraryHook {
    private LastLoginAPI lastLonginAPI;

    public LastLoginAPIHook() {
    }

    public boolean isHooked() {
        return this.lastLonginAPI != null;
    }

    public boolean hook() {
        if (!isPluginAvailable(Library.LASTLOGINAPI)) {
            return false;
        }
        if (getServer().getPluginManager().getPlugin("LastLoginAPI") != null) {
            if (getServer().getPluginManager().getPlugin("LastLoginAPI").isEnabled()) {
                this.lastLonginAPI = LastLogin.getApi();
                return this.lastLonginAPI != null;
            }
        }
        return false;
    }


    public long getlastLogin(UUID uuid) {
        LastLoginAPI api = LastLogin.getApi();
        LastLoginPlayer player = api.getPlayer(uuid); // Get the player
        player.getName(); // Get the current name
        return player.getLastLogin(); // Get the last login timestamp
    }

    public long getlastLogout(UUID uuid) {
        LastLoginAPI api = LastLogin.getApi();
        LastLoginPlayer player = api.getPlayer(uuid); // Get the player
        player.getName(); // Get the current name
        return player.getLastLogout(); // Get the last logout timestamp
    }

    public static LocalDateTime localDateFromTimestamp(Timestamp timestamp) {
        return LocalDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.ofHours(0));
    }
}