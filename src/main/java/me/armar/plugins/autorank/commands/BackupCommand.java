package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
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
        if (!(sender instanceof Player)){
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT.getConfigValue());
            return true;
        }
        if (!this.hasPermission(this.getPermission(), sender)) {
            return true;
        } else {
            if (args.length >= 2) {
                backupAll = false;
                fileToBackup = args[1].toLowerCase();
            }

            if (fileToBackup != null && !fileToBackup.equals("playerdata") && !fileToBackup.equals("storage")) {
                AutorankTools.sendDeserialize(sender, Lang.INVALID_STORAGE_FILE.getConfigValue());
                return true;
            } else {
                if (backupAll || fileToBackup.equals("playerdata")) {
                    this.plugin.getBackupManager().backupDataFolders("playerdata");
                    AutorankTools.sendDeserialize(sender, Lang.SUCCESSFULLY_CREATED_PLAYERDATA.getConfigValue());
                }

                if (backupAll || fileToBackup.equals("storage")) {
                    this.plugin.getBackupManager().backupDataFolders("storage");
                    AutorankTools.sendDeserialize(sender, Lang.SUCCESSFULLY_CREATED_STORAGE.getConfigValue());
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
