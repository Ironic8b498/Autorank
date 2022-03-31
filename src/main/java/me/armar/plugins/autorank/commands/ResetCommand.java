package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.conversations.AutorankConversation;
import me.armar.plugins.autorank.commands.conversations.resetcommand.ResetConversationType;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.bukkit.Bukkit.getLogger;

public class ResetCommand extends AutorankCommand {
    private final Autorank plugin;

    public ResetCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length != 3) {
                getLogger().warning(Lang.INVALID_FORMAT.getConfigValue("/ar reset <player> <action>"));
                return true;
            } else {
                String target = args[1];
                String action = args[2];
                UUID uuid = this.plugin.getUUIDStorage().getStoredUUID(target);
                if (uuid == null) {
                    getLogger().warning(Lang.PLAYER_IS_INVALID.getConfigValue(target));
                    return true;
                } else {
                    if (action.equalsIgnoreCase("activeprogress")) {
                        this.plugin.getPathManager().resetProgressOnActivePaths(uuid);
                        getLogger().info("Reset progress on all active paths of " + target);
                    } else if (action.equalsIgnoreCase("activepaths")) {
                        this.plugin.getPathManager().resetActivePaths(uuid);
                        getLogger().info("Removed all active paths of " + target);
                    } else if (action.equalsIgnoreCase("completedpaths")) {
                        this.plugin.getPathManager().resetCompletedPaths(uuid);
                        getLogger().info("Removed all completed paths of " + target);
                    } else {
                        if (!action.equalsIgnoreCase("allprogress")) {
                            getLogger().warning("Invalid action. You can only use: activeprogress, activepaths, allprogress or completedpaths.");
                            return true;
                        }
                        this.plugin.getPathManager().resetAllProgress(uuid);
                        getLogger().info("Reset progress on all paths (active AND completed) of " + target);
                    }
                }
                return true;
            }
        }

        if (!this.hasPermission("autorank.reset", sender)) {
            return true;
        } else {
            AutorankConversation resetConversation = AutorankConversation.fromFirstPrompt(new ResetConversationType());
            resetConversation.afterConversationEnded((result) -> {
                if (result.wasSuccessful()) {
                    String playerName = result.getStorageString("playerName");
                    String resetType = result.getStorageString(ResetConversationType.RESET_TYPE);
                    UUID uuid = null;

                    try {
                        uuid = UUIDManager.getUUID(playerName).get();
                    } catch (ExecutionException | InterruptedException var7) {
                        var7.printStackTrace();
                    }

                    if (uuid == null) {
                        sender.sendMessage(Lang.PLAYER_IS_INVALID.getConfigValue(playerName));
                    } else {
                        if (resetType.equalsIgnoreCase(ResetConversationType.RESET_ACTIVE_PROGRESS)) {
                            this.plugin.getPathManager().resetProgressOnActivePaths(uuid);
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Lang.RESET_PROGRESS.getConfigValue(playerName)));
                        } else if (resetType.equalsIgnoreCase(ResetConversationType.RESET_ACTIVE_PATHS)) {
                            this.plugin.getPathManager().resetActivePaths(uuid);
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Lang.REMOVED_ALL_ACTIVE.getConfigValue(playerName)));
                        } else if (resetType.equalsIgnoreCase(ResetConversationType.RESET_COMPLETED_PATHS)) {
                            this.plugin.getPathManager().resetCompletedPaths(uuid);
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Lang.REMOVED_ALL_COMPLETED.getConfigValue(playerName)));
                        } else if (resetType.equalsIgnoreCase(ResetConversationType.RESET_ALL_PROGRESS)) {
                            this.plugin.getPathManager().resetAllProgress(uuid);
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Lang.RESET_PROGRESS_ON_ALL.getConfigValue(playerName)));
                        }

                    }
                }
            });
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                resetConversation.startConversationAsSender(sender);
            });
            return true;
        }
    }



/*    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return args.length == 2 ? null : Arrays.asList("progress", "activepaths", "completedpaths");
    }*/

    public String getDescription() {
        return "Reset certain storage of a player";
    }

    public String getPermission() {
        return "autorank.reset";
    }

    public String getUsage() {
        return "/ar reset <player> <action>";
    }
}
