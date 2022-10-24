package me.armar.plugins.autorank.backup;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class BackupDataManager {
    private FileConfiguration backupConfig;
    private File backupConfigFile;
    private final Autorank plugin;

    public BackupDataManager(Autorank autorank) {
        this.plugin = autorank;
    }

    public void createNewFile() {
        this.reloadConfig();
        this.saveConfig();
        this.loadConfig();
        this.plugin.debugMessage("Backup storage file loaded (backup-storage.yml)");
    }

    public FileConfiguration getConfig() {
        if (this.backupConfig == null) {
            this.reloadConfig();
        }

        return this.backupConfig;
    }

    public long getLatestBackup(String file) {
        return this.backupConfig.getLong(file, -1L);
    }

    public void loadConfig() {
        this.backupConfig.options().header("Backup-storage file\nDon't edit this file if you don't know what you are doing. \nThis file is used by Autorank to check when the latest backups were made.");
        this.backupConfig.addDefault("storage", 0);
        this.backupConfig.addDefault("playerdata", 0);
        this.backupConfig.options().copyDefaults(true);
        this.saveConfig();
    }

    public void reloadConfig() {
        if (this.backupConfigFile == null) {
            this.backupConfigFile = new File(this.plugin.getDataFolder().getAbsolutePath() + File.separator + "backups", "backup-storage.yml");
        }

        this.backupConfig = YamlConfiguration.loadConfiguration(this.backupConfigFile);
    }

    public void saveConfig() {
        if (this.backupConfig != null && this.backupConfigFile != null) {
            try {
                this.getConfig().save(this.backupConfigFile);
            } catch (IOException var2) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.backupConfigFile, var2);
            }

        }
    }
}
