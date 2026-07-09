package me.armar.plugins.autorank.commands;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playtimes.PlayTimeManager;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

public class TimesCommand extends AutorankCommand {
    private final Autorank plugin;

    public TimesCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        Player player = ((Player)sender).getPlayer();
        MiniMessage mm = MiniMessage.miniMessage();
        if (!(sender instanceof Player)) {
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT.getConfigValue());
            return true;
        } else {
            String targetName;
            if (args.length > 1) {
                if (!Objects.requireNonNull(player).hasPermission("autorank.times.others")) {
                    return true;
                }

                targetName = args[1];
            } else {
                if (!Objects.requireNonNull(player).hasPermission("autorank.times.self")) {
                    return true;
                }

                targetName = sender.getName();
            }

            CompletableFuture<Void> task = UUIDManager.getUUID(targetName).thenAccept((uuid) -> {
                if (uuid == null) {
                    AutorankTools.sendDeserialize(sender, Lang.UNKNOWN_PLAYER.getConfigValue(targetName));
                } else {
                    String playerName = targetName;

                    try {
                        playerName = UUIDManager.getPlayerName(uuid).get();
                    } catch (InterruptedException | ExecutionException var12) {
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
                    } catch (InterruptedException | ExecutionException var11) {
                        var11.printStackTrace();
                    }

                    TimeUnit time = TimeUnit.valueOf(this.plugin.getSettingsConfig().getTimeFormat());
                    String order = this.plugin.getSettingsConfig().getTimeOrder();
                    if (order.equals("START")) {
                        Component var10000 = mm.deserialize(Lang.AUTORANK_TIMES_HEADER.getConfigValue(playerName));
                        Lang var10002 = Lang.AUTORANK_TIMES_PLAYER_PLAYED;
                        Object[] var10003 = new Object[]{playerName};
                        Component var10001 = mm.deserialize("<NEWLINE>" + var10002.getConfigValue(var10003));
                        Lang var26 = Lang.AUTORANK_TIMES_TODAY;
                        Object[] var10004 = new Object[]{AutorankTools.timeToString(daily, time)};
                        var10001 = var10001.append(mm.deserialize("<NEWLINE>" + var26.getConfigValue(var10004)));
                        var26 = Lang.AUTORANK_TIMES_THIS_WEEK;
                        var10004 = new Object[]{AutorankTools.timeToString(weekly, time)};
                        var10001 = var10001.append(mm.deserialize("<NEWLINE>" + var26.getConfigValue(var10004)));
                        var26 = Lang.AUTORANK_TIMES_THIS_MONTH;
                        var10004 = new Object[]{AutorankTools.timeToString(monthly, time)};
                        var10001 = var10001.append(mm.deserialize("<NEWLINE>" + var26.getConfigValue(var10004)));
                        var26 = Lang.AUTORANK_TIMES_TOTAL;
                        var10004 = new Object[]{AutorankTools.timeToString(total, time)};
                        Component times_command = var10000.append(var10001.append(mm.deserialize("<NEWLINE>" + var26.getConfigValue(var10004))));
                        this.plugin.adventure().player((Player)sender).sendMessage(times_command);
                    }

                    if (order.equals("START_WITH")) {
                        Component var17 = mm.deserialize(Lang.AUTORANK_TIMES_HEADER.getConfigValue(playerName));
                        Lang var25 = Lang.AUTORANK_TIMES_PLAYER_PLAYED;
                        Object[] var30 = new Object[]{playerName};
                        Component var21 = mm.deserialize("<NEWLINE>" + var25.getConfigValue(var30));
                        Lang var31 = Lang.AUTORANK_TIMES_TODAY;
                        Object[] var38 = new Object[]{AutorankTools.timeStartToString(daily, time)};
                        var21 = var21.append(mm.deserialize("<NEWLINE>" + var31.getConfigValue(var38)));
                        var31 = Lang.AUTORANK_TIMES_THIS_WEEK;
                        var38 = new Object[]{AutorankTools.timeStartToString(weekly, time)};
                        var21 = var21.append(mm.deserialize("<NEWLINE>" + var31.getConfigValue(var38)));
                        var31 = Lang.AUTORANK_TIMES_THIS_MONTH;
                        var38 = new Object[]{AutorankTools.timeStartToString(monthly, time)};
                        var21 = var21.append(mm.deserialize("<NEWLINE>" + var31.getConfigValue(var38)));
                        var31 = Lang.AUTORANK_TIMES_TOTAL;
                        var38 = new Object[]{AutorankTools.timeStartToString(total, time)};
                        Component times_command = var17.append(var21.append(mm.deserialize("<NEWLINE>" + var31.getConfigValue(var38))));
                        this.plugin.adventure().player((Player)sender).sendMessage(times_command);
                    }
                }

            });
            this.runCommandTask(task);
            return true;
        }
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
