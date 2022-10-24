package me.armar.plugins.utils.pluginlibrary.listeners;

import com.vexsoftware.votifier.model.VotifierEvent;
import me.armar.plugins.utils.pluginlibrary.events.PlayerVotedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class VoteListener implements Listener {
    public VoteListener() {
    }

    @EventHandler(
            priority = EventPriority.MONITOR,
            ignoreCancelled = true
    )
    public void onVote(VotifierEvent event) {
        String userName = event.getVote().getUsername();
        Player player = Bukkit.getServer().getPlayer(userName);
        if (player != null) {
            PlayerVotedEvent playerVotedEvent = new PlayerVotedEvent(player);
            Bukkit.getPluginManager().callEvent(playerVotedEvent);
        }
    }
}
