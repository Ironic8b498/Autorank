package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Iterator;
import java.util.UUID;

public class SyncStatsCommand extends AutorankCommand {
    private final Autorank plugin;

    public SyncStatsCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!this.hasPermission("autorank.syncstats", sender)) {
            return true;
        } else {
            int count = 0;
            Iterator var6 = this.plugin.getPlayTimeStorageManager().getPrimaryStorageProvider().getStoredPlayers(TimeType.TOTAL_TIME).iterator();

            while(var6.hasNext()) {
                UUID uuid = (UUID)var6.next();
                OfflinePlayer p = this.plugin.getServer().getOfflinePlayer(uuid);
                int statsPlayTime = this.plugin.getStatisticsManager().getTimePlayed(uuid, null);
                if (statsPlayTime > 0) {
                    this.plugin.getPlayTimeStorageManager().setPlayerTime(TimeType.TOTAL_TIME, uuid, statsPlayTime);
                    ++count;
                }
            }

            if (count == 0) {
                AutorankTools.sendDeserialize(sender, Lang.COULD_NOT_SYNC.getConfigValue());
            } else {
                AutorankTools.sendDeserialize(sender, Lang.TIME_HAS.getConfigValue());
            }
            return true;
        }
    }

    public String getDescription() {
        return "Sync Autorank's time to Stats' time.";
    }

    public String getPermission() {
        return "autorank.syncstats";
    }

    public String getUsage() {
        return "/ar syncstats";
    }
}
