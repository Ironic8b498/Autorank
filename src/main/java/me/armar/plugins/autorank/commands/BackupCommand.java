package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class BackupCommand extends AutorankCommand {
    private final Autorank plugin;

    public BackupCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean backupAll = true;
        String fileToBackup = null;
        if (!this.hasPermission(this.getPermission(), sender)) {
            return true;
        } else {
            if (args.length >= 2) {
                backupAll = false;
                fileToBackup = args[1].toLowerCase();
            }

            if (fileToBackup != null && !fileToBackup.equals("playerdata") && !fileToBackup.equals("storage")) {
                sender.sendMessage(ChatColor.RED + Lang.INVALID_STORAGE_FILE.getConfigValue());
                return true;
            } else {
                if (backupAll || fileToBackup.equals("playerdata")) {
                    this.plugin.getBackupManager().backupDataFolders("playerdata");
                    sender.sendMessage(ChatColor.GREEN + Lang.SUCCESSFULLY_CREATED_PLAYERDATA.getConfigValue());
                }

                if (backupAll || fileToBackup.equals("storage")) {
                    this.plugin.getBackupManager().backupDataFolders("storage");
                    sender.sendMessage(ChatColor.GREEN + Lang.SUCCESSFULLY_CREATED_STORAGE.getConfigValue());
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
