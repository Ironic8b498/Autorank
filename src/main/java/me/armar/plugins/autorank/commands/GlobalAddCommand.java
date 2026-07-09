package me.armar.plugins.autorank.commands;

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

public class GlobalAddCommand extends AutorankCommand {
    private final Autorank plugin;

    public GlobalAddCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT.getConfigValue());
            return true;
        } else if (!this.hasPermission(this.getPermission(), sender)) {
            return true;
        } else if (args.length < 3) {
            AutorankTools.sendDeserialize(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        } else if (!this.plugin.getPlayTimeStorageManager().isStorageTypeActive(StorageType.DATABASE)) {
            AutorankTools.sendDeserialize(sender, Lang.MYSQL_IS_NOT_ENABLED.getConfigValue());
            return true;
        } else {
            CompletableFuture<Void> task = UUIDManager.getUUID(args[1]).thenAccept((uuid) -> {
                if (uuid == null) {
                    AutorankTools.sendDeserialize(sender, Lang.UNKNOWN_PLAYER.getConfigValue(args[1]));
                } else {
                    int value = AutorankTools.readTimeInput(args, 2);
                    if (value >= 0) {
                        for(TimeType timeType : TimeType.values()) {
                            this.plugin.getPlayTimeManager().addGlobalPlayTime(timeType, uuid, value);
                        }

                        String playerName = args[1];

                        try {
                            playerName = UUIDManager.getPlayerName(uuid).get();
                        } catch (InterruptedException | ExecutionException var11) {
                            var11.printStackTrace();
                        }

                        int var111 = 0;

                        try {
                            var111 = this.plugin.getPlayTimeManager().getGlobalPlayTime(TimeType.TOTAL_TIME, uuid).get();
                        } catch (ExecutionException | InterruptedException var9) {
                            var9.printStackTrace();
                        }

                        var111 += value;
                        AutorankTools.sendDeserialize(sender, Lang.PLAYTIME_CHANGED.getConfigValue(playerName, AutorankTools.timeToString(var111, TimeUnit.MINUTES)));
                    } else {
                        AutorankTools.sendDeserialize(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
                    }
                }

            });
            this.runCommandTask(task);
            return true;
        }
    }

    public String getDescription() {
        return "Add [value] to [player]'s global time";
    }

    public String getPermission() {
        return "autorank.gadd";
    }

    public String getUsage() {
        return "/ar gadd [player] [value]";
    }
}
