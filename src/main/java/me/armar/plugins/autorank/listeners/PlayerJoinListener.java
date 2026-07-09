package me.armar.plugins.autorank.listeners;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.playerdata.PlayerDataStorage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Optional;

public class PlayerJoinListener implements Listener {
    private final Autorank plugin;

    public PlayerJoinListener(Autorank instance) {
        this.plugin = instance;
    }

    @EventHandler(
            priority = EventPriority.HIGH
    )
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.plugin.getPlayerChecker().doOfflineExemptionChecks(player);
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.plugin.getPathManager().autoAssignPaths(player.getUniqueId());
            if (!this.plugin.getSettingsConfig().isAutomaticPathDisabled()) {
                this.plugin.getPlayerChecker().checkPlayer(player.getUniqueId());
            }

        });
        if (player.hasPermission("autorank.noticeonupdate")) {
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                if (this.plugin.getUpdateHandler().isUpdateAvailable()) {
                    this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                        ChatColor var10001 = ChatColor.GREEN;
                        player.sendMessage(var10001 + this.plugin.getName() + " " + this.plugin.getUpdateHandler().getUpdater().getLatestVersion() + ChatColor.GOLD + " is now available for download!");
                        player.sendMessage(var10001 + "Available at: " + ChatColor.GOLD + this.plugin.getUpdateHandler().getUpdater().getResourceURL());
                    }, 10L);
                }

            });
        }

        if (player.hasPermission("autorank.noticeonwarning") && this.plugin.getWarningManager().getHighestWarning() != null) {
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> this.plugin.getWarningManager().sendWarnings(player), 10L);
        }

        this.plugin.getTaskManager().startUpdatePlayTimeTask(player.getUniqueId());
        this.performPendingResults(player);
    }

    private void performPendingResults(Player player) {
        Optional<PlayerDataStorage> playerDataStorage = this.plugin.getPlayerDataManager().getPrimaryDataStorage();
        if (playerDataStorage.isPresent()) {
            for(String pathName : playerDataStorage.get().getChosenPathsWithMissingResults(player.getUniqueId())) {
                Path path = this.plugin.getPathManager().findPathByInternalName(pathName, false);
                if (path != null) {
                    path.performResultsUponChoosing(player);
                }

                playerDataStorage.get().removeChosenPathWithMissingResults(player.getUniqueId(), pathName);
            }

            for(Path path : this.plugin.getPathManager().getAllPaths()) {
                for(int requirementId : playerDataStorage.get().getCompletedRequirementsWithMissingResults(player.getUniqueId(), path.getInternalName())) {
                    path.completeRequirement(player.getUniqueId(), requirementId);
                    playerDataStorage.get().removeCompletedRequirementWithMissingResults(player.getUniqueId(), path.getInternalName(), requirementId);
                }
            }

            for(String pathName : playerDataStorage.get().getCompletedPathsWithMissingResults(player.getUniqueId())) {
                Path path = this.plugin.getPathManager().findPathByInternalName(pathName, false);
                if (path != null) {
                    path.performResults(player);
                }

                playerDataStorage.get().removeCompletedPathWithMissingResults(player.getUniqueId(), pathName);
            }
        }

    }
}
