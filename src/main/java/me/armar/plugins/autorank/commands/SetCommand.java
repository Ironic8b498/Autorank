package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class SetCommand extends AutorankCommand {
    private final Autorank plugin;

    public SetCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        } else {
            int value = AutorankTools.readTimeInput(args, 2);
            if (value >= 0) {
                if (!this.hasPermission("autorank.set", sender)) {
                    return true;
                }

                CompletableFuture<Void> task = UUIDManager.getUUID(args[1]).thenAccept((uuid) -> {
                    if (uuid == null) {
                        sender.sendMessage(Lang.UNKNOWN_PLAYER.getConfigValue(args[1]));
                    } else {
                        String playerName = args[1];

                        try {
                            playerName = UUIDManager.getPlayerName(uuid).get();
                        } catch (ExecutionException | InterruptedException var9) {
                            var9.printStackTrace();
                        }

                        this.plugin.getPlayTimeStorageManager().setPlayerTime(uuid, value);
                        int newPlayerTime = 0;

                        try {
                            newPlayerTime = this.plugin.getPlayTimeManager().getPlayTime(TimeType.TOTAL_TIME, uuid).get();
                        } catch (ExecutionException | InterruptedException var8) {
                            var8.printStackTrace();
                        }

                        AutorankTools.sendColoredMessage(sender, Lang.PLAYTIME_CHANGED.getConfigValue(playerName, AutorankTools.timeToString(newPlayerTime, TimeUnit.MINUTES)));
                    }
                });
                this.runCommandTask(task);
            } else {
                AutorankTools.sendColoredMessage(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            }

            return true;
        }
    }

    public String getDescription() {
        return "Set [player]'s time to [value].";
    }

    public String getPermission() {
        return "autorank.set";
    }

    public String getUsage() {
        return "/ar set [player] [value]";
    }
}
