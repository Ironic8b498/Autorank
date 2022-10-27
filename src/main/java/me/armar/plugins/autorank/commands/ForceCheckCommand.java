package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ForceCheckCommand extends AutorankCommand {
    private final Autorank plugin;

    public ForceCheckCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        var mm = MiniMessage.miniMessage();
        if (!this.hasPermission("autorank.forcecheck", sender)) {
            return true;
        } else if (args.length != 2) {
            Component invalid_format = mm.deserialize(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            plugin.adventure().player((Player) sender).sendMessage(invalid_format);
            return true;
        } else {
            String target = args[1];
            CompletableFuture<Void> task = UUIDManager.getUUID(target).thenAccept((uuid) -> {
                if (uuid == null) {
                    Component unknown_player = mm.deserialize(Lang.UNKNOWN_PLAYER.getConfigValue(target));
                    plugin.adventure().player((Player) sender).sendMessage(unknown_player);
                } else {
                    String playerName = null;

                    try {
                        playerName = UUIDManager.getPlayerName(uuid).get();
                    } catch (ExecutionException | InterruptedException var6) {
                        var6.printStackTrace();
                    }

                    if (this.plugin.getPlayerChecker().isExemptedFromAutomaticChecking(uuid)) {
                        Component player_is_excluded = mm.deserialize(Lang.PLAYER_IS_EXCLUDED.getConfigValue(playerName));
                        plugin.adventure().player((Player) sender).sendMessage(player_is_excluded);
                    } else {
                        this.plugin.getPlayerChecker().checkPlayer(uuid);
                        Component checked = mm.deserialize(Lang.CHECKED.getConfigValue());
                        plugin.adventure().player((Player) sender).sendMessage(checked);
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
