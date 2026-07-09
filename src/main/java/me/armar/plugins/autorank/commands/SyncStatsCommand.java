package me.armar.plugins.autorank.commands;

import java.util.UUID;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SyncStatsCommand extends AutorankCommand {
    private final Autorank plugin;

    public SyncStatsCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT.getConfigValue());
            return true;
        } else {
            if (this.hasPermission(this.getPermission(), sender)) {
                int count = 0;

                for(UUID uuid : this.plugin.getPlayTimeStorageManager().getPrimaryStorageProvider().getStoredPlayers(TimeType.TOTAL_TIME)) {
                    this.plugin.getServer().getOfflinePlayer(uuid);
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
