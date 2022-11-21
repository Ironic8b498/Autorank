package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class LeaderboardCommand extends AutorankCommand {
    private final Autorank plugin;

    public LeaderboardCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)){
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT.getConfigValue());
            return true;
        }
        if (!this.hasPermission("autorank.leaderboard", sender)) {
            return true;
        } else {
            boolean broadcast = false;
            boolean force = false;
            String[] var7 = args;
            int var8 = args.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                String arg = var7[var9];
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
            TimeType type = null; //final TimeType type = null;
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
                }
            } else {
                type = TimeType.DAILY_TIME;
            }

            if (type == null) {
                AutorankTools.sendDeserialize(sender, Lang.INVALID_LEADERBOARD_TYPE.getConfigValue());
                return true;
            } else if (force) {
                AutorankTools.sendDeserialize(sender, Lang.UPDATING.getConfigValue() + "<NEWLINE>" + Lang.ILL_LET.getConfigValue());
                TimeType finalType = type;
                this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                    public void run() {
                        LeaderboardCommand.this.plugin.getLeaderboardManager().updateLeaderboard(finalType);
                        AutorankTools.sendDeserialize(sender, Lang.LEADERBOARD.getConfigValue());
                        LeaderboardCommand.this.plugin.getLeaderboardManager().sendLeaderboard(sender, finalType);
                    }
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

    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return Arrays.asList("total", "daily", "weekly", "monthly");
    }
}
