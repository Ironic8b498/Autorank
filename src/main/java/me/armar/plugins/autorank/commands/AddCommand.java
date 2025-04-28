package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class AddCommand extends AutorankCommand {
    private final Autorank plugin;

    public AddCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length < 3) {
                AutorankTools.consoleDeserialize(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
                return true;
            } else {
                CompletableFuture<Void> task = UUIDManager.getUUID(args[1]).thenAccept((uuid) -> {
                    if (uuid == null) {
                        AutorankTools.consoleDeserialize(Lang.UNKNOWN_PLAYER.getConfigValue(args[1]));
                    } else {
                        int value = AutorankTools.readTimeInput(args, 2);
                        if (value >= 0) {
                            this.plugin.getPlayTimeStorageManager().addPlayerTime(uuid, value);
                            String playerName = null;
                            int newPlayerTime = 0;

                            try {
                                TimeUnit.SECONDS.sleep((long) .2);
                                playerName = UUIDManager.getPlayerName(uuid).get();
                                newPlayerTime = this.plugin.getPlayTimeManager().getPlayTime(TimeType.TOTAL_TIME, uuid).get();
                            } catch (ExecutionException | InterruptedException var8) {
                                var8.printStackTrace();
                            }

                            AutorankTools.consoleDeserialize(Lang.PLAYTIME_CHANGED.getConfigValue(playerName, AutorankTools.timeToString(newPlayerTime, TimeUnit.MINUTES)));
                        } else {
                            AutorankTools.consoleDeserialize(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
                        }

                    }
                });
                this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                    try {
                        task.get();
                    } catch (ExecutionException | InterruptedException var2) {
                        var2.printStackTrace();
                    }

                });
                return true;
            }
        }
        if (!this.hasPermission("autorank.add", sender)) {
            return true;
        } else if (args.length < 3) {
            AutorankTools.sendDeserialize(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        } else {
            CompletableFuture<Void> task = UUIDManager.getUUID(args[1]).thenAccept((uuid) -> {
                if (uuid == null) {
                    AutorankTools.sendDeserialize(sender, Lang.UNKNOWN_PLAYER.getConfigValue(args[1]));
                } else {
                    int value = AutorankTools.readTimeInput(args, 2);
                    if (value >= 0) {
                        this.plugin.getPlayTimeStorageManager().addPlayerTime(uuid, value);
                        String playerName = null;
                        int newPlayerTime = 0;

                        try {
                            TimeUnit.SECONDS.sleep((long) .2);
                            playerName = UUIDManager.getPlayerName(uuid).get();
                            newPlayerTime = this.plugin.getPlayTimeManager().getPlayTime(TimeType.TOTAL_TIME, uuid).get();
                        } catch (ExecutionException | InterruptedException var8) {
                            var8.printStackTrace();
                        }

                        AutorankTools.sendDeserialize(sender, Lang.PLAYTIME_CHANGED.getConfigValue(playerName, AutorankTools.timeToString(newPlayerTime, TimeUnit.MINUTES)));
                    } else {
                        AutorankTools.sendDeserialize(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
                    }

                }
            });
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                try {
                    task.get();
                } catch (ExecutionException | InterruptedException var2) {
                    var2.printStackTrace();
                }

            });
            return true;
        }
    }

    public String getDescription() {
        return "Add [value] to [player]'s time";
    }

    public String getPermission() {
        return "autorank.add";
    }

    public String getUsage() {
        return "/ar add [player] [value]";
    }
}
