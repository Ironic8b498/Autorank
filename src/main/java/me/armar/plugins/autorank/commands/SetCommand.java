package me.armar.plugins.autorank.commands;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetCommand extends AutorankCommand {
    private final Autorank plugin;

    public SetCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT.getConfigValue());
            return true;
        } else if (!this.hasPermission(this.getPermission(), sender)) {
            return true;
        } else {
            if (args.length < 3) {
                AutorankTools.sendDeserialize(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            } else {
                int value = AutorankTools.readTimeInput(args, 2);
                if (value >= 0) {
                    CompletableFuture<Void> task = UUIDManager.getUUID(args[1]).thenAccept((uuid) -> {
                        if (uuid == null) {
                            AutorankTools.sendDeserialize(sender, Lang.UNKNOWN_PLAYER.getConfigValue(args[1]));
                        } else {
                            String playerName = args[1];

                            try {
                                playerName = UUIDManager.getPlayerName(uuid).get();
                            } catch (InterruptedException | ExecutionException var9) {
                                var9.printStackTrace();
                            }

                            this.plugin.getPlayTimeStorageManager().setPlayerTime(uuid, value);
                            int newPlayerTime = 0;

                            try {
                                newPlayerTime = this.plugin.getPlayTimeManager().getPlayTime(TimeType.TOTAL_TIME, uuid).get();
                            } catch (InterruptedException | ExecutionException var8) {
                                var8.printStackTrace();
                            }

                            AutorankTools.sendDeserialize(sender, Lang.PLAYTIME_CHANGED.getConfigValue(playerName, AutorankTools.timeToString(newPlayerTime, TimeUnit.MINUTES)));
                        }

                    });
                    this.runCommandTask(task);
                } else {
                    AutorankTools.sendDeserialize(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
                }
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
