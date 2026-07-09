package me.armar.plugins.autorank.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class AFKListener implements Listener {
    public AFKListener() {
    }

    @EventHandler(
            priority = EventPriority.HIGH,
            ignoreCancelled = true
    )
    public void onPlayerMove(PlayerMoveEvent event) {
        assert event.getTo() != null;
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ() && event.getFrom().getBlockY() == event.getTo().getBlockY()) {
        }

    }
}
