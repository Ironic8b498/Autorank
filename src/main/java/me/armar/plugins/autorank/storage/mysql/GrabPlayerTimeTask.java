package me.armar.plugins.autorank.storage.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import static org.bukkit.Bukkit.getLogger;

public class GrabPlayerTimeTask implements Callable<Integer> {
    private final SQLConnection mysql;
    private final String table;
    private final UUID uuid;

    public GrabPlayerTimeTask(SQLConnection mysql, UUID uuid, String table) {
        this.mysql = mysql;
        this.uuid = uuid;
        this.table = table;
    }

    public Integer call() {
        if (this.mysql == null) {
            return -1;
        } else {
            int time = -1;
            String statement = "SELECT * FROM " + this.table + " WHERE uuid='" + this.uuid.toString() + "'";
            Optional<ResultSet> rs = this.mysql.executeQuery(statement);
            if (!rs.isPresent()) {
                return time;
            } else {
                try {
                    if (!rs.get().next()) {
                        return time;
                    }

                    time = rs.get().getInt(2);
                    rs.get().close();
                } catch (SQLException var5) {
                    getLogger().info("SQLException: " + var5.getMessage());
                    getLogger().info("SQLState: " + var5.getSQLState());
                    getLogger().info("VendorError: " + var5.getErrorCode());
                }

                return time;
            }
        }
    }
}
