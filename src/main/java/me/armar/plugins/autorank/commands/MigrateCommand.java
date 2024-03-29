package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.migration.MigrationManager;
import me.armar.plugins.autorank.migration.MigrationablePlugin;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MigrateCommand extends AutorankCommand {
    private final Autorank plugin;

    public MigrateCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)){
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT.getConfigValue());
            return true;
        }
        if (!this.hasPermission("autorank.migrate", sender)) {
            return true;
        } else if (args.length < 2) {
            AutorankTools.sendDeserialize(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        } else {
            MigrationManager.Migrationable migrationableType;
            try {
                migrationableType = MigrationManager.Migrationable.valueOf(args[1].toUpperCase().replace(" ", "_"));
            } catch (IllegalArgumentException var9) {
                AutorankTools.sendDeserialize(sender, Lang.THIS_IS_NOT.getConfigValue());
                return true;
            }

            MigrationablePlugin migrationablePlugin = this.plugin.getMigrationManager().getMigrationablePlugin(migrationableType).orElse(null);
            if (migrationablePlugin == null) {
                AutorankTools.sendDeserialize(sender, Lang.COULD_NOT_FIND.getConfigValue());
                return true;
            } else if (!migrationablePlugin.isReady()) {
                AutorankTools.sendDeserialize(sender, Lang.THIS_MIGRATION.getConfigValue());
                return true;
            } else {
                List<UUID> uuids = this.plugin.getPlayTimeStorageManager().getPrimaryStorageProvider().getStoredPlayers(TimeType.TOTAL_TIME);
                CompletableFuture<Void> task = migrationablePlugin.migratePlayTime(uuids).thenAccept((migratedPlayers) -> {
                    AutorankTools.sendDeserialize(sender, Lang.PLAYERS_HAVE_BEEN_MIGRATED.getConfigValue(migratedPlayers));
                });
                this.runCommandTask(task);
                return true;
            }
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return Arrays.stream(MigrationManager.Migrationable.values()).map(Enum::toString).filter((string) -> {
            return string.toLowerCase().startsWith(args[1].toLowerCase());
        }).collect(Collectors.toList());
    }

    public String getDescription() {
        return "Migrate play time data from another plugin to Autorank";
    }

    public String getPermission() {
        return "autorank.migrate";
    }

    public String getUsage() {
        return "/ar migrate <type>";
    }
}
