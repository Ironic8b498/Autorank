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

public class RemoveCommand extends AutorankCommand {
    private final Autorank plugin;

    public RemoveCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!this.hasPermission("autorank.remove", sender)) {
            return true;
        } else if (args.length < 3) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        } else {
            CompletableFuture<Void> task = UUIDManager.getUUID(args[1]).thenAccept((uuid) -> {
                if (uuid == null) {
                    sender.sendMessage(Lang.UNKNOWN_PLAYER.getConfigValue(args[1]));
                } else {
                    int value = AutorankTools.readTimeInput(args, 2);
                    String playerName = args[1];
                    int newPlayerTime = 0;

                    try {
                        playerName = UUIDManager.getPlayerName(uuid).get();
                        newPlayerTime = this.plugin.getPlayTimeManager().getPlayTime(TimeType.TOTAL_TIME, uuid).get();
                    } catch (ExecutionException | InterruptedException var8) {
                        var8.printStackTrace();
                    }

                    if (value >= 0) {
                        this.plugin.getPlayTimeStorageManager().addPlayerTime(uuid, -value);
                        AutorankTools.sendColoredMessage(sender, Lang.PLAYTIME_CHANGED.getConfigValue(playerName, AutorankTools.timeToString(newPlayerTime, TimeUnit.MINUTES)));
                    } else {
                        AutorankTools.sendColoredMessage(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
                    }

                }
            });
            this.runCommandTask(task);
            return true;
        }
    }

    public String getDescription() {
        return "Remove [value] from [player]'s time.";
    }

    public String getPermission() {
        return "autorank.remove";
    }

    public String getUsage() {
        return "/ar remove [player] [value]";
    }
}
