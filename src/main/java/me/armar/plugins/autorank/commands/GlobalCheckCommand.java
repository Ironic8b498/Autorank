package me.armar.plugins.autorank.commands;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.storage.PlayTimeStorageProvider.StorageType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GlobalCheckCommand extends AutorankCommand {
    private final Autorank plugin;

    public GlobalCheckCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT.getConfigValue());
            return true;
        } else if (!this.hasPermission(this.getPermission(), sender)) {
            return true;
        } else {
            if (!this.plugin.getPlayTimeStorageManager().isStorageTypeActive(StorageType.DATABASE)) {
                AutorankTools.sendDeserialize(sender, Lang.MYSQL_IS_NOT_ENABLED.getConfigValue());
            } else {
                CompletableFuture<Void> task = CompletableFuture.completedFuture(null).thenAccept((nothing) -> {
                    UUID uuid = null;
                    String playerName = null;
                    if (args.length > 1) {
                        if (!this.hasPermission("autorank.checkothers", sender)) {
                            return;
                        }

                        Player player = this.plugin.getServer().getPlayer(args[1]);

                        try {
                            uuid = UUIDManager.getUUID(args[1]).get();
                            playerName = UUIDManager.getPlayerName(uuid).get();
                        } catch (InterruptedException | ExecutionException var9) {
                            var9.printStackTrace();
                        }

                        if (uuid == null) {
                            AutorankTools.sendDeserialize(sender, Lang.PLAYER_IS_INVALID.getConfigValue(args[1]));
                            return;
                        }

                        if (player != null) {
                            if (player.hasPermission("autorank.exclude")) {
                                AutorankTools.sendDeserialize(sender, Lang.PLAYER_IS_EXCLUDED.getConfigValue(player.getName()));
                                return;
                            }

                            playerName = player.getName();
                        }
                    } else {
                        if (sender.hasPermission("autorank.exclude")) {
                            AutorankTools.sendDeserialize(sender, Lang.PLAYER_IS_EXCLUDED.getConfigValue(sender.getName()));
                            return;
                        }

                        Player player = (Player)sender;
                        uuid = player.getUniqueId();
                        playerName = player.getName();
                    }

                    int globalPlayTime = 0;

                    try {
                        globalPlayTime = this.plugin.getPlayTimeManager().getGlobalPlayTime(TimeType.TOTAL_TIME, uuid).get();
                    } catch (InterruptedException | ExecutionException var8) {
                        var8.printStackTrace();
                    }

                    if (globalPlayTime < 0) {
                        AutorankTools.sendDeserialize(sender, Lang.PLAYER_IS_INVALID.getConfigValue(playerName));
                    } else {
                        String var10001 = Lang.HAS_PLAYED.getConfigValue(playerName);
                        AutorankTools.sendDeserialize(sender, var10001 + AutorankTools.timeToString(globalPlayTime, TimeUnit.MINUTES) + Lang.ACROSS_ALL_SERVERS.getConfigValue());
                    }

                });
                this.runCommandTask(task);
            }

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
