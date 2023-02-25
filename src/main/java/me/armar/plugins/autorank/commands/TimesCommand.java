package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playtimes.PlayTimeManager;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class TimesCommand extends AutorankCommand {
    private final Autorank plugin;

    public TimesCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        var mm = MiniMessage.miniMessage();
        String targetName = "";
        if (!(sender instanceof Player)) {
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT.getConfigValue());
            return true;
        }
        if (args.length > 1) {
            if (!this.hasPermission("autorank.times.others", sender)) {
                return true;
            }

            targetName = args[1];
        } else {
            if (!(sender instanceof Player)) {
                AutorankTools.sendDeserialize(sender, Lang.CANNOT_CHECK_CONSOLE.getConfigValue());
                return true;
            }

            if (!this.hasPermission("autorank.times.self", sender)) {
                return true;
            }

            targetName = sender.getName();
        }

        String finalTargetName = targetName;
        CompletableFuture<Void> task = UUIDManager.getUUID(targetName).thenAccept((uuid) -> {
            if (uuid == null) {
                AutorankTools.sendDeserialize(sender, Lang.UNKNOWN_PLAYER.getConfigValue(finalTargetName));
            } else {
                String playerName = finalTargetName;

                try {
                    playerName = UUIDManager.getPlayerName(uuid).get();
                } catch (ExecutionException | InterruptedException var12) {
                    var12.printStackTrace();
                }

                PlayTimeManager playTimeManager = this.plugin.getPlayTimeManager();
                int daily = 0;
                int weekly = 0;
                int monthly = 0;
                int total = 0;

                try {
                    daily = playTimeManager.getPlayTime(TimeType.DAILY_TIME, uuid).get();
                    weekly = playTimeManager.getPlayTime(TimeType.WEEKLY_TIME, uuid).get();
                    monthly = playTimeManager.getPlayTime(TimeType.MONTHLY_TIME, uuid).get();
                    total = playTimeManager.getPlayTime(TimeType.TOTAL_TIME, uuid).get();
                } catch (ExecutionException | InterruptedException var11) {
                    var11.printStackTrace();
                }

                TimeUnit time = TimeUnit.valueOf(plugin.getSettingsConfig().getTimeFormat());
                String order = plugin.getSettingsConfig().getTimeOrder();
                if (order.equals("START")) {
                    Component times_command = mm.deserialize(Lang.AUTORANK_TIMES_HEADER.getConfigValue(playerName))
                            .append(mm.deserialize("<NEWLINE>" + Lang.AUTORANK_TIMES_PLAYER_PLAYED.getConfigValue(playerName))
                                    .append(mm.deserialize("<NEWLINE>" + Lang.AUTORANK_TIMES_TODAY.getConfigValue(AutorankTools.timeToString(daily, time))))
                                    .append(mm.deserialize("<NEWLINE>" + Lang.AUTORANK_TIMES_THIS_WEEK.getConfigValue(AutorankTools.timeToString(weekly, time))))
                                    .append(mm.deserialize("<NEWLINE>" + Lang.AUTORANK_TIMES_THIS_MONTH.getConfigValue(AutorankTools.timeToString(monthly, time))))
                                    .append(mm.deserialize("<NEWLINE>" + Lang.AUTORANK_TIMES_TOTAL.getConfigValue(AutorankTools.timeToString(total, time)))));
                    plugin.adventure().player((Player) sender).sendMessage(times_command);
                }
                if (order.equals("START_WITH")) {
                    Component times_command = mm.deserialize(Lang.AUTORANK_TIMES_HEADER.getConfigValue(playerName))
                            .append(mm.deserialize("<NEWLINE>" + Lang.AUTORANK_TIMES_PLAYER_PLAYED.getConfigValue(playerName))
                                    .append(mm.deserialize("<NEWLINE>" + Lang.AUTORANK_TIMES_TODAY.getConfigValue(AutorankTools.timeStartToString(daily, time))))
                                    .append(mm.deserialize("<NEWLINE>" + Lang.AUTORANK_TIMES_THIS_WEEK.getConfigValue(AutorankTools.timeStartToString(weekly, time))))
                                    .append(mm.deserialize("<NEWLINE>" + Lang.AUTORANK_TIMES_THIS_MONTH.getConfigValue(AutorankTools.timeStartToString(monthly, time))))
                                    .append(mm.deserialize("<NEWLINE>" + Lang.AUTORANK_TIMES_TOTAL.getConfigValue(AutorankTools.timeStartToString(total, time)))));
                    plugin.adventure().player((Player) sender).sendMessage(times_command);
                }
            }
        });
        this.runCommandTask(task);
        return true;
    }

    public String getDescription() {
        return "Show the amount of time you played.";
    }

    public String getPermission() {
        return "autorank.times.self";
    }

    public String getUsage() {
        return "/ar times <player>";
    }
}
