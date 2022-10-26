package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
        var mm = MiniMessage.miniMessage();
        this.plugin.getPlayerChecker().checkPlayer(uuid);
        this.plugin.getPathManager().autoAssignPaths(uuid);
        List<Path> activePaths = this.plugin.getPathManager().getActivePaths(uuid);
        if (activePaths.isEmpty()) {
            Component does_not_have_active = mm.deserialize(Lang.DOES_NOT_HAVE_ACTIVE.getConfigValue(playerName));
            plugin.adventure().player((Player) sender).sendMessage(does_not_have_active);
        } else {
            Component progress_of_paths = mm.deserialize(Lang.PROGRESS_OF_PATHS.getConfigValue(playerName));
            plugin.adventure().player((Player) sender).sendMessage(progress_of_paths);
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
//                TextComponent invalid_stoage_file = mm.deserialize(Lang.INVALID_STORAGE_FILE.getConfigValue());
//                plugin.adventure().player((Player) sender).sendMessage(invalid_stoage_file);
            }
            Player player = (Player) sender;
            //new MessageSender(player, String.valueOf(Lang.TO_VIEW_THE_PROGRESS.getConfigValue()+ " MessageSender"));
            //sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(Lang.TO_VIEW_THE_PROGRESS.getConfigValue())));
            Component to_view_the_progress = mm.deserialize(Lang.TO_VIEW_THE_PROGRESS.getConfigValue());
            plugin.adventure().player((Player) sender).sendMessage(to_view_the_progress);
        }
    }

    public void showSpecificPath(CommandSender sender, String playerName, UUID uuid, Path path) {
        var mm = MiniMessage.miniMessage();
        this.plugin.getPlayerChecker().checkPlayer(uuid);
        Component specificpath = mm.deserialize(Lang.SPECIFIC_PATH.getConfigValue());
        plugin.adventure().player((Player) sender).sendMessage(specificpath);
        Component specificpath1 = mm.deserialize(Lang.YOU_ARE_VIEWING.getConfigValue(playerName, path.getDisplayName()));
        plugin.adventure().player((Player) sender).sendMessage(specificpath1);
        Component specificpath2 = mm.deserialize(Lang.SPECIFIC_PATH.getConfigValue());
        plugin.adventure().player((Player) sender).sendMessage(specificpath2);
        Component specificpath3 = mm.deserialize(Lang.REQUIREMENTS.getConfigValue());
        plugin.adventure().player((Player) sender).sendMessage(specificpath3);
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
        var mm = MiniMessage.miniMessage();
        boolean showListOfPaths = false;
        Path targetPath = null;
        OfflinePlayer targetPlayer = null;
        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                Component you_should_specify = mm.deserialize(Lang.YOU_SHOULD_SPECIFY.getConfigValue());
                plugin.adventure().player((Player) sender).sendMessage(you_should_specify);
                return true;
            } else if (!this.hasPermission("autorank.check", sender)) {
                return true;
            } else if (this.plugin.getPlayerChecker().isExemptedFromAutomaticChecking(((Player)sender).getUniqueId())) {
                Component player_is_excluded = mm.deserialize(Lang.PLAYER_IS_EXCLUDED.getConfigValue(sender.getName()));
                plugin.adventure().player((Player) sender).sendMessage(player_is_excluded);
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
                        Component there_is_no_player = mm.deserialize(Lang.THERE_IS_NO_PLAYER.getConfigValue(args[1]));
                        plugin.adventure().player((Player) sender).sendMessage(there_is_no_player);
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
                    Component player_is_excluded = mm.deserialize(Lang.PLAYER_IS_EXCLUDED.getConfigValue(onlineTargetPlayer.getName()));
                    plugin.adventure().player((Player) sender).sendMessage(player_is_excluded);
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
                        Component does_not_have_as_active = mm.deserialize(Lang.DOES_NOT_HAVE_AS_ACTIVE.getConfigValue(finalTargetPlayerName, finalTargetPath.getDisplayName()));
                        plugin.adventure().player((Player) sender).sendMessage(does_not_have_as_active);
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
