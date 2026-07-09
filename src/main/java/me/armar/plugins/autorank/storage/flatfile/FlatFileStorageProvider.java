package me.armar.plugins.autorank.storage.flatfile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.backup.BackupManager;
import me.armar.plugins.autorank.config.SimpleYamlConfiguration;
import me.armar.plugins.autorank.storage.PlayTimeStorageProvider;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.storage.PlayTimeStorageProvider.StorageType;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;

public class FlatFileStorageProvider extends PlayTimeStorageProvider {
    private final String pathTotalTimeFile = "/data/Total_time.yml";
    private final String pathDailyTimeFile = "/data/Daily_time.yml";
    private final String pathWeeklyTimeFile = "/data/Weekly_time.yml";
    private final String pathMonthlyTimeFile = "/data/Monthly_time.yml";
    private final Map<TimeType, String> dataTypePaths = new HashMap<>();
    private final Map<TimeType, SimpleYamlConfiguration> dataFiles = new HashMap<>();
    private boolean isLoaded = false;

    public FlatFileStorageProvider(Autorank instance) {
        super(instance);
    }

    public void setPlayerTime(TimeType timeType, UUID uuid, int time) {
        this.plugin.debugMessage("Setting time of " + uuid.toString() + " to " + time + " (" + timeType.name() + ").");
        this.plugin.getLoggerManager().logMessage("Setting (Flatfile) " + timeType.name() + " of " + uuid + " to: " + time);
        SimpleYamlConfiguration data = this.getDataFile(timeType);
        data.set(uuid.toString(), time);
    }

    public CompletableFuture<Integer> getPlayerTime(TimeType timeType, UUID uuid) {
        SimpleYamlConfiguration data = this.getDataFile(timeType);
        return CompletableFuture.completedFuture(data.getInt(uuid.toString(), 0));
    }

    public void resetData(TimeType timeType) {
        SimpleYamlConfiguration data = this.getDataFile(timeType);
        this.plugin.debugMessage("Resetting storage file '" + timeType + "'!");
        boolean deleted = data.getInternalFile().delete();
        if (!deleted) {
            this.plugin.debugMessage("Tried deleting storage file, but could not delete!");
        } else if (timeType == TimeType.DAILY_TIME) {
            this.plugin.getLoggerManager().logMessage("Resetting daily time file");

            try {
                this.dataFiles.put(TimeType.DAILY_TIME, new SimpleYamlConfiguration(this.plugin, this.dataTypePaths.get(TimeType.DAILY_TIME), "Daily storage"));
            } catch (InvalidConfigurationException var8) {
                var8.printStackTrace();
            }
        } else if (timeType == TimeType.WEEKLY_TIME) {
            this.plugin.getLoggerManager().logMessage("Resetting weekly time file");

            try {
                this.dataFiles.put(TimeType.WEEKLY_TIME, new SimpleYamlConfiguration(this.plugin, this.dataTypePaths.get(TimeType.WEEKLY_TIME), "Weekly storage"));
            } catch (InvalidConfigurationException var7) {
                var7.printStackTrace();
            }
        } else if (timeType == TimeType.MONTHLY_TIME) {
            this.plugin.getLoggerManager().logMessage("Resetting monthly time file");

            try {
                this.dataFiles.put(TimeType.MONTHLY_TIME, new SimpleYamlConfiguration(this.plugin, this.dataTypePaths.get(TimeType.MONTHLY_TIME), "Monthly storage"));
            } catch (InvalidConfigurationException var6) {
                var6.printStackTrace();
            }
        } else if (timeType == TimeType.TOTAL_TIME) {
            this.plugin.getLoggerManager().logMessage("Resetting total time file");

            try {
                this.dataFiles.put(TimeType.TOTAL_TIME, new SimpleYamlConfiguration(this.plugin, this.dataTypePaths.get(TimeType.TOTAL_TIME), "Total storage"));
            } catch (InvalidConfigurationException var5) {
                var5.printStackTrace();
            }
        }

    }

    public void addPlayerTime(TimeType timeType, UUID uuid, int timeToAdd) {
        int time = 0;
        this.plugin.debugMessage("Adding " + timeToAdd + " to " + uuid.toString() + " (" + timeType.name() + ")");

        try {
            time = this.getPlayerTime(timeType, uuid).get();
            this.plugin.debugMessage("Player " + uuid + " already has " + time + " for (" + timeType.name() + ")");
        } catch (InterruptedException | ExecutionException var6) {
            var6.printStackTrace();
        }

        if (time < 0) {
            time = 0;
        }

        this.plugin.debugMessage("New time of " + uuid + " will be " + (time + timeToAdd) + " (" + timeType.name() + ")");
        this.setPlayerTime(timeType, uuid, time + timeToAdd);
    }

    public String getName() {
        return "FlatFileStorageProvider";
    }

    public CompletableFuture<Boolean> initialiseProvider() {
        return CompletableFuture.supplyAsync(() -> {
            this.loadDataFiles();
            this.registerTasks();
            this.isLoaded = true;
            return true;
        });
    }

    public int purgeOldEntries(int threshold) {
        int entriesRemoved = 0;
        SimpleYamlConfiguration data = this.getDataFile(TimeType.TOTAL_TIME);
        long currentTime = System.currentTimeMillis();

        for(UUID uuid : this.getStoredPlayers(TimeType.TOTAL_TIME)) {
            OfflinePlayer offPlayer = this.plugin.getServer().getOfflinePlayer(uuid);
            if (offPlayer.getName() == null) {
                data.set(uuid.toString(), null);
                ++entriesRemoved;
            } else {
                long lastPlayed = offPlayer.getLastPlayed();
                if (lastPlayed <= 0L || (currentTime - lastPlayed) / 86400000L >= (long)threshold) {
                    data.set(uuid.toString(), null);
                    ++entriesRemoved;
                }
            }
        }

        return entriesRemoved;
    }

