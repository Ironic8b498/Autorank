package me.armar.plugins.utils.pluginlibrary.hooks;

import me.armar.plugins.utils.pluginlibrary.Library;
import net.prosavage.factionsx.FactionsX;
import net.prosavage.factionsx.core.FPlayer;
import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.manager.FactionManager;
import net.prosavage.factionsx.manager.PlayerManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FactionXHook extends LibraryHook {
    private FactionsX factionsX;

    public FactionXHook() {
    }

    public boolean isHooked() {
        return this.factionsX != null;
    }

    public boolean hook() {
        if (!isPluginAvailable(Library.FACTIONSX)) {
            return false;
        } else {
            this.factionsX = (FactionsX) this.getServer().getPluginManager().getPlugin(Library.FACTIONSX.getInternalPluginName());
            return this.factionsX != null;
        }
    }

    public Faction getFactionByName(String factionName) {
        if (!this.isHooked()) {
            return null;
        } else if (factionName == null) {
            return null;
        } else {
            String factionTag = "CoolFaction";
            Faction fac = FactionManager.INSTANCE.getFaction(factionTag);
            return fac;
        }
    }

    public Faction getFactionByUUID(UUID uuid) {
        if (!this.isHooked()) {
            return null;
        } else if (uuid == null) {
            return null;
        } else {
            FPlayer fPlayer = this.getFactionsPlayer(uuid);
            if (fPlayer == null) {
                return null;
            } else {
                Faction fac = fPlayer.getFaction();
                return fac;
            }
        }
    }

    public Faction getFactionById(String factionId) {
        long factionID = 1;
        if (!this.isHooked()) {
            return null;
        } else if (factionId == null) {
            return null;
        } else {
            Faction faction = FactionManager.INSTANCE.getFaction(factionID);
            return faction;
        }
    }

    public List<Faction> getAllFactions() {
        List<Faction> factions = new ArrayList();
        if (!this.isHooked()) {
            return factions;
        } else {
            factions = (List<Faction>) FactionManager.INSTANCE.getFactions();
            return factions;
        }
    }

    public Faction getWilderness() {
        if (!this.isHooked()) {
            return null;
        } else {
            Faction fac = FactionManager.INSTANCE.getWilderness();
            return fac;
        }
    }

    public Faction getSafezone() {
        if (!this.isHooked()) {
            return null;
        } else {
            Faction fac = FactionManager.INSTANCE.getSafezone();
            return fac;
        }
    }

    public Faction getWarzone() {
        if (!this.isHooked()) {
            return null;
        } else {
            Faction fac = FactionManager.INSTANCE.getWarzone();
            return fac;
        }
    }

/*    public Faction getFactionAt(Location location) {
        if (!this.isHooked()) {
            return null;
        } else if (location == null) {
            return null;
        } else {
            FLocation fLocation = new FLocation(location);
            Faction fac = Board.getInstance().getFactionAt(fLocation);
            return fac;
        }
    }*/

    public FPlayer getFactionsPlayer(UUID uuid) {
        if (!this.isHooked()) {
            return null;
        } else {
            OfflinePlayer offlinePlayer = this.getServer().getOfflinePlayer(uuid);
            FPlayer fPlayer = PlayerManager.INSTANCE.getFPlayer((Player)offlinePlayer);//fPlayer = FPlayers.getInstance().getByOfflinePlayer(offlinePlayer);
            return fPlayer;
        }
    }

    public double getFactionPower(UUID uuid) {
        if (!this.isHooked()) {
            return -1.0D;
        } else if (uuid == null) {
            return -1.1D;
        } else {
            FPlayer fPlayer = this.getFactionsPlayer(uuid);
            if (fPlayer == null) {
                return -1.2D;
            } else {
                return !fPlayer.hasFaction() ? -1.3D : fPlayer.getFaction().getPower();
            }
        }
    }
}
