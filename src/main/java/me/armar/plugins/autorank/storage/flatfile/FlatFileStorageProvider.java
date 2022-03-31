package me.armar.plugins.autorank.storage.flatfile;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.backup.BackupManager;
import me.armar.plugins.autorank.config.SimpleYamlConfiguration;
import me.armar.plugins.autorank.storage.PlayTimeStorageProvider;
import me.armar.plugins.autorank.storage.TimeType;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FlatFileStorageProvider extends PlayTimeStorageProvider {
    private final String pathTotalTimeFile = "/data/Total_time.yml";
    private final String pathDailyTimeFile = "/data/Daily_time.yml";
    private final String pathWeeklyTimeFile = "/data/Weekly_time.yml";
    private final String pathMonthlyTimeFile = "/data/Monthly_time.yml";
    private final Map<TimeType, String> dataTypePaths = new HashMap();
    private final Map<TimeType, SimpleYamlConfiguration> dataFiles = new HashMap();
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
        } else {
            if (timeType == TimeType.DAILY_TIME) {
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
    }

    public void addPlayerTime(TimeType timeType, UUID uuid, int timeToAdd) {
        int time = 0;
        this.plugin.debugMessage("Adding " + timeToAdd + " to " + uuid.toString() + " (" + timeType.name() + ")");

        try {
            time = this.getPlayerTime(timeType, uuid).get();
            this.plugin.debugMessage("Player " + uuid + " already has " + time + " for (" + timeType.name() + ")");
        } catch (ExecutionException | InterruptedException var6) {
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
        Iterator var6 = this.getStoredPlayers(TimeType.TOTAL_TIME).iterator();

        while(true) {
            while(var6.hasNext()) {
                UUID uuid = (UUID)var6.next();
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
    }

    public CompletableFuture<Integer> getNumberOfStoredPlayers(TimeType timeType) {
        return CompletableFuture.completedFuture(this.getStoredPlayers(timeType).size());
    }

    public List<UUID> getStoredPlayers(TimeType timeType) {
        List<UUID> uuids = new ArrayList();
        SimpleYamlConfiguration data = this.getDataFile(timeType);
        Iterator var4 = data.getKeys(false).iterator();

        while(var4.hasNext()) {
            String uuidString = (String)var4.next();
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
        Iterator var1 = this.dataFiles.entrySet().iterator();

        while(var1.hasNext()) {
            Entry<TimeType, SimpleYamlConfiguration> entry = (Entry)var1.next();
            entry.getValue().saveFile();
        }

    }

    public StorageType getStorageType() {
        return StorageType.FLAT_FILE;
    }

    public boolean canImportData() {
        return true;
    }

    public void importData() {
        SimpleYamlConfiguration data = this.getDataFile(TimeType.TOTAL_TIME);
        data.reloadFile();
    }

    public boolean canBackupData() {
        return true;
    }

    public boolean backupData() {
        Iterator var1 = this.dataTypePaths.entrySet().iterator();

        while(var1.hasNext()) {
            Entry<TimeType, String> entry = (Entry)var1.next();
            this.plugin.debugMessage("Making a backup of " + entry.getValue());
            this.plugin.getBackupManager().backupFile(entry.getValue(), this.plugin.getDataFolder().getAbsolutePath() + File.separator + "backups" + File.separator + entry.getValue().replace("/data/", ""));
        }

        return true;
    }

    public int clearBackupsBeforeDate(LocalDate date) {
        String backupsFolder = this.plugin.getDataFolder().getAbsolutePath() + File.separator + "backups";
        AtomicInteger deletedFiles = new AtomicInteger();

        try {
            Stream<Path> walk = Files.walk(Paths.get(backupsFolder));
            Throwable var5 = null;

            try {
                List<String> result = walk.filter((x$0) -> {
                    return Files.isRegularFile(x$0);
                }).map(Path::toString).collect(Collectors.toList());
                result.forEach((fileName) -> {
                    String fileDateString = fileName.replaceAll("[^\\d]", "");
                    Date fileDate = null;

                    try {
                        fileDate = BackupManager.dateFormat.parse(fileDateString);
                    } catch (ParseException var7) {
                    }

                    if (fileDate != null) {
                        if (fileDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(date)) {
                            try {
                                Files.deleteIfExists(Paths.get(fileName));
                                deletedFiles.getAndIncrement();
                            } catch (IOException var6) {
                            }

                        }
                    }
                });
            } catch (Throwable var15) {
                var5 = var15;
                throw var15;
            } finally {
                if (walk != null) {
                    if (var5 != null) {
                        try {
                            walk.close();
                        } catch (Throwable var14) {
                            var5.addSuppressed(var14);
                        }
                    } else {
                        walk.close();
                    }
                }

            }
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
        Iterator var4 = this.getStoredPlayers(TimeType.TOTAL_TIME).iterator();

        while(var4.hasNext()) {
            UUID uuid = (UUID)var4.next();
            int time = 0;

            try {
                time = this.getPlayerTime(TimeType.TOTAL_TIME, uuid).get();
            } catch (ExecutionException | InterruptedException var8) {
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
