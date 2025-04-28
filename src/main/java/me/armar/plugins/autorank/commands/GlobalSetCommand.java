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
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GlobalSetCommand extends AutorankCommand {
    private final Autorank plugin;

    public GlobalSetCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)){
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT.getConfigValue());
            return true;
        }
        if (args.length < 3) {
            AutorankTools.sendDeserialize(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        } else if (!this.plugin.getPlayTimeStorageManager().isStorageTypeActive(PlayTimeStorageProvider.StorageType.DATABASE)) {
            AutorankTools.sendDeserialize(sender, Lang.MYSQL_IS_NOT_ENABLED.getConfigValue());
            return true;
        } else {
            int value = AutorankTools.readTimeInput(args, 2);
            if (value >= 0) {
                if (!this.hasPermission("autorank.gset", sender)) {
                    return true;
                }

                CompletableFuture<Void> task = UUIDManager.getUUID(args[1]).thenAccept((uuid) -> {
                    if (uuid == null) {
                        AutorankTools.sendDeserialize(sender, Lang.UNKNOWN_PLAYER.getConfigValue(args[1]));
                    } else {
                        TimeType[] var5 = TimeType.values();
                        int var6 = var5.length;

                        for(int var7 = 0; var7 < var6; ++var7) {
                            TimeType timeType = var5[var7];
                            this.plugin.getPlayTimeManager().setGlobalPlayTime(timeType, uuid, value);
                        }

                        String playerName = args[1];

                        try {
                            playerName = UUIDManager.getPlayerName(uuid).get();
                        } catch (ExecutionException | InterruptedException var9) {
                            var9.printStackTrace();
                        }

                        AutorankTools.sendDeserialize(sender, Lang.PLAYTIME_CHANGED.getConfigValue(playerName, AutorankTools.timeToString(value, TimeUnit.MINUTES)));
                    }
                });
                this.runCommandTask(task);
            } else {
                AutorankTools.sendDeserialize(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            }

            return true;
        }
    }

    public String getDescription() {
        return "Set [player]'s global time to [value].";
    }

    public String getPermission() {
        return "autorank.gset";
    }

    public String getUsage() {
        return "/ar gset [player] [value in minutes]";
    }
}
