package me.armar.plugins.autorank.pathbuilder;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.api.events.PathCompletedEvent;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.builders.PathBuilder;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import me.armar.plugins.autorank.pathbuilder.playerdata.PlayerDataManager;
import me.armar.plugins.autorank.pathbuilder.playerdata.PlayerDataStorage;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PathManager {
    private final Autorank plugin;
    private PathBuilder builder;
    private List<Path> paths = new ArrayList();

    public PathManager(Autorank plugin) {
        this.plugin = plugin;
        this.setBuilder(new PathBuilder(plugin));
    }

    public List<String> debugPaths() {
        List<String> messages = new ArrayList();
        messages.add(" ------------------- Path debug info ------------------- ");
        Iterator var2 = this.paths.iterator();

        while(var2.hasNext()) {
            Path path = (Path)var2.next();
            String pathName = path.getInternalName();
            List<CompositeRequirement> requirements = path.getRequirements();
            List<CompositeRequirement> prerequisites = path.getPrerequisites();
            List<AbstractResult> abstractResults = path.getResults();
            int count = 1;
            messages.add("Path: " + pathName);
            messages.add("Display name: " + path.getDisplayName());
            messages.add("Prerequisites: ");

            Iterator var9;
            CompositeRequirement req;
            for(var9 = prerequisites.iterator(); var9.hasNext(); ++count) {
                req = (CompositeRequirement)var9.next();
                messages.add("    " + count + ". " + req.getDescription());
            }

            count = 1;
            messages.add("Requirements: ");

            for(var9 = requirements.iterator(); var9.hasNext(); ++count) {
                req = (CompositeRequirement)var9.next();
                messages.add("    " + count + ". " + req.getDescription());
            }

            count = 1;
            messages.add("Results: ");

            for(var9 = abstractResults.iterator(); var9.hasNext(); ++count) {
                AbstractResult res = (AbstractResult)var9.next();
                messages.add("    " + count + ". " + res.getDescription());
            }

            messages.add("----------------------------");
        }

        return messages;
    }

    public PathBuilder getBuilder() {
        return this.builder;
    }

    public void setBuilder(PathBuilder builder) {
        this.builder = builder;
    }

    public List<Path> getActivePaths(UUID uuid) {
        Collection<String> activePathNames = (Collection)this.plugin.getPlayerDataManager().getPrimaryDataStorage().map((storage) -> {
            return storage.getActivePaths(uuid);
        }).orElse(new ArrayList());
        List<Path> activePaths = new ArrayList();
        Iterator var4 = activePathNames.iterator();

        while(var4.hasNext()) {
            String activePathName = (String)var4.next();
            Path activePath = this.findPathByInternalName(activePathName, false);
            if (activePath != null) {
                activePaths.add(activePath);
            }
        }

        return activePaths;
    }

    public void resetProgressOnActivePaths(UUID uuid) {
        this.plugin.getPlayerDataManager().getPrimaryDataStorage().ifPresent((s) -> {
            s.setActivePaths(uuid, new ArrayList());
        });
    }

    public void resetProgressOfPath(Path path, UUID uuid) {
        this.plugin.getPlayerDataManager().getPrimaryDataStorage().ifPresent((s) -> {
            s.setCompletedPrerequisites(uuid, path.getInternalName(), new ArrayList());
            s.setCompletedRequirements(uuid, path.getInternalName(), new ArrayList());
        });
    }

    public void resetActivePaths(UUID uuid) {
        this.plugin.getPlayerDataManager().getPrimaryDataStorage().ifPresent((s) -> {
            s.getActivePaths(uuid).forEach((name) -> {
                s.removeActivePath(uuid, name);
            });
        });
    }

    public void resetAllProgress(UUID uuid) {
        this.plugin.getPlayerDataManager().getPrimaryDataStorage().ifPresent((s) -> {
            s.resetProgressOfAllPaths(uuid);
        });
    }

    public List<Path> getCompletedPaths(UUID uuid) {
        Collection<String> completedPathsNames = (Collection)this.plugin.getPlayerDataManager().getPrimaryDataStorage().map((s) -> {
            return s.getCompletedPaths(uuid);
        }).orElse(new ArrayList());
        List<Path> completedPaths = new ArrayList();
        Iterator var4 = completedPathsNames.iterator();

        while(var4.hasNext()) {
            String completedPathName = (String)var4.next();
            Path completedPath = this.findPathByInternalName(completedPathName, false);
            if (completedPath != null) {
                completedPaths.add(completedPath);
            }
        }

        return completedPaths;
    }

    public void resetCompletedPaths(UUID uuid) {
        this.plugin.getPlayerDataManager().getPrimaryDataStorage().ifPresent((s) -> {
            s.getCompletedPaths(uuid).forEach((name) -> {
                s.removeCompletedPath(uuid, name);
            });
        });
    }

    public void addCompletedRequirement(UUID uuid, Path path, int reqId) {
        this.plugin.getPlayerDataManager().getPrimaryDataStorage().ifPresent((s) -> {
            s.addCompletedRequirement(uuid, path.getInternalName(), reqId);
        });
    }

    public boolean hasCompletedRequirement(UUID uuid, Path path, int reqId) {
        return this.plugin.getPlayerDataManager().getPrimaryDataStorage().map((s) -> {
            return s.hasCompletedRequirement(uuid, path.getInternalName(), reqId);
        }).orElse(false);
    }

    public List<Path> getAllPaths() {
        return Collections.unmodifiableList(this.paths);
    }

    public List<Path> getEligiblePaths(UUID uuid) {
        List<Path> possibilities = new ArrayList();
        Iterator var3 = this.getAllPaths().iterator();

        while(var3.hasNext()) {
            Path path = (Path)var3.next();
            if (path.isEligible(uuid)) {
                possibilities.add(path);
            }
        }

        return possibilities;
    }

    public void initialiseFromConfigs() {
        this.paths.clear();
        List<Path> temp = this.builder.initialisePaths();
        if (temp != null && !temp.isEmpty()) {
            this.paths = temp;
            Iterator var2 = this.debugPaths().iterator();

            while(var2.hasNext()) {
                String message = (String)var2.next();
                this.plugin.debugMessage(message);
            }

        } else {
            this.plugin.getLogger().warning("The paths file was not configured correctly! See your log file for more info.");
        }
    }

    public Path findPathByDisplayName(String displayName, boolean isCaseSensitive) {
        Iterator var3 = this.getAllPaths().iterator();

        while(var3.hasNext()) {
            Path path = (Path)var3.next();
            if (isCaseSensitive) {
                if (path.getDisplayName().equals(displayName)) {
                    return path;
                }
            } else if (path.getDisplayName().equalsIgnoreCase(displayName)) {
                return path;
            }
        }

        return null;
    }

    public Path findPathByInternalName(String internalName, boolean isCaseSensitive) {
        Iterator var3 = this.getAllPaths().iterator();

        while(var3.hasNext()) {
            Path path = (Path)var3.next();
            if (isCaseSensitive) {
                if (path.getInternalName().equals(internalName)) {
                    return path;
                }
            } else if (path.getInternalName().equalsIgnoreCase(internalName)) {
                return path;
            }
        }

        return null;
    }

    public void assignPath(Path path, UUID uuid, boolean byForce) throws IllegalArgumentException {
        if (!path.isEligible(uuid) && !byForce) {
            throw new IllegalArgumentException("Path is not eligible, so cannot be assigned to the player!");
        } else {
            Optional<PlayerDataStorage> storage = this.plugin.getPlayerDataManager().getPrimaryDataStorage();
            if (storage.isPresent()) {
                String internalName = path.getInternalName();
                storage.get().addActivePath(uuid, internalName);
                if (!path.shouldStoreProgressOnDeactivation()) {
                    storage.get().setCompletedRequirements(uuid, internalName, new ArrayList());
                    storage.get().setCompletedPrerequisites(uuid, internalName, new ArrayList());
                }

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                if (offlinePlayer.getPlayer() == null) {
                    storage.get().addChosenPathWithMissingResults(uuid, internalName);
                } else {
                    path.performResultsUponChoosing(offlinePlayer.getPlayer());
                }

            }
        }
    }

    public void deassignPath(Path path, UUID uuid) {
        if (path.isActive(uuid)) {
            if (!path.shouldStoreProgressOnDeactivation()) {
                this.plugin.getPathManager().resetProgressOfPath(path, uuid);
            }

            this.plugin.getPlayerDataManager().getPrimaryDataStorage().ifPresent((s) -> {
                s.removeActivePath(uuid, path.getInternalName());
            });
        }
    }

    public List<Path> autoAssignPaths(UUID uuid) {
        this.plugin.debugMessage("Trying to assign paths to " + uuid);
        List<Path> assignedPaths = new ArrayList();
        Iterator var3 = this.getAllPaths().iterator();

        while(var3.hasNext()) {
            Path path = (Path)var3.next();
            this.plugin.debugMessage("Trying to assign path " + path.getDisplayName() + " to " + uuid);
            if (!path.isAutomaticallyAssigned()) {
                this.plugin.debugMessage(String.format("Path %s is not automatically assigned", path.getDisplayName()));
            } else if (!path.isEligible(uuid)) {
                this.plugin.debugMessage(String.format("Player '%s' is not eligible for path %s", uuid, path.getDisplayName()));
            } else if (path.isDeactivated(uuid)) {
                this.plugin.debugMessage(String.format("Path %s is deactivated", path.getDisplayName()));
            } else {
                this.assignPath(path, uuid, false);
                Player onlinePlayer = Bukkit.getOfflinePlayer(uuid).getPlayer();
                if (onlinePlayer != null) {
                    this.plugin.debugMessage("Assigned " + path.getDisplayName() + " to " + onlinePlayer.getName());
                    onlinePlayer.sendMessage(Lang.AUTOMATICALLY_ASSIGNED_PATH.getConfigValue(path.getDisplayName()));
                }

                assignedPaths.add(path);
            }
        }

        return assignedPaths;
    }

    public boolean completePath(Path path, UUID uuid) {
        Optional<PlayerDataStorage> storage = this.plugin.getPlayerDataManager().getPrimaryDataStorage();
        if (!storage.isPresent()) {
            return false;
        } else {
            storage.get().addCompletedPath(uuid, path.getInternalName());
            this.plugin.getPlayerDataManager().getDataStorage(PlayerDataManager.PlayerDataStorageType.GLOBAL).ifPresent((s) -> {
                s.addCompletedPath(uuid, path.getInternalName());
            });
            storage.get().removeActivePath(uuid, path.getInternalName());
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            Player player = offlinePlayer.getPlayer();
            boolean result = false;
            (new BukkitRunnable() {
                public void run() {
                    PathCompletedEvent completedEvent = new PathCompletedEvent(uuid, path);
                    Bukkit.getPluginManager().callEvent((Event)completedEvent);
                }
            }).runTaskAsynchronously((Plugin)this.plugin);
            if (player == null) {
                storage.get().addCompletedPathWithMissingResults(uuid, path.getInternalName());
            } else {
                result = path.performResults(player);
            }

            this.plugin.getPathManager().autoAssignPaths(uuid);
            return result;
        }
    }
}
