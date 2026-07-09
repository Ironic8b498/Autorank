package me.armar.plugins.autorank.commands;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ForceCheckCommand extends AutorankCommand {
    private final Autorank plugin;

    public ForceCheckCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT.getConfigValue());
            return true;
        } else if (!this.hasPermission(this.getPermission(), sender)) {
            return true;
        } else if (args.length != 2) {
            AutorankTools.sendDeserialize(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        } else {
            String target = args[1];
            CompletableFuture<Void> task = UUIDManager.getUUID(target).thenAccept((uuid) -> {
                if (uuid == null) {
                    AutorankTools.sendDeserialize(sender, Lang.UNKNOWN_PLAYER.getConfigValue(target));
                } else {
                    String playerName = null;

                    try {
                        playerName = UUIDManager.getPlayerName(uuid).get();
                    } catch (InterruptedException | ExecutionException var6) {
                        var6.printStackTrace();
                    }

                    if (this.plugin.getPlayerChecker().isExemptedFromAutomaticChecking(uuid)) {
                        AutorankTools.sendDeserialize(sender, Lang.PLAYER_IS_EXCLUDED.getConfigValue(playerName));
                    } else {
                        this.plugin.getPlayerChecker().checkPlayer(uuid);
                        AutorankTools.sendDeserialize(sender, Lang.CHECKED.getConfigValue());
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
