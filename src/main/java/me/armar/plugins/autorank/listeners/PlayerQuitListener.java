package me.armar.plugins.autorank.listeners;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitListener implements Listener {
    private final Autorank plugin;

    public PlayerQuitListener(Autorank instance) {
        this.plugin = instance;
    }

    @EventHandler(
            priority = EventPriority.HIGH
    )
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        this.plugin.getTaskManager().stopUpdatePlayTimeTask(uuid);
        long lastPlayTimeUpdate = this.plugin.getTaskManager().getLastPlayTimeUpdate(uuid);
        if (lastPlayTimeUpdate > 0L) {
            double difference = (double)(System.currentTimeMillis() - lastPlayTimeUpdate) / 1000.0D / 60.0D;
            if (difference > 1.0D) {
                int roundedDiff = (int)Math.round(difference);
                this.plugin.getPlayTimeStorageManager().addPlayerTime(uuid, roundedDiff);
                this.plugin.getTaskManager().setLastPlayTimeUpdate(uuid, -1L);
            }
        }

        this.plugin.getPlayerChecker().doOfflineExemptionChecks(event.getPlayer());
    }
}
