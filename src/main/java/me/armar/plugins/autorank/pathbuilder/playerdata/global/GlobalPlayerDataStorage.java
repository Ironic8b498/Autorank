package me.armar.plugins.autorank.pathbuilder.playerdata.global;

import io.reactivex.annotations.NonNull;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.config.SettingsConfig;
import me.armar.plugins.autorank.config.SettingsConfig.MySQLSettings;
import me.armar.plugins.autorank.pathbuilder.playerdata.PlayerDataManager.PlayerDataStorageType;
import me.armar.plugins.autorank.pathbuilder.playerdata.PlayerDataStorage;
import me.armar.plugins.autorank.storage.mysql.SQLConnection;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GlobalPlayerDataStorage implements PlayerDataStorage {
    private final String tablePlayerdataStorageCompletedPaths;
    private final String tableServerRegister;
    private final Autorank plugin;
    private SQLConnection connection;
    private final PlayerDataCache playerDataCache = new PlayerDataCache();

    public GlobalPlayerDataStorage(Autorank instance) {
        this.plugin = instance;
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                boolean loadedDatabase = this.loadDatabaseConnection().get();
                if (loadedDatabase) {
                    this.loadServerRegister();
                    this.loadPlayerData();
                }
            } catch (ExecutionException | InterruptedException var2) {
                var2.printStackTrace();
            }

        });
        String prefix = this.plugin.getSettingsConfig().getMySQLSetting(MySQLSettings.TABLE_PREFIX);
        this.tablePlayerdataStorageCompletedPaths = prefix + "playerdata_completed_paths";
        this.tableServerRegister = prefix + "servers";
        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
            if (this.getConnection() != null && !this.getConnection().isClosed()) {
                this.updateCacheFromRemote();
            }

        }, 600L, 600L);
    }

    @NotNull
    private CompletableFuture<Boolean> loadDatabaseConnection() {
        return CompletableFuture.supplyAsync(() -> {
            SettingsConfig configHandler = this.plugin.getSettingsConfig();
            if (!configHandler.useMySQL()) {
                this.plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Can't load MySQL database, as you've disabled the MySQL server.");
                return false;
            } else {
                this.connection = SQLConnection.getInstance(configHandler);
                if (this.connection.connect()) {
                    this.plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Successfully attached to your MySQL database to retrieve playerdata");
                    return true;
                } else {
                    this.plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not attach to your MySQL database to retrieve playerdata");
                    this.plugin.getWarningManager().registerWarning("Could not attach to your MySQL database to retrieve playerdata", 10);
                    return false;
                }
            }
        });
    }

    private SQLConnection getConnection() {
        return this.connection;
    }

    private void loadServerRegister() {
        this.getConnection().execute("CREATE TABLE IF NOT EXISTS " + this.tableServerRegister + "(server_name varchar(36) NOT NULL, hostname varchar(55) NOT NULL, last_updated timestamp DEFAULT CURRENT_TIMESTAMP, UNIQUE(server_name, hostname))");
        this.getConnection().execute("INSERT INTO " + this.tableServerRegister + " VALUES ('" + this.plugin.getSettingsConfig().getMySQLSetting(MySQLSettings.SERVER_NAME) + "', '" + this.getHostname() + "', CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE last_updated=CURRENT_TIMESTAMP");
        this.plugin.debugMessage("Loaded online server register.");
    }

    private String getHostname() {
        return this.plugin.getServer().getIp() + ":" + this.plugin.getServer().getPort();
    }

    private void loadPlayerData() {
        this.getConnection().execute("CREATE TABLE IF NOT EXISTS " + this.tablePlayerdataStorageCompletedPaths + "(server_name varchar(36) NOT NULL, uuid varchar(36) NOT NULL, completed_path varchar(36) NOT NULL, UNIQUE(server_name, uuid, completed_path))");
        this.plugin.debugMessage("Loaded online playerdata storage.");
        this.updateCacheFromRemote();
    }

    private void updateCacheFromRemote() {
        Optional<ResultSet> resultSet = this.getConnection().executeQuery("SELECT * FROM " + this.tablePlayerdataStorageCompletedPaths + " ORDER BY uuid");
        if (!resultSet.isPresent()) {
            this.plugin.debugMessage("Could not update cache of global player data storage because the connection is not valid.");
        } else {
            ResultSet result = resultSet.get();

            while(true) {
                try {
                    if (!result.next()) {
                        break;
                    }
                } catch (SQLException var8) {
                    var8.printStackTrace();
                }

                String serverName = null;
                String completedPath = null;
                UUID uuid = null;

                try {
                    serverName = result.getString("server_name");
                    uuid = UUID.fromString(result.getString("uuid"));
                    completedPath = result.getString("completed_path");
                } catch (SQLException var7) {
                    var7.printStackTrace();
                }

                if (uuid != null && serverName != null && completedPath != null) {
                    CachedPlayerData cachedPlayerData = this.playerDataCache.getCachedPlayerData(uuid);
                    cachedPlayerData.addCachedEntry(completedPath, serverName);
                }
            }

            this.getConnection().close(null, null, result);
        }
    }

    public Collection<Integer> getCompletedRequirements(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    public boolean hasCompletedRequirement(UUID uuid, String pathName, int requirementId) {
        throw new UnsupportedOperationException();
    }

    public void addCompletedRequirement(UUID uuid, String pathName, int requirementId) {
        throw new UnsupportedOperationException();
    }

    public void setCompletedRequirements(UUID uuid, String pathName, Collection<Integer> requirements) {
        throw new UnsupportedOperationException();
    }

    public Collection<Integer> getCompletedRequirementsWithMissingResults(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    public void addCompletedRequirementWithMissingResults(UUID uuid, String pathName, int requirementId) {
        throw new UnsupportedOperationException();
    }

    public void removeCompletedRequirementWithMissingResults(UUID uuid, String pathName, int requirementId) {
        throw new UnsupportedOperationException();
    }

    public boolean hasCompletedRequirementWithMissingResults(UUID uuid, String pathName, int requirementId) {
        throw new UnsupportedOperationException();
    }

    public Collection<Integer> getCompletedPrerequisites(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    public boolean hasCompletedPrerequisite(UUID uuid, String pathName, int prerequisiteId) {
        throw new UnsupportedOperationException();
    }

    public void addCompletedPrerequisite(UUID uuid, String pathName, int prerequisiteId) {
        throw new UnsupportedOperationException();
    }

    public void setCompletedPrerequisites(UUID uuid, String pathName, Collection<Integer> prerequisites) {
        throw new UnsupportedOperationException();
    }

    public Collection<String> getChosenPathsWithMissingResults(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    public void addChosenPathWithMissingResults(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    public void removeChosenPathWithMissingResults(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    public boolean hasChosenPathWithMissingResults(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    public Collection<String> getActivePaths(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    public boolean hasActivePath(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    public void addActivePath(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    public void removeActivePath(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    public void setActivePaths(UUID uuid, Collection<String> paths) {
        throw new UnsupportedOperationException();
    }

    public Collection<String> getCompletedPaths(UUID uuid) {
        return null;
    }

    public boolean hasCompletedPath(@NonNull UUID uuid, @NonNull String completedPath) {
        return this.playerDataCache.getCachedPlayerData(uuid).getCachedEntriesByPath(completedPath).size() > 0;
    }

    public void addCompletedPath(@NonNull UUID uuid, @NonNull String completedPath) {
        Validate.notNull(uuid);
        Validate.notNull(completedPath);
        String serverName = this.plugin.getSettingsConfig().getMySQLSetting(MySQLSettings.SERVER_NAME);
        this.getConnection().execute("INSERT INTO " + this.tablePlayerdataStorageCompletedPaths + " VALUES ('" + serverName + "', '" + uuid + "', '" + completedPath + "') ON DUPLICATE KEY UPDATE uuid=uuid;");
        this.getConnection().execute("UPDATE " + this.tableServerRegister + " SET last_updated = CURRENT_TIMESTAMP WHERE server_name = '" + serverName + "';");
    }

    public void removeCompletedPath(UUID uuid, String pathName) {
        String serverName = this.plugin.getSettingsConfig().getMySQLSetting(MySQLSettings.SERVER_NAME);
        this.getConnection().execute("DELETE FROM " + this.tablePlayerdataStorageCompletedPaths + " WHERE uuid='" + uuid.toString() + "' AND server_name='" + serverName + "' AND completed_path='" + pathName + "';");
    }

    public void setCompletedPaths(UUID uuid, Collection<String> paths) {
        String serverName = this.plugin.getSettingsConfig().getMySQLSetting(MySQLSettings.SERVER_NAME);
        this.getConnection().execute("DELETE FROM " + this.tablePlayerdataStorageCompletedPaths + " WHERE uuid='" + uuid.toString() + "' AND server_name='" + serverName + "';");
        paths.forEach((completedPath) -> {
            this.addCompletedPath(uuid, completedPath);
        });
    }

    public int getTimesCompletedPath(UUID uuid, String pathName) {
        return this.hasCompletedPath(uuid, pathName) ? 1 : 0;
    }

    public Optional<Long> getTimeSinceCompletionOfPath(UUID uuid, String pathName) {
        return Optional.empty();
    }

    public void resetProgressOfAllPaths(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    public void resetProgressOfPath(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    public Collection<String> getCompletedPathsWithMissingResults(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    public void addCompletedPathWithMissingResults(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    public void removeCompletedPathWithMissingResults(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    public boolean hasCompletedPathWithMissingResults(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    public boolean hasLeaderboardExemption(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    public void setLeaderboardExemption(UUID uuid, boolean value) {
        throw new UnsupportedOperationException();
    }

    public boolean hasAutoCheckingExemption(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    public void setAutoCheckingExemption(UUID uuid, boolean value) {
        throw new UnsupportedOperationException();
    }

    public boolean hasTimeAdditionExemption(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    public void setTimeAdditionExemption(UUID uuid, boolean value) {
        throw new UnsupportedOperationException();
    }

    public PlayerDataStorageType getDataStorageType() {
        return PlayerDataStorageType.GLOBAL;
    }
}
