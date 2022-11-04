package me.armar.plugins.utils.pluginlibrary.hooks;

import me.armar.plugins.utils.pluginlibrary.Library;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Location;

import java.util.UUID;

public class GriefPreventionHook extends LibraryHook {
    private GriefPrevention griefPrevention;

    public GriefPreventionHook() {
    }

    public boolean isHooked() {
        return this.griefPrevention != null;
    }

    public boolean hook() {
        if (!isPluginAvailable(Library.GRIEFPREVENTION)) {
            return false;
        } else {
            this.griefPrevention = (GriefPrevention)this.getServer().getPluginManager().getPlugin(Library.GRIEFPREVENTION.getInternalPluginName());
            return this.griefPrevention != null;
        }
    }

    private PlayerData getPlayerData(UUID uuid) {
        return this.griefPrevention.dataStore.getPlayerData(uuid);
    }

    public int getNumberOfClaims(UUID uuid) {
        if (!this.isHooked()) {
            return -1;
        } else {
            PlayerData data = this.getPlayerData(uuid);
            return data.getClaims().size();
        }
    }

    public int getNumberOfClaimedBlocks(UUID uuid) {
        if (!this.isHooked()) {
            return -1;
        } else {
            PlayerData data = this.getPlayerData(uuid);
            data.getAccruedClaimBlocksLimit();
            return data.getAccruedClaimBlocks();
        }
    }

    public int getNumberOfRemainingBlocks(UUID uuid) {
        if (!this.isHooked()) {
            return -1;
        } else {
            PlayerData data = this.getPlayerData(uuid);
            return data.getRemainingClaimBlocks();
        }
    }

    public int getNumberOfBonusBlocks(UUID uuid) {
        if (!this.isHooked()) {
            return -1;
        } else {
            PlayerData data = this.getPlayerData(uuid);
            return data.getBonusClaimBlocks();
        }
    }

    public boolean isInRegion(Location loc, UUID uuid) {
        if (!this.isHooked()) {
            return false;
        } else {
            PlayerData data = this.getPlayerData(uuid);
            Claim claim = this.griefPrevention.dataStore.getClaimAt(loc, false, data.lastClaim);
            return claim != null;
        }
    }
}
