package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackupCommand extends AutorankCommand {
    private final Autorank plugin;

    public BackupCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean backupAll = true;
        String fileToBackup = null;
        var mm = MiniMessage.miniMessage();
        if (!this.hasPermission(this.getPermission(), sender)) {
            return true;
        } else {
            if (args.length >= 2) {
                backupAll = false;
                fileToBackup = args[1].toLowerCase();
            }

            if (fileToBackup != null && !fileToBackup.equals("playerdata") && !fileToBackup.equals("storage")) {
                Component invalid_stoage_file = mm.deserialize(Lang.INVALID_STORAGE_FILE.getConfigValue());
                plugin.adventure().player((Player) sender).sendMessage(invalid_stoage_file);
                return true;
            } else {
                if (backupAll || fileToBackup.equals("playerdata")) {
                    this.plugin.getBackupManager().backupDataFolders("playerdata");
                    Component successfully_created_playerdata = mm.deserialize(Lang.SUCCESSFULLY_CREATED_PLAYERDATA.getConfigValue());
                    plugin.adventure().player((Player) sender).sendMessage(successfully_created_playerdata);
                }

                if (backupAll || fileToBackup.equals("storage")) {
                    this.plugin.getBackupManager().backupDataFolders("storage");
                    Component successfully_created_storage = mm.deserialize(Lang.SUCCESSFULLY_CREATED_STORAGE.getConfigValue());
                    plugin.adventure().player((Player) sender).sendMessage(successfully_created_storage);
                }

                return true;
            }
        }
    }

    public String getDescription() {
        return "Backup files with playerdata and/or regular storage.";
    }

    public String getPermission() {
        return "autorank.backup.storage";
    }

    public String getUsage() {
        return "/ar backup <file>";
    }
}
