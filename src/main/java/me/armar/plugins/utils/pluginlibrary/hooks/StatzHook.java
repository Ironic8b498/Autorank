package me.armar.plugins.utils.pluginlibrary.hooks;

import me.armar.plugins.utils.pluginlibrary.Library;
import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.RowRequirement;
import me.staartvin.statz.datamanager.player.PlayerStat;
import org.bukkit.Statistic;

import java.util.UUID;

public class StatzHook extends LibraryHook {
    private Statz statz;

    public StatzHook() {
    }

    public boolean isHooked() {
        return this.statz != null;
    }

    public boolean hook() {
        if (!isPluginAvailable(Library.STATZ)) {
            return false;
        } else {
            this.statz = (Statz)this.getServer().getPluginManager().getPlugin(Library.STATZ.getInternalPluginName());
            return this.statz != null;
        }
    }

    public int getMinecraftStatistic(UUID uuid, Statistic statistic) {
        return !this.isHooked() ? -1 : this.statz.getStatzAPI().getMinecraftStatistic(uuid, statistic);
    }

    public Double getTotalStatistics(PlayerStat statType, UUID uuid, String worldName) {
        return !this.isHooked() ? -1.0D : this.statz.getStatzAPI().getTotalOf(statType, uuid, worldName);
    }

    public Double getSpecificStatistics(PlayerStat statType, UUID uuid, RowRequirement... requirements) {
        return !this.isHooked() ? -1.0D : this.statz.getStatzAPI().getSpecificData(statType, uuid, requirements);
    }
}