    public CompletableFuture<Integer> getNumberOfStoredPlayers(TimeType timeType) {
        return CompletableFuture.completedFuture(this.getStoredPlayers(timeType).size());
    }

    public List<UUID> getStoredPlayers(TimeType timeType) {
        List<UUID> uuids = new ArrayList<>();
        SimpleYamlConfiguration data = this.getDataFile(timeType);

        for(String uuidString : data.getKeys(false)) {
            UUID uuid = null;

            try {
                uuid = UUID.fromString(uuidString);
            } catch (IllegalArgumentException var8) {
                continue;
            }

            uuids.add(uuid);
        }

        return uuids;
    }

    public void saveData() {
        for(Map.Entry<TimeType, SimpleYamlConfiguration> entry : this.dataFiles.entrySet()) {
            entry.getValue().saveFile();
        }

    }

    public PlayTimeStorageProvider.StorageType getStorageType() {
        return StorageType.FLAT_FILE;
    }

    public boolean canImportData() {
        return true;
    }

    public void importData() {
        SimpleYamlConfiguration data = this.getDataFile(TimeType.TOTAL_TIME);
        try {
            data.reloadFile();
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean canBackupData() {
        return true;
    }

    public boolean backupData() {
        for(Map.Entry<TimeType, String> entry : this.dataTypePaths.entrySet()) {
            this.plugin.debugMessage("Making a backup of " + entry.getValue());
            BackupManager var10000 = this.plugin.getBackupManager();
            String var10001 = entry.getValue();
            String var10002 = this.plugin.getDataFolder().getAbsolutePath();
            var10000.backupFile(var10001, var10002 + File.separator + "backups" + File.separator + entry.getValue().replace("/data/", ""));
        }

        return true;
    }

    public int clearBackupsBeforeDate(LocalDate date) {
        String var10000 = this.plugin.getDataFolder().getAbsolutePath();
        String backupsFolder = var10000 + File.separator + "backups";
        AtomicInteger deletedFiles = new AtomicInteger();

        try (Stream<Path> walk = Files.walk(Paths.get(backupsFolder))) {
            List<String> result = walk.filter(Files::isRegularFile).map(Path::toString).toList();
            result.forEach((fileName) -> {
                String fileDateString = fileName.replaceAll("[^\\d]", "");
                Date fileDate = null;

                try {
                    fileDate = BackupManager.dateFormat.parse(fileDateString);
                } catch (ParseException ignored) {
                }

                if (fileDate != null && fileDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(date)) {
                    try {
                        Files.deleteIfExists(Paths.get(fileName));
                        deletedFiles.getAndIncrement();
                    } catch (IOException ignored) {
                    }
                }

            });
        } catch (IOException var17) {
            var17.printStackTrace();
        }

        return deletedFiles.get();
    }

    public boolean isLoaded() {
        return this.isLoaded;
    }

    private SimpleYamlConfiguration getDataFile(TimeType type) {
        return this.dataFiles.get(type);
    }

    private void loadDataFiles() {
        this.dataTypePaths.put(TimeType.TOTAL_TIME, "/data/Total_time.yml");
        this.dataTypePaths.put(TimeType.DAILY_TIME, "/data/Daily_time.yml");
        this.dataTypePaths.put(TimeType.WEEKLY_TIME, "/data/Weekly_time.yml");
        this.dataTypePaths.put(TimeType.MONTHLY_TIME, "/data/Monthly_time.yml");

        try {
            this.dataFiles.put(TimeType.TOTAL_TIME, new SimpleYamlConfiguration(this.plugin, this.dataTypePaths.get(TimeType.TOTAL_TIME), "Total storage"));
            this.dataFiles.put(TimeType.DAILY_TIME, new SimpleYamlConfiguration(this.plugin, this.dataTypePaths.get(TimeType.DAILY_TIME), "Daily storage"));
            this.dataFiles.put(TimeType.WEEKLY_TIME, new SimpleYamlConfiguration(this.plugin, this.dataTypePaths.get(TimeType.WEEKLY_TIME), "Weekly storage"));
            this.dataFiles.put(TimeType.MONTHLY_TIME, new SimpleYamlConfiguration(this.plugin, this.dataTypePaths.get(TimeType.MONTHLY_TIME), "Monthly storage"));
        } catch (InvalidConfigurationException var2) {
            var2.printStackTrace();
        }

    }

    private void registerTasks() {
        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new Runnable() {
            public void run() {
                FlatFileStorageProvider.this.plugin.debugMessage("Periodically saving all flatfile storage files.");
                FlatFileStorageProvider.this.saveData();
            }
        }, 20L, 1200L);
    }

    private int archive(int minimum) {
        int counter = 0;
        SimpleYamlConfiguration data = this.getDataFile(TimeType.TOTAL_TIME);

        for(UUID uuid : this.getStoredPlayers(TimeType.TOTAL_TIME)) {
            int time = 0;

            try {
                time = this.getPlayerTime(TimeType.TOTAL_TIME, uuid).get();
            } catch (InterruptedException | ExecutionException var8) {
                var8.printStackTrace();
            }

            if (time < minimum) {
                ++counter;
                data.set(uuid.toString(), null);
            }
        }

        this.saveData();
        return counter;
    }
}
