package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TotalTimeRequirement extends AbstractRequirement {
    int totalTime = -1;

    public TotalTimeRequirement() {
    }

    public String getDescription() {
        return Lang.TOTAL_TIME_REQUIREMENT.getConfigValue(AutorankTools.timeToString(this.totalTime, TimeUnit.MINUTES));
    }

    public String getProgressString(UUID uuid) {
        long joinTime = Bukkit.getOfflinePlayer(uuid).getFirstPlayed();
        long currentTime = System.currentTimeMillis();
        long difference = (currentTime - joinTime) / 60000L;
        return difference + " min/" + this.totalTime + " min";
    }

    protected boolean meetsRequirement(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.hasPlayedBefore()) {
            this.getAutorank().debugMessage("User has not played before, so does not meet '" + this.getDescription() + "'.");
            return false;
        } else {
            long joinTime = offlinePlayer.getFirstPlayed();
            long currentTime = System.currentTimeMillis();
            long difference = (currentTime - joinTime) / 60000L;
            return difference >= (long)this.totalTime;
        }
    }

    public boolean initRequirement(String[] options) {
        if (options.length > 0) {
            this.totalTime = AutorankTools.stringToTime(options[0], TimeUnit.MINUTES);
            if (this.totalTime < 0) {
                this.registerWarningMessage("No number is provided or smaller than 0.");
                return false;
            } else {
                return true;
            }
        } else {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        long joinTime = Bukkit.getOfflinePlayer(uuid).getFirstPlayed();
        long currentTime = System.currentTimeMillis();
        long difference = (currentTime - joinTime) / 60000L;
        return (double)difference * 1.0D / (double)this.totalTime;
    }
}
