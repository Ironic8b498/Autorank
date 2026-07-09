package me.armar.plugins.autorank.commands;

import java.util.Arrays;
import java.util.List;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LeaderboardCommand extends AutorankCommand {
    private final Autorank plugin;

    public LeaderboardCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT.getConfigValue());
            return true;
        } else if (!this.hasPermission(this.getPermission(), sender)) {
            return true;
        } else {
            boolean broadcast = false;
            boolean force = false;

            for(String arg : args) {
                if (arg.equalsIgnoreCase("force")) {
                    if (!this.hasPermission("autorank.leaderboard.force", sender)) {
                        return true;
                    }

                    force = true;
                } else if (arg.equalsIgnoreCase("broadcast")) {
                    if (!this.hasPermission("autorank.leaderboard.broadcast", sender)) {
                        return true;
                    }

                    broadcast = true;
                }
            }

            String leaderboardType = "total";
            TimeType type;
            if (args.length > 1 && !args[1].equalsIgnoreCase("force") && !args[1].equalsIgnoreCase("broadcast")) {
                leaderboardType = args[1].toLowerCase();
            }

            if (leaderboardType.equalsIgnoreCase("total")) {
                type = TimeType.TOTAL_TIME;
            } else if (!leaderboardType.equalsIgnoreCase("daily") && !leaderboardType.contains("day")) {
                if (leaderboardType.contains("week")) {
                    type = TimeType.WEEKLY_TIME;
                } else if (leaderboardType.contains("month")) {
                    type = TimeType.MONTHLY_TIME;
                } else {
                    type = null;
                }
            } else {
                type = TimeType.DAILY_TIME;
            }

            if (type == null) {
                AutorankTools.sendDeserialize(sender, Lang.INVALID_LEADERBOARD_TYPE.getConfigValue());
                return true;
            } else if (force) {
                String var10001 = Lang.UPDATING.getConfigValue();
                AutorankTools.sendDeserialize(sender, var10001 + "<NEWLINE>" + Lang.ILL_LET.getConfigValue());
                this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                    this.plugin.getLeaderboardManager().updateLeaderboard(type);
                    AutorankTools.sendDeserialize(sender, Lang.LEADERBOARD.getConfigValue());
                    this.plugin.getLeaderboardManager().sendLeaderboard(sender, type);
                });
                return true;
            } else {
                if (!broadcast) {
                    this.plugin.getLeaderboardManager().sendLeaderboard(sender, type);
                } else {
                    this.plugin.getLeaderboardManager().broadcastLeaderboard(type);
                }

                return true;
            }
        }
    }

    public String getDescription() {
        return "Show the leaderboard.";
    }

    public String getPermission() {
        return "autorank.leaderboard";
    }

    public String getUsage() {
        return "/ar leaderboard <type>";
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        return Arrays.asList("total", "daily", "weekly", "monthly");
    }
}
