package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ForceCheckCommand extends AutorankCommand {
    private final Autorank plugin;

    public ForceCheckCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!this.hasPermission("autorank.forcecheck", sender)) {
            return true;
        } else if (args.length != 2) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        } else {
            String target = args[1];
            CompletableFuture<Void> task = UUIDManager.getUUID(target).thenAccept((uuid) -> {
                if (uuid == null) {
                    sender.sendMessage(Lang.UNKNOWN_PLAYER.getConfigValue(target));
                } else {
                    String playerName = null;

                    try {
                        playerName = UUIDManager.getPlayerName(uuid).get();
                    } catch (ExecutionException | InterruptedException var6) {
                        var6.printStackTrace();
                    }

                    if (this.plugin.getPlayerChecker().isExemptedFromAutomaticChecking(uuid)) {
                        sender.sendMessage(Lang.PLAYER_IS_EXCLUDED.getConfigValue(playerName));
                    } else {
                        this.plugin.getPlayerChecker().checkPlayer(uuid);
                        sender.sendMessage(ChatColor.GREEN + Lang.CHECKED.getConfigValue());
                    }
                }
            });
            this.runCommandTask(task);
            return true;
        }
    }

    public String getDescription() {
        return "Do a manual silent check.";
    }

    public String getPermission() {
        return "autorank.forcecheck";
    }

    public String getUsage() {
        return "/ar forcecheck <player>";
    }
}
