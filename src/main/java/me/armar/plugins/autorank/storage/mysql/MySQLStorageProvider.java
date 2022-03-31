package me.armar.plugins.autorank.storage.mysql;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.config.SettingsConfig;
import me.armar.plugins.autorank.config.SettingsConfig.MySQLSettings;
import me.armar.plugins.autorank.storage.PlayTimeStorageProvider;
import me.armar.plugins.autorank.storage.TimeType;
import org.bukkit.ChatColor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

public class MySQLStorageProvider extends PlayTimeStorageProvider {
    public static int CACHE_EXPIRY_TIME = 2;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Map<TimeType, String> tableNames = new HashMap();
    private final CacheManager cacheManager = new CacheManager();
    private SQLConnection mysqlLibrary;
    private boolean isLoaded = false;

    public MySQLStorageProvider(Autorank instance) {
        super(instance);
        CACHE_EXPIRY_TIME = this.plugin.getSettingsConfig().getIntervalTime();
        instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, () -> {
            Set<UUID> cachedUUIDs = this.cacheManager.getCachedUUIDs();
            Iterator var2 = cachedUUIDs.iterator();

            while(var2.hasNext()) {
                UUID cachedUUID = (UUID)var2.next();
                TimeType[] var4 = TimeType.values();
                int var5 = var4.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    TimeType timeType = var4[var6];
                    if (this.cacheManager.shouldUpdateCachedEntry(timeType, cachedUUID)) {
                        this.plugin.debugMessage("Refreshing cached global time of " + cachedUUID);
                        int playTime = 0;

                        try {
                            playTime = this.getFreshPlayerTime(timeType, cachedUUID).get();
                        } catch (ExecutionException | InterruptedException var10) {
                            var10.printStackTrace();
                        }

                        this.cacheManager.registerCachedTime(timeType, cachedUUID, playTime);
                    }
                }
            }

        }, 1200L, CACHE_EXPIRY_TIME * 1200 / 2);
    }

    public StorageType getStorageType() {
        return StorageType.DATABASE;
    }

    public void setPlayerTime(TimeType timeType, UUID uuid, int time) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.plugin.debugMessage("Setting time (" + timeType + ") of '" + uuid.toString() + "' to " + time);
            if (this.mysqlLibrary.isClosed()) {
                this.mysqlLibrary.connect();
            }

            String tableName = this.tableNames.get(timeType);
            this.plugin.getLoggerManager().logMessage("Setting (MySQL) " + timeType.name() + " of " + uuid + " to: " + time);
            String statement = "INSERT INTO " + tableName + " VALUES ('" + uuid + "', " + time + ", CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE time=" + time;
            this.cacheManager.registerCachedTime(timeType, uuid, time);
            this.mysqlLibrary.execute(statement);
        });
    }

    public CompletableFuture<Integer> getPlayerTime(TimeType timeType, UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            int freshPlayerTime;
            if (this.cacheManager.hasCachedTime(timeType, uuid)) {
                this.plugin.debugMessage("Getting cached time (" + timeType + ") of '" + uuid.toString() + "'");
                freshPlayerTime = this.cacheManager.getCachedTime(timeType, uuid);
                this.plugin.getLoggerManager().logMessage("Retrieved cached time (MySQL) " + timeType.name() + " of " + uuid + ": " + freshPlayerTime + " minutes");
                return freshPlayerTime;
            } else {
                try {
                    freshPlayerTime = this.getFreshPlayerTime(timeType, uuid).get();
                    this.plugin.getLoggerManager().logMessage("Retrieved fresh time (MySQL) " + timeType.name() + " of " + uuid.toString() + ": " + freshPlayerTime + " minutes");
                    return freshPlayerTime;
                } catch (ExecutionException | InterruptedException var4) {
                    var4.printStackTrace();
                    this.plugin.getLoggerManager().logMessage("Couldn't retrieve data (MySQL) " + timeType.name() + " of " + uuid.toString());
                    return 0;
                }
            }
        });
    }

    public void resetData(TimeType timeType) {
        String tableName = this.tableNames.get(timeType);
        String statement = "TRUNCATE TABLE " + tableName;
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.mysqlLibrary.execute(statement);
        });
    }

    public void addPlayerTime(TimeType timeType, UUID uuid, int timeToAdd) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.plugin.debugMessage("Adding " + timeToAdd + " minutes of (" + timeType + ") to '" + uuid.toString() + "'");
            if (this.mysqlLibrary.isClosed()) {
                this.mysqlLibrary.connect();
            }

            String tableName = this.tableNames.get(timeType);
            this.plugin.getLoggerManager().logMessage("Adding (MySQL) " + timeType.name() + " of " + uuid + " time: " + timeToAdd + " minutes");
            String statement = "INSERT INTO " + tableName + " VALUES ('" + uuid + "', " + timeToAdd + ", CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE time=time+" + timeToAdd;
            this.mysqlLibrary.execute(statement);
        });
    }

    public String getName() {
        return "MySQLStorageProvider";
    }

    public CompletableFuture<Boolean> initialiseProvider() {
        return CompletableFuture.supplyAsync(() -> {
            this.loadTableNames();

            try {
                if (!(Boolean)this.loadMySQLVariables().get()) {
                    return false;
                }
            } catch (ExecutionException | InterruptedException var2) {
                var2.printStackTrace();
                return false;
            }

            if (this.mysqlLibrary == null) {
                return false;
            } else {
                this.createTables();
                this.isLoaded = true;
                return true;
            }
        });
    }

    public int purgeOldEntries(int threshold) {
        return 0;
    }

    public CompletableFuture<Integer> getNumberOfStoredPlayers(TimeType timeType) {
        return CompletableFuture.supplyAsync(() -> {
            String tableName = this.tableNames.get(timeType);
            String statement = "SELECT COUNT(uuid) FROM " + tableName;
            Optional<ResultSet> rs = this.mysqlLibrary.executeQuery(statement);
            if (!rs.isPresent()) {
                return 0;
            } else {
                try {
                    if (rs.get().next()) {
                        return rs.get().getInt(1);
                    }
                } catch (SQLException var6) {
                    System.out.println("SQLException: " + var6.getMessage());
                    System.out.println("SQLState: " + var6.getSQLState());
                    System.out.println("VendorError: " + var6.getErrorCode());
                }

                return 0;
            }
        });
    }

    public List<UUID> getStoredPlayers(TimeType timeType) {
        List<UUID> uuids = new ArrayList();
        String tableName = this.tableNames.get(timeType);
        String statement = "SELECT uuid FROM " + tableName;
        Optional<ResultSet> rs = this.mysqlLibrary.executeQuery(statement);
        if (!rs.isPresent()) {
            return uuids;
        } else {
            try {
                while(rs.get().next()) {
                    String uuidString = rs.get().getString("uuid");
                    uuids.add(UUID.fromString(uuidString));
                }
            } catch (SQLException var7) {
                System.out.println("SQLException: " + var7.getMessage());
                System.out.println("SQLState: " + var7.getSQLState());
                System.out.println("VendorError: " + var7.getErrorCode());
            }

            return uuids;
        }
    }

    public void saveData() {
    }

    public boolean canImportData() {
        return false;
    }

    public void importData() {
    }

    public boolean canBackupData() {
        return true;
    }

    public boolean backupData() {
        List<String> statements = new ArrayList();
        DateFormat df = new SimpleDateFormat("yyyy_MM_dd HH_mm_ss");
        Iterator var3 = this.tableNames.entrySet().iterator();

        while(var3.hasNext()) {
            Entry<TimeType, String> entry = (Entry)var3.next();
            String tableName = entry.getValue();
            String backupTableName = tableName + "_backup_" + df.format(new Date());
            statements.add(String.format("CREATE TABLE `%1$s` LIKE `%2$s`;", backupTableName, tableName));
            statements.add(String.format("INSERT INTO `%1$s` SELECT * FROM `%2$s`;", backupTableName, tableName));
        }

        this.mysqlLibrary.executeQueries(statements);
        return true;
    }

    public int clearBackupsBeforeDate(LocalDate date) {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        List<String> tablesToDelete = new ArrayList();
        Optional<ResultSet> optionalResultSet = this.mysqlLibrary.executeQuery("SHOW TABLES;");
        if (!optionalResultSet.isPresent()) {
            return 0;
        } else {
            try {
                ResultSet resultSet = optionalResultSet.get();
                Throwable var6 = null;

                try {
                    while(resultSet.next()) {
                        String tableName = resultSet.getString(1);
                        String fileDateString = tableName.replaceAll("[^\\d]", "");
                        Date fileDate = null;

                        try {
                            fileDate = df.parse(fileDateString);
                        } catch (ParseException var20) {
                        }

                        if (fileDate != null && fileDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(date)) {
                            tablesToDelete.add(tableName);
                        }
                    }

                    tablesToDelete.forEach((tableNamex) -> {
                        this.mysqlLibrary.execute("DROP TABLE `" + tableNamex + "`;");
                    });
                } catch (Throwable var21) {
                    var6 = var21;
                    throw var21;
                } finally {
                    if (resultSet != null) {
                        if (var6 != null) {
                            try {
                                resultSet.close();
                            } catch (Throwable var19) {
                                var6.addSuppressed(var19);
                            }
                        } else {
                            resultSet.close();
                        }
                    }

                }
            } catch (SQLException var23) {
                var23.printStackTrace();
            }

            return tablesToDelete.size();
        }
    }

    public boolean isLoaded() {
        return this.isLoaded;
    }

    private void loadTableNames() {
        String prefix = this.plugin.getSettingsConfig().getMySQLSetting(MySQLSettings.TABLE_PREFIX);
        this.tableNames.put(TimeType.TOTAL_TIME, prefix + "totalTime");
        this.tableNames.put(TimeType.DAILY_TIME, prefix + "dailyTime");
        this.tableNames.put(TimeType.WEEKLY_TIME, prefix + "weeklyTime");
        this.tableNames.put(TimeType.MONTHLY_TIME, prefix + "monthlyTime");
    }

    private CompletableFuture<Boolean> loadMySQLVariables() {
        return CompletableFuture.supplyAsync(() -> {
            SettingsConfig configHandler = this.plugin.getSettingsConfig();
            if (!configHandler.useMySQL()) {
                this.plugin.getLogger().warning("Autorank is trying to register a MySQL storage provider, but MySQL is disabled in the settings file!");
                return false;
            } else {
                this.mysqlLibrary = SQLConnection.getInstance(configHandler);
                if (!this.mysqlLibrary.connect()) {
                    this.mysqlLibrary = null;
                    this.plugin.getLogger().severe("Could not connect to MySQL!");
                    this.plugin.debugMessage(ChatColor.RED + "Could not connect to MySQL!");
                    return false;
                } else {
                    this.plugin.debugMessage(ChatColor.RED + "Successfully established connection to MySQL");
                    return true;
                }
            }
        });
    }

    private void createTables() {
        if (this.mysqlLibrary.isClosed()) {
            this.mysqlLibrary.connect();
        }

        Iterator var1 = this.tableNames.entrySet().iterator();

        while(var1.hasNext()) {
            Entry<TimeType, String> entry = (Entry)var1.next();
            String statement = "CREATE TABLE IF NOT EXISTS " + entry.getValue() + " (uuid VARCHAR(40) not NULL,  time INTEGER not NULL,  modified TIMESTAMP not NULL,  PRIMARY KEY ( uuid ))";
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                this.mysqlLibrary.execute(statement);
            });
        }

    }

    private CompletableFuture<Integer> getFreshPlayerTime(TimeType timeType, UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            if (this.mysqlLibrary.isClosed()) {
                this.mysqlLibrary.connect();
            }

            String tableName = this.tableNames.get(timeType);
            int time = 0;
            String statement = "SELECT * FROM " + tableName + " WHERE uuid='" + uuid.toString() + "'";
            Optional<ResultSet> optionalResultSet = this.mysqlLibrary.executeQuery(statement);
            if (!optionalResultSet.isPresent()) {
                return time;
            } else {
                try {
                    ResultSet rs = optionalResultSet.get();
                    Throwable var8 = null;

                    try {
                        if (rs.next()) {
                            time = rs.getInt(2);
                            rs.close();
                        }
                    } catch (Throwable var18) {
                        var8 = var18;
                        throw var18;
                    } finally {
                        if (rs != null) {
                            if (var8 != null) {
                                try {
                                    rs.close();
                                } catch (Throwable var17) {
                                    var8.addSuppressed(var17);
                                }
                            } else {
                                rs.close();
                            }
                        }

                    }
                } catch (SQLException var20) {
                    System.out.println("SQLException: " + var20.getMessage());
                    System.out.println("SQLState: " + var20.getSQLState());
                    System.out.println("VendorError: " + var20.getErrorCode());
                }

                this.plugin.getLoggerManager().logMessage("Fetched fresh (MySQL) " + timeType.name() + " of " + uuid + ": " + time + " minutes");
                this.cacheManager.registerCachedTime(timeType, uuid, time);
                this.plugin.debugMessage("(" + (Thread.currentThread().getName().contains("Server thread") ? "not async" : "async") + ") Obtained fresh global time (" + timeType + ") of '" + uuid + "' with value " + time);
                return time;
            }
        });
    }

    private void disconnectDatabase() {
        this.executor.shutdown();

        try {
            this.plugin.debugMessage(ChatColor.RED + "Awaiting termination of MySQL thread...");
            this.executor.awaitTermination(30L, TimeUnit.SECONDS);
        } catch (InterruptedException var2) {
            this.plugin.getLogger().warning("Failed to await termination of thread pool. Interrupted.");
        }

        if (this.mysqlLibrary != null) {
            this.mysqlLibrary.closeConnection();
        }

    }

    public void updateFromOldTables() {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            public void run() {
                List<String> adjustedTables = new ArrayList();
                String statement = "SHOW TABLES LIKE 'null%'";
                Optional<ResultSet> rs = MySQLStorageProvider.this.mysqlLibrary.executeQuery(statement);
                MySQLStorageProvider.this.plugin.debugMessage("Looking for old data in MySQL database that might be useful.");
                if (rs.isPresent()) {
                    try {
                        String foundTableName;
                        while(rs.get().next()) {
                            ResultSet set = rs.get();
                            foundTableName = null;

                            try {
                                foundTableName = set.getString(1);
                            } catch (SQLException var26) {
                                continue;
                            }

                            if (foundTableName != null) {
                                MySQLStorageProvider.this.plugin.debugMessage("Found table " + foundTableName + " that might have old data.");
                                String readOldTableStatement = "SELECT * FROM `" + foundTableName + "`";
                                ResultSet oldTableData = null;
                                if (!foundTableName.toLowerCase(Locale.ROOT).contains("imported") && !foundTableName.toLowerCase(Locale.ROOT).contains("backup")) {
                                    if (!foundTableName.equalsIgnoreCase("nulldailyTime") && !foundTableName.equalsIgnoreCase("nullweeklyTime") && !foundTableName.equalsIgnoreCase("nullmonthlyTime") && !foundTableName.equalsIgnoreCase("nulltotalTime")) {
                                        MySQLStorageProvider.this.plugin.debugMessage("Skipping table " + foundTableName + ".");
                                    } else {
                                        Optional<ResultSet> innerResult = MySQLStorageProvider.this.mysqlLibrary.executeQuery(readOldTableStatement);
                                        MySQLStorageProvider.this.plugin.debugMessage("Loading old data of " + foundTableName);
                                        if (!innerResult.isPresent()) {
                                            continue;
                                        }

                                        oldTableData = innerResult.get();
                                    }

                                    if (oldTableData != null) {
                                        adjustedTables.add(foundTableName);
                                        int count = 0;

                                        while(oldTableData.next()) {
                                            String uuidString = null;

                                            try {
                                                uuidString = oldTableData.getString("uuid");
                                            } catch (SQLException var29) {
                                                continue;
                                            }

                                            if (uuidString != null) {
                                                UUID uuid;
                                                try {
                                                    uuid = UUID.fromString(uuidString);
                                                } catch (IllegalArgumentException var28) {
                                                    continue;
                                                }

                                                boolean var12 = false;

                                                int minutes;
                                                try {
                                                    minutes = oldTableData.getInt("time");
                                                } catch (SQLException var27) {
                                                    continue;
                                                }

                                                ++count;
                                                if (foundTableName.toLowerCase(Locale.ROOT).contains("daily")) {
                                                    MySQLStorageProvider.this.plugin.getPlayTimeManager().addGlobalPlayTime(TimeType.DAILY_TIME, uuid, minutes);
                                                    MySQLStorageProvider.this.plugin.getPlayTimeManager().addLocalPlayTime(TimeType.DAILY_TIME, uuid, minutes);
                                                } else if (foundTableName.toLowerCase(Locale.ROOT).contains("weekly")) {
                                                    MySQLStorageProvider.this.plugin.getPlayTimeManager().addGlobalPlayTime(TimeType.WEEKLY_TIME, uuid, minutes);
                                                    MySQLStorageProvider.this.plugin.getPlayTimeManager().addLocalPlayTime(TimeType.WEEKLY_TIME, uuid, minutes);
                                                } else if (foundTableName.toLowerCase(Locale.ROOT).contains("monthly")) {
                                                    MySQLStorageProvider.this.plugin.getPlayTimeManager().addGlobalPlayTime(TimeType.MONTHLY_TIME, uuid, minutes);
                                                    MySQLStorageProvider.this.plugin.getPlayTimeManager().addLocalPlayTime(TimeType.MONTHLY_TIME, uuid, minutes);
                                                } else {
                                                    MySQLStorageProvider.this.plugin.getPlayTimeManager().addGlobalPlayTime(TimeType.TOTAL_TIME, uuid, minutes);
                                                    MySQLStorageProvider.this.plugin.getPlayTimeManager().addLocalPlayTime(TimeType.TOTAL_TIME, uuid, minutes);
                                                }
                                            }
                                        }

                                        MySQLStorageProvider.this.plugin.debugMessage("Restored " + count + " rows of player time for table " + foundTableName);
                                        MySQLStorageProvider.this.plugin.getLoggerManager().logMessage("Restored " + count + " rows of player time for table " + foundTableName);
                                        oldTableData.close();
                                    }
                                }
                            }
                        }

                        Iterator var32 = adjustedTables.iterator();

                        while(var32.hasNext()) {
                            foundTableName = (String)var32.next();
                            MySQLStorageProvider.this.plugin.debugMessage("Renaming table " + foundTableName + " to IMPORTED_" + foundTableName + " so it's not imported again.");
                            MySQLStorageProvider.this.plugin.getLoggerManager().logMessage("Renaming table " + foundTableName + " to IMPORTED_" + foundTableName + " so it's not imported again.");
                            MySQLStorageProvider.this.mysqlLibrary.execute("RENAME TABLE " + foundTableName + " TO IMPORTED_" + foundTableName);
                        }
                    } catch (SQLException var30) {
                        System.out.println("SQLException: " + var30.getMessage());
                        System.out.println("SQLState: " + var30.getSQLState());
                        System.out.println("VendorError: " + var30.getErrorCode());
                    } finally {
                        try {
                            rs.get().close();
                        } catch (SQLException var25) {
                            var25.printStackTrace();
                        }

                    }

                }
            }
        });
    }
}
