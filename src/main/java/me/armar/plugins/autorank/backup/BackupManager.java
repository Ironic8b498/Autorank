package me.armar.plugins.autorank.backup;

import com.google.common.io.Files;
import me.armar.plugins.autorank.Autorank;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class BackupManager {
    public static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private final BackupDataManager backupDataManager;
    private final Autorank plugin;

    public BackupManager(Autorank plugin) {
        this.plugin = plugin;
        this.backupDataManager = new BackupDataManager(plugin);
        this.backupDataManager.createNewFile();
    }

    public void backupFile(String sourceFileName, String storePath) {
        String folderPath = this.plugin.getDataFolder().getAbsolutePath() + File.separator;
        File sourceFile = new File(folderPath + sourceFileName);
        File copyFile = null;
        String dateFormatForFiles = dateFormat.format(new Date());
        if (storePath == null) {
            copyFile = new File(folderPath + sourceFileName.replace(".yml", "") + "-backup-" + dateFormatForFiles + ".yml");
        } else {
            copyFile = new File(storePath.replace(".yml", "") + "-backup-" + dateFormatForFiles + ".yml");
        }

        copyFile.getParentFile().mkdirs();

        try {
            Files.copy(sourceFile, copyFile);
            this.plugin.debugMessage("Made backup of '" + sourceFileName + "'!");
        } catch (IOException var8) {
            this.plugin.getServer().getConsoleSender().sendMessage("[Autorank] " + ChatColor.RED + "Was not able to back up " + sourceFileName + ", trying again in 24 hours.");
        }

    }

    public void backupDataFolders(String dataType) {
        if (dataType.equalsIgnoreCase("storage")) {
            this.plugin.debugMessage(ChatColor.GREEN + "Making a backup of all storage files!");
            this.plugin.getPlayTimeStorageManager().backupStorageProviders();
            this.backupDataManager.getConfig().set("storage", System.currentTimeMillis());
        } else if (dataType.equalsIgnoreCase("playerdata")) {
            this.plugin.debugMessage(ChatColor.GREEN + "Making a backup of PlayerData file!");
            this.plugin.getBackupManager().backupFile("/playerdata/PlayerData.yml", this.plugin.getDataFolder().getAbsolutePath() + File.separator + "backups" + File.separator + "PlayerData.yml");
            this.backupDataManager.getConfig().set("playerdata", System.currentTimeMillis());
        }

    }

    public void startBackupSystem() {
        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new Runnable() {
            public void run() {
                BackupManager.this.plugin.debugMessage("Running backup system to check if we need to backup the files.");
                if (System.currentTimeMillis() - BackupManager.this.backupDataManager.getLatestBackup("storage") > 86400000L) {
                    BackupManager.this.backupDataFolders("storage");
                } else {
                    BackupManager.this.plugin.debugMessage("Data files did not have to be backed up yet.");
                }

                if (System.currentTimeMillis() - BackupManager.this.backupDataManager.getLatestBackup("playerdata") > 86400000L) {
                    BackupManager.this.backupDataFolders("playerdata");
                } else {
                    BackupManager.this.plugin.debugMessage("Playerdata files did not have to be backed up yet.");
                }

                BackupManager.this.plugin.getPlayTimeStorageManager().getActiveStorageProviders().forEach((providerName) -> {
                    int deletedBackups = BackupManager.this.plugin.getPlayTimeStorageManager().getActiveStorageProvider(providerName).clearBackupsBeforeDate(LocalDate.now().minusDays(BackupManager.this.plugin.getSettingsConfig().getBackupRemovalTime()));
                    BackupManager.this.plugin.debugMessage("Deleted " + deletedBackups + " backups of " + providerName);
                });
                BackupManager.this.backupDataManager.saveConfig();
            }
        }, 0L, 1728000L);
    }
}
