package me.armar.plugins.autorank.listeners;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.playerdata.PlayerDataStorage;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

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

        try {
            this.plugin.getUUIDStorage().storeUUID(player.getName(), player.getUniqueId()).get();
        } catch (ExecutionException | InterruptedException var4) {
            var4.printStackTrace();
        }

        this.plugin.getPlayerChecker().doOfflineExemptionChecks(player);
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.plugin.getPathManager().autoAssignPaths(player.getUniqueId());
            AutorankTools.consoleDeserialize(String.valueOf(this.plugin.getSettingsConfig().isAutomaticPathDisabled()));
            if (!this.plugin.getSettingsConfig().isAutomaticPathDisabled()) {
                this.plugin.getPlayerChecker().checkPlayer(player.getUniqueId());
            }
        });
        if (player.hasPermission("autorank.noticeonupdate")) {
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                if (this.plugin.getUpdateHandler().isUpdateAvailable()) {
                    this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                        player.sendMessage(ChatColor.GREEN + this.plugin.getName() + " " + this.plugin.getUpdateHandler().getUpdater().getLatestVersion() + ChatColor.GOLD + " is now available for download!");
                        player.sendMessage(ChatColor.GREEN + "Available at: " + ChatColor.GOLD + this.plugin.getUpdateHandler().getUpdater().getResourceURL());
                    }, 10L);
                }

            });
        }

        if (player.hasPermission("autorank.noticeonwarning") && this.plugin.getWarningManager().getHighestWarning() != null) {
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                this.plugin.getWarningManager().sendWarnings(player);
            }, 10L);
        }

        this.plugin.getTaskManager().startUpdatePlayTimeTask(player.getUniqueId());
        this.performPendingResults(player);
    }

    private void performPendingResults(Player player) {
        Optional<PlayerDataStorage> playerDataStorage = this.plugin.getPlayerDataManager().getPrimaryDataStorage();
        if (playerDataStorage.isPresent()) {
            Collection<String> joinedPaths = playerDataStorage.get().getChosenPathsWithMissingResults(player.getUniqueId());

            Iterator var4;
            String pathName;
            for(var4 = joinedPaths.iterator(); var4.hasNext(); playerDataStorage.get().removeChosenPathWithMissingResults(player.getUniqueId(), pathName)) {
                pathName = (String)var4.next();
                Path path = this.plugin.getPathManager().findPathByInternalName(pathName, false);
                if (path != null) {
                    path.performResultsUponChoosing(player);
                }
            }

            var4 = this.plugin.getPathManager().getAllPaths().iterator();

            while(var4.hasNext()) {
                Path path = (Path)var4.next();
                Collection<Integer> completedRequirements = playerDataStorage.get().getCompletedRequirementsWithMissingResults(player.getUniqueId(), path.getInternalName());

                for (int requirementId : completedRequirements) {
                    path.completeRequirement(player.getUniqueId(), requirementId);
                    playerDataStorage.get().removeCompletedRequirementWithMissingResults(player.getUniqueId(), path.getInternalName(), requirementId);
                }
            }

            Collection<String> completedPaths = playerDataStorage.get().getCompletedPathsWithMissingResults(player.getUniqueId());

            for(Iterator<String> var11 = completedPaths.iterator(); var11.hasNext(); playerDataStorage.get().removeCompletedPathWithMissingResults(player.getUniqueId(), pathName)) {
                pathName = (String)var11.next();
                Path path = this.plugin.getPathManager().findPathByInternalName(pathName, false);
                if (path != null) {
                    path.performResults(player);
                }
            }

        }
    }
}
