package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CheckCommand extends AutorankCommand {
    private final Autorank plugin;

    public CheckCommand(Autorank instance) {
        this.plugin = instance;
    }

    public void showPathsOverview(CommandSender sender, String playerName, UUID uuid) {
        this.plugin.getPlayerChecker().checkPlayer(uuid);
        this.plugin.getPathManager().autoAssignPaths(uuid);
        List<Path> activePaths = this.plugin.getPathManager().getActivePaths(uuid);
        if (activePaths.isEmpty()) {
            sender.sendMessage(ChatColor.GOLD + playerName + ChatColor.RED + Lang.DOES_NOT_HAVE_ACTIVE.getConfigValue());
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',Lang.PROGRESS_OF_PATHS.getConfigValue(playerName)));
            Iterator var5 = activePaths.iterator();

            while(var5.hasNext()) {
                Path activePath = (Path)var5.next();
                StringBuilder message = new StringBuilder(ChatColor.GRAY + Lang.PROGRESS_OF.getConfigValue() + ChatColor.BLUE + activePath.getDisplayName() + ChatColor.GRAY + "': ");
                double completeRatio = activePath.getProgress(uuid);
                message.append(ChatColor.GRAY + "[");

                int i;
                for(i = 0; (double)i < completeRatio * 10.0D; ++i) {
                    message.append(ChatColor.GREEN + "|");
                }

                for(i = 0; (double)i < 10.0D - completeRatio * 10.0D; ++i) {
                    message.append(ChatColor.RED + "|");
                }

                message.append(ChatColor.GRAY + "]").append(ChatColor.GOLD + " (").append((new BigDecimal(completeRatio * 100.0D)).setScale(2, RoundingMode.HALF_UP)).append("%)");
                sender.sendMessage(message.toString());
            }

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(Lang.TO_VIEW_THE_PROGRESS.getConfigValue())));
        }
    }

    public void showSpecificPath(CommandSender sender, String playerName, UUID uuid, Path path) {
        this.plugin.getPlayerChecker().checkPlayer(uuid);
        sender.sendMessage(ChatColor.DARK_AQUA + "-----------------------");
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Lang.YOU_ARE_VIEWING.getConfigValue(playerName, path.getDisplayName())));
        sender.sendMessage(ChatColor.DARK_AQUA + "-----------------------");
        sender.sendMessage(ChatColor.GRAY + String.valueOf(Lang.REQUIREMENTS.getConfigValue()));
        List<CompositeRequirement> allRequirements = path.getRequirements();
        List<CompositeRequirement> completedRequirements = path.getCompletedRequirements(uuid);
        List<String> messages = this.plugin.getPlayerChecker().formatRequirementsToList(allRequirements, completedRequirements);
        Iterator var8 = messages.iterator();

        while(var8.hasNext()) {
            String message = (String)var8.next();
            AutorankTools.sendColoredMessage(sender, message);
        }

    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean showListOfPaths = false;
        Path targetPath = null;
        OfflinePlayer targetPlayer = null;
        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + String.valueOf(Lang.YOU_SHOULD_SPECIFY.getConfigValue()));
                return true;
            } else if (!this.hasPermission("autorank.check", sender)) {
                return true;
            } else if (this.plugin.getPlayerChecker().isExemptedFromAutomaticChecking(((Player)sender).getUniqueId())) {
                sender.sendMessage(ChatColor.RED + Lang.PLAYER_IS_EXCLUDED.getConfigValue(new Object[]{sender.getName()}));
                return true;
            } else {
                Player player = (Player)sender;
                CompletableFuture<Void> task = this.plugin.getPlayTimeManager().getPlayTime(TimeType.TOTAL_TIME, player.getUniqueId()).thenAccept((playTime) -> {
                    AutorankTools.sendColoredMessage(sender, Lang.HAS_PLAYED_FOR.getConfigValue(player.getName(), AutorankTools.timeToString(playTime, TimeUnit.MINUTES)));
                    List<Path> activePaths = this.plugin.getPathManager().getActivePaths(player.getUniqueId());
                    if (activePaths.size() == 1 && this.plugin.getSettingsConfig().showDetailsOfPathWithOneActivePath()) {
                        this.showSpecificPath(sender, player.getName(), player.getUniqueId(), activePaths.get(0));
                    } else {
                        this.showPathsOverview(sender, player.getName(), player.getUniqueId());
                    }

                });
                this.runCommandTask(task);
                return true;
            }
        } else {
            boolean isPath;
            if (args.length >= 2) {
                isPath = false;
                boolean isPlayer = false;
                targetPlayer = this.plugin.getServer().getOfflinePlayer(args[1].trim());
                if (targetPlayer.hasPlayedBefore()) {
                    isPlayer = true;
                }

                if (isPlayer && args.length > 2) {
                    targetPath = this.plugin.getPathManager().findPathByDisplayName(AutorankCommand.getStringFromArgs(args, 2).trim(), false);
                } else {
                    targetPath = this.plugin.getPathManager().findPathByDisplayName(AutorankCommand.getStringFromArgs(args, 1).trim(), false);
                }

                if (targetPath != null) {
                    isPath = true;
                }

                if (isPath) {
                    showListOfPaths = false;
                    if (!isPlayer) {
                        if (!(sender instanceof Player)) {
                            AutorankTools.sendColoredMessage(sender, Lang.CANNOT_CHECK_CONSOLE.getConfigValue());
                            return true;
                        }

                        targetPlayer = (OfflinePlayer)sender;
                    }
                } else {
                    if (!isPlayer) {
                        sender.sendMessage(ChatColor.RED + Lang.THERE_IS_NO_PLAYER.getConfigValue(args[1]));
                        return true;
                    }

                    showListOfPaths = true;
                }
            }

            if (targetPlayer.getName().equalsIgnoreCase(sender.getName())) {
                if (!this.hasPermission("autorank.check", sender)) {
                    return true;
                }
            } else if (!this.hasPermission("autorank.checkothers", sender)) {
                return true;
            }

            isPath = targetPlayer.isOnline();
            String targetPlayerName = null;
            UUID targetUUID = null;
            if (isPath) {
                Player onlineTargetPlayer = targetPlayer.getPlayer();
                if (this.plugin.getPlayerChecker().isExemptedFromAutomaticChecking(onlineTargetPlayer.getUniqueId())) {
                    sender.sendMessage(ChatColor.RED + Lang.PLAYER_IS_EXCLUDED.getConfigValue(new Object[]{onlineTargetPlayer.getName()}));
                    return true;
                }

                targetPlayerName = onlineTargetPlayer.getName();
                targetUUID = onlineTargetPlayer.getUniqueId();
            } else {
                try {
                    targetUUID = UUIDManager.getUUID(targetPlayer.getName()).get();
                } catch (ExecutionException | InterruptedException var16) {
                    var16.printStackTrace();
                }

                targetPlayerName = targetPlayer.getName();
            }

            String finalTargetPlayerName = targetPlayerName;
            boolean finalShowListOfPaths = showListOfPaths;
            UUID finalTargetUUID = targetUUID;
            Path finalTargetPath = targetPath;
            CompletableFuture<Void> task = this.plugin.getPlayTimeManager().getPlayTime(TimeType.TOTAL_TIME, targetUUID).thenAccept((playTime) -> {
                AutorankTools.sendColoredMessage(sender, Lang.HAS_PLAYED_FOR.getConfigValue(finalTargetPlayerName, AutorankTools.timeToString(playTime, TimeUnit.MINUTES)));
                if (finalShowListOfPaths) {
                    this.showPathsOverview(sender, finalTargetPlayerName, finalTargetUUID);
                } else {
                    if (finalTargetPath != null && !finalTargetPath.isActive(finalTargetUUID)) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Lang.DOES_NOT_HAVE_AS_ACTIVE.getConfigValue(finalTargetPlayerName, finalTargetPath.getDisplayName())));
                        return;
                    }

                    this.showSpecificPath(sender, finalTargetPlayerName, finalTargetUUID, finalTargetPath);
                }

            });
            this.runCommandTask(task);
            return true;
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        } else {
            Player player = (Player)sender;
            String typedPath = AutorankCommand.getStringFromArgs(args, 1);
            List<String> suggestedPaths = this.plugin.getPathManager().getActivePaths(player.getUniqueId()).stream().map(Path::getDisplayName).collect(Collectors.toList());
            return AutorankCommand.getOptionsStartingWith(suggestedPaths, typedPath);
        }
    }

    public String getDescription() {
        return "Check [player]'s status";
    }

    public String getPermission() {
        return "autorank.check";
    }

    public String getUsage() {
        return "/ar check <player> <path>";
    }
}
