package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class InfoCommand extends AutorankCommand {
    private final Autorank plugin;

    public InfoCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        } else {
            if (args[1].equalsIgnoreCase(sender.getName())) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Lang.CANNOT_CHECK_CONSOLE.getDefault());
                    return true;
                }

                if (!this.hasPermission("autorank.info.self", sender)) {
                    return true;
                }
            } else if (!this.hasPermission("autorank.info.others", sender)) {
                return true;
            }

            CompletableFuture<Void> task = UUIDManager.getUUID(args[1]).thenAccept((uuid) -> {
                if (uuid == null) {
                    sender.sendMessage(Lang.PLAYER_IS_INVALID.getConfigValue(args[1]));
                } else {
                    String playerName = null;

                    try {
                        playerName = UUIDManager.getPlayerName(uuid).get();
                    } catch (ExecutionException | InterruptedException var18) {
                        var18.printStackTrace();
                    }

                    sender.sendMessage(ChatColor.DARK_AQUA + "---------- [" + ChatColor.GOLD + playerName + ChatColor.DARK_AQUA + "] ----------");
                    List<Path> activePaths = this.plugin.getPathManager().getActivePaths(uuid);
                    List<Path> completedPaths = this.plugin.getPathManager().getCompletedPaths(uuid);
                    boolean isExemptedFromLeaderboard = this.plugin.getPlayerChecker().isExemptedFromLeaderboard(uuid);
                    boolean isExemptedFromAutomaticChecking = this.plugin.getPlayerChecker().isExemptedFromAutomaticChecking(uuid);
                    boolean isExemptedFromTimeAddition = this.plugin.getPlayerChecker().isExemptedFromTimeAddition(uuid);
                    int localTotalTime = 0;
                    int globalTotalTime = 0;

                    try {
                        localTotalTime = this.plugin.getPlayTimeManager().getLocalPlayTime(TimeType.TOTAL_TIME, uuid).get();
                        globalTotalTime = this.plugin.getPlayTimeManager().getGlobalPlayTime(TimeType.TOTAL_TIME, uuid).get();
                    } catch (ExecutionException | InterruptedException var17) {
                        var17.printStackTrace();
                    }

                    StringBuilder activePathsString = new StringBuilder();

                    for(int i = 0; i < activePaths.size(); ++i) {
                        long pathProgress = Math.round(activePaths.get(i).getProgress(uuid) * 100.0D);
                        String progressStringx = null;
                        if (pathProgress <= 35L) {
                            progressStringx = ChatColor.RED + "" + pathProgress;
                        } else if (pathProgress <= 70L) {
                            progressStringx = ChatColor.YELLOW + "" + pathProgress;
                        } else {
                            progressStringx = ChatColor.GREEN + "" + pathProgress;
                        }

                        if (i == activePaths.size() - 1) {
                            activePathsString.append(ChatColor.DARK_AQUA).append(activePaths.get(i).toString()).append(" (").append(progressStringx).append(ChatColor.DARK_AQUA).append("%)");
                        } else if (i == activePaths.size() - 2) {
                            activePathsString.append(ChatColor.DARK_AQUA).append(activePaths.get(i).toString()).append(" (").append(progressStringx).append(ChatColor.DARK_AQUA).append("%)").append(" and ");
                        } else {
                            activePathsString.append(ChatColor.DARK_AQUA).append(activePaths.get(i).toString()).append(" (").append(progressStringx).append(ChatColor.DARK_AQUA).append("%), ");
                        }
                    }

                    StringBuilder completedPathsString = new StringBuilder();

                    for(int ix = 0; ix < completedPaths.size(); ++ix) {
                        String progressString = ChatColor.YELLOW + "" + completedPaths.get(ix).getTimesCompleted(uuid) + "x";
                        if (ix == completedPaths.size() - 1) {
                            completedPathsString.append(ChatColor.DARK_AQUA).append(completedPaths.get(ix).toString()).append(" (").append(progressString).append(ChatColor.DARK_AQUA).append(")");
                        } else if (ix == completedPaths.size() - 2) {
                            completedPathsString.append(ChatColor.DARK_AQUA).append(completedPaths.get(ix).toString()).append(" (").append(progressString).append(ChatColor.DARK_AQUA).append(")").append(" and ");
                        } else {
                            completedPathsString.append(ChatColor.DARK_AQUA).append(completedPaths.get(ix).toString()).append(" (").append(progressString).append(ChatColor.DARK_AQUA).append("), ");
                        }
                    }

                    sender.sendMessage(ChatColor.LIGHT_PURPLE + Lang.ACTIVE.getConfigValue() + " (" + ChatColor.GOLD + activePaths.size() + ChatColor.LIGHT_PURPLE + "): " + (activePathsString.length() == 0 ? Lang.NONE.getConfigValue() : activePathsString.toString()));
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + Lang.COMPLETED.getConfigValue() + " (" + ChatColor.GOLD + completedPaths.size() + ChatColor.LIGHT_PURPLE + "): " + (completedPathsString.length() == 0 ? Lang.NONE.getConfigValue() : completedPathsString.toString()));
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + Lang.IS_EXEMPTED_LEADERBOARD.getConfigValue() + ": " + (isExemptedFromLeaderboard ? ChatColor.GREEN : ChatColor.RED) + isExemptedFromLeaderboard);
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + Lang.IS_EXEMPTED_CHECKING.getConfigValue() + ": " + (isExemptedFromAutomaticChecking ? ChatColor.GREEN : ChatColor.RED) + isExemptedFromAutomaticChecking);
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + Lang.IS_EXEMPTED_OBTAINING.getConfigValue() + ": " + (isExemptedFromTimeAddition ? ChatColor.GREEN : ChatColor.RED) + isExemptedFromTimeAddition);
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + Lang.LOCAL.getConfigValue() + ": " + ChatColor.GOLD + (localTotalTime <= 0 ? Lang.NONE.getConfigValue() : AutorankTools.timeToString(localTotalTime, TimeUnit.MINUTES)));
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + Lang.GLOBAL.getConfigValue() + ": " + ChatColor.GOLD + (globalTotalTime <= 0 ? Lang.NONE.getConfigValue() : AutorankTools.timeToString(globalTotalTime, TimeUnit.MINUTES)));
                }
            });
            this.runCommandTask(task);
            return true;
        }
    }

    public String getDescription() {
        return "Show info of a player";
    }

    public String getPermission() {
        return "autorank.info.self";
    }

    public String getUsage() {
        return "/ar info <player>";
    }
}

