package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
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

public class AddCommand extends AutorankCommand {
    private final Autorank plugin;

    public AddCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        var mm = MiniMessage.miniMessage();
        if (!this.hasPermission("autorank.add", sender)) {
            return true;
        } else if (args.length < 3) {
            Component invalid_format = mm.deserialize(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            plugin.adventure().player((Player) sender).sendMessage(invalid_format);
            return true;
        } else {
            CompletableFuture<Void> task = UUIDManager.getUUID(args[1]).thenAccept((uuid) -> {
                if (uuid == null) {
                    Component unknown_player = mm.deserialize(Lang.UNKNOWN_PLAYER.getConfigValue(args[1]));
                    plugin.adventure().player((Player) sender).sendMessage(unknown_player);
                } else {
                    int value = AutorankTools.readTimeInput(args, 2);
                    if (value >= 0) {
                        this.plugin.getPlayTimeStorageManager().addPlayerTime(uuid, value);
                        String playerName = null;
                        int newPlayerTime = 0;

                        try {
                            playerName = UUIDManager.getPlayerName(uuid).get();
                            newPlayerTime = this.plugin.getPlayTimeManager().getPlayTime(TimeType.TOTAL_TIME, uuid).get();
                        } catch (ExecutionException | InterruptedException var8) {
                            var8.printStackTrace();
                        }

                        Component playtime_changed = mm.deserialize(Lang.PLAYTIME_CHANGED.getConfigValue(playerName, AutorankTools.timeToString(newPlayerTime, TimeUnit.MINUTES)));
                        plugin.adventure().player((Player) sender).sendMessage(playtime_changed);
                    } else {
                        Component invalid_format = mm.deserialize(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
                        plugin.adventure().player((Player) sender).sendMessage(invalid_format);
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
