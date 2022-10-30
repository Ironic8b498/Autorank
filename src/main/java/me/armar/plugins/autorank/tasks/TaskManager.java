package me.armar.plugins.autorank.tasks;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.playtimes.PlayTimeManager;
import me.armar.plugins.autorank.playtimes.UpdateTimePlayedTask;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaskManager {
    private final Autorank plugin;
    private final Map<UUID, Integer> updatePlayTimeTaskIds = new HashMap();
    private final Map<UUID, Long> lastPlayTimeUpdate = new HashMap();

    public TaskManager(Autorank plugin) {
        this.plugin = plugin;
    }

    public void startUpdatePlayTimeTask(UUID uuid) {
        if (!this.updatePlayTimeTaskIds.containsKey(uuid)) {
            BukkitTask task = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new UpdateTimePlayedTask(this.plugin, uuid), PlayTimeManager.INTERVAL_MINUTES * 1200L, PlayTimeManager.INTERVAL_MINUTES * 1200L);
            this.updatePlayTimeTaskIds.put(uuid, task.getTaskId());
            this.lastPlayTimeUpdate.put(uuid, System.currentTimeMillis());
            this.plugin.debugMessage("Registered update play time task for player " + uuid + " (" + task.getTaskId() + ").");
        }
    }

    public void stopUpdatePlayTimeTask(UUID uuid) {
        this.plugin.debugMessage("Stop update play time task for player " + uuid);
        if (this.updatePlayTimeTaskIds.containsKey(uuid)) {
            this.plugin.getServer().getScheduler().cancelTask(this.updatePlayTimeTaskIds.get(uuid));
            this.updatePlayTimeTaskIds.remove(uuid);
        }
    }

    public void setLastPlayTimeUpdate(UUID uuid, long value) {
        if (value < 0L) {
            this.lastPlayTimeUpdate.remove(uuid);
        }

        this.lastPlayTimeUpdate.put(uuid, value);
    }

    public long getLastPlayTimeUpdate(UUID uuid) {
        return !this.lastPlayTimeUpdate.containsKey(uuid) ? -1L : this.lastPlayTimeUpdate.get(uuid);
    }
}
