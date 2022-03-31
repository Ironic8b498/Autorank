package me.armar.plugins.utils.pluginlibrary.hooks;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import me.armar.plugins.utils.pluginlibrary.Library;

import java.util.Optional;

public class TownyAdvancedHook extends LibraryHook {
    public TownyAdvancedHook() {
    }

    public boolean isHooked() {
        return isPluginAvailable(Library.TOWNY_ADVANCED);
    }

    public boolean hook() {
        return isPluginAvailable(Library.TOWNY_ADVANCED);
    }

    public Optional<Resident> getResident(String playerName) {
        if (!this.isHooked()) {
            return Optional.empty();
        } else if (!TownyAPI.getInstance().getDataSource().hasResident(playerName)) {
            return Optional.empty();
        } else {
            try {
                return Optional.ofNullable(TownyAPI.getInstance().getDataSource().getResident(playerName));
            } catch (Exception var3) {
                return Optional.empty();
            }
        }
    }

    public boolean hasTown(String playerName) {
        Resident resident = this.getResident(playerName).orElse(null);
        return resident != null && resident.hasTown();
    }

    public boolean hasNation(String playerName) {
        Resident resident = this.getResident(playerName).orElse(null);
        return resident != null && resident.hasNation();
    }

    public boolean isKing(String playerName) {
        Resident resident = this.getResident(playerName).orElse(null);
        if (resident == null) {
            return false;
        } else {
            return resident.hasNation() && resident.isKing();
        }
    }

    public boolean isMayor(String playerName) {
        Resident resident = this.getResident(playerName).orElse(null);
        if (resident == null) {
            return false;
        } else {
            return resident.hasTown() && resident.isMayor();
        }
    }

    public boolean isJailed(String playerName) {
        Resident resident = this.getResident(playerName).orElse(null);
        return resident != null && resident.isJailed();
    }

    public int getNumberOfTownBlocks(String playerName) {
        Resident resident = this.getResident(playerName).orElse(null);
        if (resident == null) {
            return 0;
        } else {
            return !resident.hasTown() ? 0 : resident.getTownBlocks().size();
        }
    }
}
