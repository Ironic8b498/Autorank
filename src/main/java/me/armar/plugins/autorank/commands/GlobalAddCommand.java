package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.storage.PlayTimeStorageProvider;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GlobalAddCommand extends AutorankCommand {
    private final Autorank plugin;

    public GlobalAddCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!this.hasPermission("autorank.gadd", sender)) {
            return true;
        } else if (args.length < 3) {
            AutorankTools.sendDeserialize(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        } else if (!this.plugin.getPlayTimeStorageManager().isStorageTypeActive(PlayTimeStorageProvider.StorageType.DATABASE)) {
            AutorankTools.sendDeserialize(sender, Lang.MYSQL_IS_NOT_ENABLED.getConfigValue());
            return true;
        } else {
            CompletableFuture<Void> task = UUIDManager.getUUID(args[1]).thenAccept((uuid) -> {
                if (uuid == null) {
                    AutorankTools.sendDeserialize(sender, Lang.UNKNOWN_PLAYER.getConfigValue(args[1]));
                } else {
                    int value = AutorankTools.readTimeInput(args, 2);
                    if (value >= 0) {
                        TimeType[] var5 = TimeType.values();
                        int globalPlayTime = var5.length;

                        for(int var7 = 0; var7 < globalPlayTime; ++var7) {
                            TimeType timeType = var5[var7];
                            this.plugin.getPlayTimeManager().addGlobalPlayTime(timeType, uuid, value);
                        }

                        String playerName = args[1];

                        try {
                            playerName = UUIDManager.getPlayerName(uuid).get();
                        } catch (ExecutionException | InterruptedException var11) {
                            var11.printStackTrace();
                        }

                        globalPlayTime = 0;

                        try {
                            globalPlayTime = this.plugin.getPlayTimeManager().getGlobalPlayTime(TimeType.TOTAL_TIME, uuid).get();
                        } catch (InterruptedException var9) {
                            var9.printStackTrace();
                        } catch (ExecutionException var10) {
                            var10.printStackTrace();
                        }

                        globalPlayTime = globalPlayTime + value;
                        AutorankTools.sendDeserialize(sender, Lang.PLAYTIME_CHANGED.getConfigValue(playerName, AutorankTools.timeToString(globalPlayTime, TimeUnit.MINUTES)));
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
