package me.armar.plugins.autorank.api;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.addons.AddOnManager;
import me.armar.plugins.autorank.api.services.RequirementManager;
import me.armar.plugins.autorank.api.services.RequirementService;
import me.armar.plugins.autorank.api.services.ResultManager;
import me.armar.plugins.autorank.api.services.ResultService;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.storage.PlayTimeStorageProvider;
import me.armar.plugins.autorank.storage.TimeType;
import org.bukkit.plugin.ServicePriority;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class API {
    private final Autorank plugin;

    public API(Autorank instance) {
        this.plugin = instance;
        this.plugin.getServer().getServicesManager().register(RequirementManager.class, new RequirementService(instance), instance, ServicePriority.Normal);
        this.plugin.getLogger().info("Registered requirement service for adding custom requirements!");
        this.plugin.getServer().getServicesManager().register(ResultManager.class, new ResultService(instance), instance, ServicePriority.Normal);
        this.plugin.getLogger().info("Registered result service for adding custom results!");
    }

    public AddOnManager getAddOnManager() {
        return this.plugin.getAddonManager();
    }

    public CompletableFuture<Integer> getGlobalPlayTime(UUID uuid) {
        return !this.plugin.getPlayTimeStorageManager().isStorageTypeActive(PlayTimeStorageProvider.StorageType.DATABASE) ? CompletableFuture.completedFuture(0) : this.plugin.getPlayTimeStorageManager().getStorageProvider(PlayTimeStorageProvider.StorageType.DATABASE).getPlayerTime(TimeType.TOTAL_TIME, uuid);
    }

    public CompletableFuture<Integer> getLocalPlayTime(UUID uuid) {
        return this.getPlayTime(TimeType.TOTAL_TIME, uuid);
    }

    /** @deprecated */
    @Deprecated
    public CompletableFuture<Integer> getPlayTime(TimeType timeType, UUID uuid) {
        return this.plugin.getPlayTimeManager().getPlayTime(timeType, uuid);
    }

    public CompletableFuture<Long> getPlayTime(TimeType timeType, UUID uuid, TimeUnit timeUnit) {
        return this.plugin.getPlayTimeManager().getPlayTime(timeType, uuid, timeUnit);
    }

    public List<Path> getActivePaths(UUID uuid) {
        return this.plugin.getPathManager().getActivePaths(uuid);
    }

    public List<Path> getCompletedPaths(UUID uuid) {
        return this.plugin.getPathManager().getCompletedPaths(uuid);
    }

    public List<Path> getEligiblePaths(UUID uuid) {
        return this.plugin.getPathManager().getEligiblePaths(uuid);
    }

    public Path getPath(String pathName) {
        return this.plugin.getPathManager().getAllPaths().parallelStream().filter((path) -> {
            return path.getDisplayName().equalsIgnoreCase(pathName) || path.getInternalName().equalsIgnoreCase(pathName);
        }).findFirst().orElse(null);
    }
}
