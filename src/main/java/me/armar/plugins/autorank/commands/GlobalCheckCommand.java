package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.storage.PlayTimeStorageProvider;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GlobalCheckCommand extends AutorankCommand {
    private final Autorank plugin;

    public GlobalCheckCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        var mm = MiniMessage.miniMessage();
        if (!this.plugin.getPlayTimeStorageManager().isStorageTypeActive(PlayTimeStorageProvider.StorageType.DATABASE)) {
            Component mysql_is_not_enabled = mm.deserialize(Lang.MYSQL_IS_NOT_ENABLED.getConfigValue(new Object[0]));
            plugin.adventure().player((Player) sender).sendMessage(mysql_is_not_enabled);
            return true;
        } else {
            CompletableFuture<Void> task = CompletableFuture.completedFuture(null).thenAccept((nothing) -> {
                UUID uuid = null;
                String playerName = null;
                Player player;
                if (args.length > 1) {
                    if (!this.hasPermission("autorank.checkothers", sender)) {
                        return;
                    }

                    player = this.plugin.getServer().getPlayer(args[1]);

                    try {
                        uuid = UUIDManager.getUUID(args[1]).get();
                        playerName = UUIDManager.getPlayerName(uuid).get();
                    } catch (ExecutionException | InterruptedException var9) {
                        var9.printStackTrace();
                    }

                    if (uuid == null) {
                        Component player_is_invalid = mm.deserialize(Lang.PLAYER_IS_INVALID.getConfigValue(args[1]));
                        plugin.adventure().player((Player) sender).sendMessage(player_is_invalid);
                        return;
                    }

                    if (player != null) {
                        if (player.hasPermission("autorank.exclude")) {
                            Component player_is_excluded = mm.deserialize(Lang.PLAYER_IS_EXCLUDED.getConfigValue(new Object[]{player.getName()}));
                            plugin.adventure().player((Player) sender).sendMessage(player_is_excluded);
                            return;
                        }

                        playerName = player.getName();
                    }
                } else {
                    if (!(sender instanceof Player)) {
                        Component cannot_check_console = mm.deserialize(Lang.CANNOT_CHECK_CONSOLE.getConfigValue());
                        plugin.adventure().player((Player) sender).sendMessage(cannot_check_console);
                        return;
                    }

                    if (!this.hasPermission("autorank.gcheck", sender)) {
                        return;
                    }

                    if (sender.hasPermission("autorank.exclude")) {
                        Component player_is_excluded = mm.deserialize(Lang.PLAYER_IS_EXCLUDED.getConfigValue(new Object[]{sender.getName()}));
                        plugin.adventure().player((Player) sender).sendMessage(player_is_excluded);
                        return;
                    }

                    player = (Player)sender;
                    uuid = player.getUniqueId();
                    playerName = player.getName();
                }

                int globalPlayTime = 0;

                try {
                    globalPlayTime = this.plugin.getPlayTimeManager().getGlobalPlayTime(TimeType.TOTAL_TIME, uuid).get();
                } catch (ExecutionException | InterruptedException var8) {
                    var8.printStackTrace();
                }

                if (globalPlayTime < 0) {
                    Component player_is_invalid = mm.deserialize(Lang.PLAYER_IS_INVALID.getConfigValue(playerName));
                    plugin.adventure().player((Player) sender).sendMessage(player_is_invalid);
                } else {
                  //  AutorankTools.sendColoredMessage(sender, playerName + " has played for " + AutorankTools.timeToString(globalPlayTime, TimeUnit.MINUTES) + " across all servers.");
                    Component player_is_excluded = mm.deserialize(  Lang.HAS_PLAYED.getConfigValue(playerName) + AutorankTools.timeToString(globalPlayTime, TimeUnit.MINUTES) + Lang.ACROSS_ALL_SERVERS.getConfigValue());
                    plugin.adventure().player((Player) sender).sendMessage(player_is_excluded);
                }
            });
            this.runCommandTask(task);
            return true;
        }
    }

    public String getDescription() {
        return "Check [player]'s global playtime.";
    }

    public String getPermission() {
        return "autorank.gcheck";
    }

    public String getUsage() {
        return "/ar gcheck [player]";
    }
}
