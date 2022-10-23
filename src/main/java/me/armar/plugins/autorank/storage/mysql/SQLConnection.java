package me.armar.plugins.autorank.storage.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.armar.plugins.autorank.config.SettingsConfig;
import me.armar.plugins.autorank.config.SettingsConfig.MySQLSettings;
import static org.bukkit.Bukkit.getLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

public class SQLConnection {
    private static SQLConnection instance;
    private final String database;
    private final String hostname;
    private final String password;
    private final String username;
    private final String useSSL;
    private HikariDataSource dataSource = null;

    private SQLConnection(String hostname, String username, String password, String database, String useSSL) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.database = database;
        this.useSSL = useSSL;
    }

    public static synchronized SQLConnection getInstance(SettingsConfig configHandler) {
        if (instance == null) {
            String hostname = configHandler.getMySQLSetting(MySQLSettings.HOSTNAME);
            String username = configHandler.getMySQLSetting(MySQLSettings.USERNAME);
            String password = configHandler.getMySQLSetting(MySQLSettings.PASSWORD);
            String database = configHandler.getMySQLSetting(MySQLSettings.DATABASE);
            String useSSL = configHandler.getMySQLSetting(MySQLSettings.USESSL);
            instance = new SQLConnection(hostname, username, password, database, useSSL);
        }

        return instance;
    }

    public void closeConnection() {
        if (this.dataSource != null && !this.dataSource.isClosed()) {
            this.dataSource.close();
        }

    }

    public boolean connect() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("autorank-hikari");
        config.setJdbcUrl("jdbc:mysql://" + this.hostname + "/" + this.database);
        config.setUsername(this.username);
        config.setPassword(this.password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        config.addDataSourceProperty("autoReconnect", "true");
        config.addDataSourceProperty("useSSL", this.useSSL);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(10);
        config.setIdleTimeout(300000L);
        config.setMaxLifetime(600000L);
        config.setConnectionTimeout(5000L);
        config.setInitializationFailTimeout(-1L);

        try {
            this.dataSource = new HikariDataSource(config);
            return this.isConnected();
        } catch (Exception var3) {
            return false;
        }
    }

    public void execute(String sql) {
        PreparedStatement stmt = null;
        if (this.isConnected()) {
            try {
                Connection connection = this.getConnection().get();
                Throwable var4 = null;

                try {
                    stmt = connection.prepareStatement(sql);
                    stmt.executeUpdate();
                } catch (Throwable var22) {
                    var4 = var22;
                    throw var22;
                } finally {
                    if (connection != null) {
                        if (var4 != null) {
                            try {
                                connection.close();
                            } catch (Throwable var21) {
                                var4.addSuppressed(var21);
                            }
                        } else {
                            connection.close();
                        }
                    }

                }
            } catch (SQLException var24) {
                getLogger().info("SQLDataStorage.execute");
                getLogger().info("SQLException: " + var24.getMessage());
                getLogger().info("SQLState: " + var24.getSQLState());
                getLogger().info("VendorError: " + var24.getErrorCode());
            } finally {
                this.close(null, stmt, null);
            }

        }
    }

    public void executeQueries(Collection<String> queries) {
        Iterator var2 = queries.iterator();

        while(var2.hasNext()) {
            String query = (String)var2.next();
            this.execute(query);
        }

    }

    public Optional<ResultSet> executeQuery(String sql) {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        if (!this.isConnected()) {
            return Optional.empty();
        } else {
            try {
                Connection connection = this.getConnection().get();
                Throwable var5 = null;

                Optional var6;
                try {
                    stmt = connection.prepareStatement(sql);
                    rs = stmt.executeQuery();
                    var6 = Optional.of(rs);
                } catch (Throwable var16) {
                    var5 = var16;
                    throw var16;
                } finally {
                    if (connection != null) {
                        if (var5 != null) {
                            try {
                                connection.close();
                            } catch (Throwable var15) {
                                var5.addSuppressed(var15);
                            }
                        } else {
                            connection.close();
                        }
                    }

                }

                return var6;
            } catch (SQLException var18) {
                getLogger().info("SQLDataStorage.execute");
                getLogger().info("SQLException: " + var18.getMessage());
                getLogger().info("SQLState: " + var18.getSQLState());
                getLogger().info("VendorError: " + var18.getErrorCode());
                return Optional.empty();
            }
        }
    }

    public boolean isClosed() {
        return this.dataSource == null || this.dataSource.isClosed();
    }

    public Optional<Connection> getConnection() {
        try {
            Connection connection = this.dataSource.getConnection();
            if (connection == null) {
                return Optional.empty();
            } else if (connection.isValid(5)) {
                return Optional.of(connection);
            } else {
                connection.close();
                return Optional.empty();
            }
        } catch (SQLException var2) {
            var2.printStackTrace();
            return Optional.empty();
        }
    }

    public boolean isConnected() {
        Optional<Connection> connection = this.getConnection();
        boolean isValid = connection.isPresent();
        connection.ifPresent((c) -> {
            try {
                c.close();
            } catch (SQLException var2) {
                var2.printStackTrace();
            }

        });
        return isValid;
    }

    public void close(Connection conn, PreparedStatement ps, ResultSet res) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException var7) {
            }
        }

        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException var6) {
            }
        }

        if (res != null) {
            try {
                res.close();
            } catch (SQLException var5) {
            }
        }

    }
}
