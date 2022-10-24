package me.armar.plugins.autorank.pathbuilder.playerdata.local;

import io.reactivex.annotations.NonNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.config.AbstractConfig;
import me.armar.plugins.autorank.pathbuilder.playerdata.PlayerDataStorage;
import me.armar.plugins.autorank.pathbuilder.playerdata.PlayerDataManager.PlayerDataStorageType;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class LocalPlayerDataStorage extends AbstractConfig implements PlayerDataStorage {
    private boolean convertingData = false;

    public LocalPlayerDataStorage(Autorank instance) {
        this.setPlugin(instance);
        this.setFileName("/playerdata/PlayerData.yml");
        this.getPlugin().getServer().getScheduler().runTaskTimerAsynchronously(this.getPlugin(), new Runnable() {
            public void run() {
                LocalPlayerDataStorage.this.getPlugin().debugMessage("Saving playerdata.yml file");
                LocalPlayerDataStorage.this.saveConfig();
            }
        }, 600L, 600L);
        this.getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(this.getPlugin(), new Runnable() {
            public void run() {
                LocalPlayerDataStorage.this.getPlugin().debugMessage("Trying to convert paths file format (if it is outdated)");
                LocalPlayerDataStorage.this.convertFormatToSupportMultiplePathsFormat();
            }
        }, 200L);
        this.loadConfig();
    }

    public Collection<Integer> getCompletedRequirements(UUID uuid, String pathName) {
        ConfigurationSection section = this.getProgressOnPathSection(uuid, pathName);
        return section == null ? new ArrayList() : section.getIntegerList("completed requirements");
    }

    public boolean hasCompletedRequirement(UUID uuid, String pathName, int reqId) {
        return this.getCompletedRequirements(uuid, pathName).contains(reqId);
    }

    public void addCompletedRequirement(UUID uuid, String pathName, int reqId) {
        if (!this.hasCompletedRequirement(uuid, pathName, reqId)) {
            Collection<Integer> completedRequirements = this.getCompletedRequirements(uuid, pathName);
            completedRequirements.add(reqId);
            this.setCompletedRequirements(uuid, pathName, completedRequirements);
        }
    }

    public void setCompletedRequirements(UUID uuid, String pathName, Collection<Integer> requirements) {
        this.getProgressOnPathsSection(uuid).set(pathName + ".completed requirements", requirements);
    }

    public Collection<Integer> getCompletedPrerequisites(UUID uuid, String pathName) {
        ConfigurationSection section = this.getProgressOnPathSection(uuid, pathName);
        return section == null ? new ArrayList() : section.getIntegerList("completed prerequisites");
    }

    public boolean hasCompletedPrerequisite(UUID uuid, String pathName, int preReqId) {
        return this.getCompletedPrerequisites(uuid, pathName).contains(preReqId);
    }

    public void addCompletedPrerequisite(UUID uuid, String pathName, int preReqId) {
        if (!this.hasCompletedPrerequisite(uuid, pathName, preReqId)) {
            Collection<Integer> completedPrerequisites = this.getCompletedPrerequisites(uuid, pathName);
            completedPrerequisites.add(preReqId);
            this.setCompletedPrerequisites(uuid, pathName, completedPrerequisites);
        }
    }

    public void setCompletedPrerequisites(UUID uuid, String pathName, Collection<Integer> prerequisites) {
        this.getProgressOnPathsSection(uuid).set(pathName + ".completed prerequisites", prerequisites);
    }

    public void convertNamesToUUIDs() {
        if (!this.convertingData) {
            this.convertingData = true;
            this.getPlugin().getLogger().info("Starting to convert playerdata.yml");
            this.getPlugin().getServer().getScheduler().runTaskAsynchronously(this.getPlugin(), new Runnable() {
                public void run() {
                    LocalPlayerDataStorage.this.getPlugin().getBackupManager().backupFile("/playerdata/playerdata.yml", null);
                    LocalPlayerDataStorage.this.getPlugin().debugMessage("Trying to convert playernames to UUIDs");
                    Iterator var1 = LocalPlayerDataStorage.this.getConfig().getKeys(false).iterator();

                    while(var1.hasNext()) {
                        String name = (String)var1.next();
                        if (!name.contains("-")) {
                            UUID uuid = null;

                            try {
                                uuid = UUIDManager.getUUID(name).get();
                            } catch (ExecutionException | InterruptedException var6) {
                                var6.printStackTrace();
                            }

                            if (uuid != null) {
                                List<Integer> progress = LocalPlayerDataStorage.this.getConfig().getIntegerList(name + ".progress");
                                String lastKnownGroup = LocalPlayerDataStorage.this.getConfig().getString(name + ".last group");
                                LocalPlayerDataStorage.this.getConfig().set(name, null);
                                LocalPlayerDataStorage.this.getConfig().set(uuid + ".progress", progress);
                                LocalPlayerDataStorage.this.getConfig().set(uuid + ".last group", lastKnownGroup);
                            }
                        }
                    }

                    LocalPlayerDataStorage.this.getPlugin().getLogger().info("Converted playerdata.yml to UUID format");
                }
            });
        }
    }

    private void convertFormatToSupportMultiplePathsFormat() {
        this.getPlugin().debugMessage("Looking for UUIDs to convert in PlayerData.yml file!");
        int convertedUUIDCount = 0;
        Iterator var2 = this.getConfig().getKeys(false).iterator();

        while(true) {
            String uuidString;
            UUID uuid;
            String chosenPath;
            do {
                if (!var2.hasNext()) {
                    this.getPlugin().debugMessage("Converted " + convertedUUIDCount + " uuids to new format.");
                    this.saveConfig();
                    return;
                }

                uuidString = (String)var2.next();
                uuid = UUID.fromString(uuidString);
                chosenPath = this.getConfig().getString(uuidString + ".chosen path");
            } while(chosenPath == null);

            this.getPlugin().debugMessage("Converting UUID " + uuidString + "...");
            this.addActivePath(uuid, chosenPath);
            Iterator var6 = this.getConfig().getIntegerList(uuidString + ".completed requirements").iterator();

            while(var6.hasNext()) {
                int completedRequirementId = (Integer)var6.next();
                this.addCompletedRequirement(uuid, chosenPath, completedRequirementId);
            }

            List<String> completedPaths = this.getConfig().getStringList(uuidString + ".completed paths");
            this.getConfig().set(uuidString + ".completed paths", null);
            Iterator var10 = completedPaths.iterator();

            while(var10.hasNext()) {
                String completedPathName = (String)var10.next();
                this.addCompletedPath(uuid, completedPathName);
            }

            this.getConfig().set(uuidString + ".chosen path", null);
            this.getConfig().set(uuidString + ".started paths", null);
            this.getConfig().set(uuidString + ".completed requirements", null);
            ++convertedUUIDCount;
        }
    }

    public Collection<String> getActivePaths(UUID uuid) {
        ConfigurationSection section = this.getActivePathsSection(uuid);
        return section.getKeys(false);
    }

    public boolean hasActivePath(UUID uuid, String pathName) {
        return this.getActivePaths(uuid).contains(pathName);
    }

    public void addActivePath(UUID uuid, String pathName) {
        if (!this.hasActivePath(uuid, pathName)) {
            ConfigurationSection activePathsSection = this.getActivePathsSection(uuid);
            activePathsSection.set(pathName + ".started", System.currentTimeMillis());
        }
    }

    public void setActivePaths(UUID uuid, Collection<String> paths) {
        ConfigurationSection activePathsSection = this.getActivePathsSection(uuid);
        Iterator var4 = paths.iterator();

        while(var4.hasNext()) {
            String pathName = (String)var4.next();
            activePathsSection.set(pathName + ".started", System.currentTimeMillis());
        }

    }

    public void removeActivePath(UUID uuid, String pathName) {
        if (this.hasActivePath(uuid, pathName)) {
            ConfigurationSection activePathsSection = this.getActivePathsSection(uuid);
            activePathsSection.set(pathName, null);
        }
    }

    public Collection<String> getCompletedPaths(UUID uuid) {
        ConfigurationSection section = this.getCompletedPathsSection(uuid);
        return section.getKeys(false);
    }

    public boolean hasCompletedPath(UUID uuid, String pathName) {
        return this.getCompletedPaths(uuid).contains(pathName);
    }

    public void addCompletedPath(UUID uuid, String pathName) {
        ConfigurationSection completedPathSection = this.getCompletedPathSection(uuid, pathName);
        if (completedPathSection == null) {
            this.getCompletedPathsSection(uuid).set(pathName + ".completed", 1);
        } else {
            completedPathSection.set("completed", completedPathSection.getInt("completed", 0) + 1);
        }

        this.getCompletedPathSection(uuid, pathName).set("completed at", System.currentTimeMillis());
    }

    public void removeCompletedPath(UUID uuid, String pathName) {
        if (this.hasCompletedPath(uuid, pathName)) {
            ConfigurationSection section = this.getCompletedPathsSection(uuid);
            section.set(pathName, null);
        }
    }

    public void setCompletedPaths(UUID uuid, Collection<String> paths) {
        ConfigurationSection section = this.getCompletedPathsSection(uuid);
        this.getCompletedPaths(uuid).forEach((completedPath) -> {
            section.set(completedPath, null);
        });
        paths.forEach((completedPath) -> {
            this.addCompletedPath(uuid, completedPath);
        });
    }

    public int getTimesCompletedPath(UUID uuid, String pathName) {
        ConfigurationSection completedPathSection = this.getCompletedPathSection(uuid, pathName);
        return completedPathSection != null ? completedPathSection.getInt("completed", 0) : 0;
    }

    public Optional<Long> getTimeSinceCompletionOfPath(UUID uuid, String pathName) {
        ConfigurationSection completedPathSection = this.getCompletedPathSection(uuid, pathName);
        if (completedPathSection == null) {
            return Optional.empty();
        } else {
            long completionTime = completedPathSection.getLong("completed at");
            return Optional.of((System.currentTimeMillis() - completionTime) / 60000L);
        }
    }

    public void resetProgressOfAllPaths(UUID uuid) {
        ConfigurationSection pathProgressSection = this.getProgressOnPathsSection(uuid);
        Iterator var3 = pathProgressSection.getKeys(false).iterator();

        while(var3.hasNext()) {
            String path = (String)var3.next();
            this.resetProgressOfPath(uuid, path);
        }

        this.saveConfig();
    }

    public void resetProgressOfPath(UUID uuid, String pathName) {
        ConfigurationSection pathProgressSection = this.getProgressOnPathsSection(uuid);
        pathProgressSection.set(pathName, null);
    }

    public Collection<String> getCompletedPathsWithMissingResults(@NonNull UUID uuid) {
        ConfigurationSection section = this.getResultsNotPerformedSection(uuid);
        return section.getStringList("completed paths");
    }

    public void addCompletedPathWithMissingResults(@NonNull UUID uuid, @NonNull String pathName) {
        Collection<String> completedPaths = this.getCompletedPathsWithMissingResults(uuid);
        completedPaths.add(pathName);
        this.getResultsNotPerformedSection(uuid).set("completed paths", completedPaths);
    }

    public void removeCompletedPathWithMissingResults(@NonNull UUID uuid, @NonNull String pathName) {
        Collection<String> completedPaths = this.getCompletedPathsWithMissingResults(uuid);
        completedPaths.remove(pathName);
        this.getResultsNotPerformedSection(uuid).set("completed paths", completedPaths);
    }

    public boolean hasCompletedPathWithMissingResults(@NonNull UUID uuid, @NonNull String pathName) {
        return this.getCompletedPathsWithMissingResults(uuid).contains(pathName);
    }

    public List<Integer> getCompletedRequirementsWithMissingResults(@NonNull UUID uuid, @NonNull String pathName) {
        ConfigurationSection section = this.getCompletedRequirementsMissingResultsSection(uuid);
        return section.getIntegerList(pathName);
    }

    public void addCompletedRequirementWithMissingResults(@NonNull UUID uuid, @NonNull String pathName, @NonNull int requirementId) {
        List<Integer> completedRequirements = this.getCompletedRequirementsWithMissingResults(uuid, pathName);
        completedRequirements.add(requirementId);
        this.getCompletedRequirementsMissingResultsSection(uuid).set(pathName, completedRequirements);
    }

    public void removeCompletedRequirementWithMissingResults(@NonNull UUID uuid, @NonNull String pathName, @NonNull int requirementId) {
        List<Integer> completedRequirements = this.getCompletedRequirementsWithMissingResults(uuid, pathName);
        completedRequirements.remove(requirementId);
        this.getCompletedRequirementsMissingResultsSection(uuid).set(pathName, completedRequirements);
    }

    public boolean hasCompletedRequirementWithMissingResults(@NonNull UUID uuid, @NonNull String pathName, @NonNull int requirementId) {
        return this.getCompletedRequirementsWithMissingResults(uuid, pathName).contains(requirementId);
    }

    public Collection<String> getChosenPathsWithMissingResults(@NonNull UUID uuid) {
        ConfigurationSection section = this.getResultsNotPerformedSection(uuid);
        return section.getStringList("chosen paths");
    }

    public void addChosenPathWithMissingResults(@NonNull UUID uuid, @NonNull String pathName) {
        Collection<String> chosenPaths = this.getChosenPathsWithMissingResults(uuid);
        chosenPaths.add(pathName);
        this.getResultsNotPerformedSection(uuid).set("chosen paths", chosenPaths);
    }

    public void removeChosenPathWithMissingResults(@NonNull UUID uuid, @NonNull String pathName) {
        Collection<String> chosenPaths = this.getChosenPathsWithMissingResults(uuid);
        chosenPaths.remove(pathName);
        this.getResultsNotPerformedSection(uuid).set("chosen paths", chosenPaths);
    }

    public boolean hasChosenPathWithMissingResults(@NonNull UUID uuid, @NonNull String pathName) {
        return this.getChosenPathsWithMissingResults(uuid).contains(pathName);
    }

    public boolean hasLeaderboardExemption(UUID uuid) {
        return this.getPlayerSection(uuid).getBoolean("exempt leaderboard", false);
    }

    public void setLeaderboardExemption(UUID uuid, boolean value) {
        this.getPlayerSection(uuid).set("exempt leaderboard", value);
    }

    public boolean hasAutoCheckingExemption(UUID uuid) {
        return this.getPlayerSection(uuid).getBoolean("exempted from checking", false);
    }

    public void setAutoCheckingExemption(UUID uuid, boolean value) {
        this.getPlayerSection(uuid).set("exempted from checking", value);
    }

    public boolean hasTimeAdditionExemption(UUID uuid) {
        return this.getPlayerSection(uuid).getBoolean("exempted from time addition", false);
    }

    public void setTimeAdditionExemption(UUID uuid, boolean value) {
        this.getPlayerSection(uuid).set("exempted from time addition", value);
    }

    public PlayerDataStorageType getDataStorageType() {
        return PlayerDataStorageType.LOCAL;
    }

    @NotNull
    private ConfigurationSection getPlayerSection(@NotNull UUID uuid) {
        ConfigurationSection playerSection = this.getConfig().getConfigurationSection(uuid.toString());
        if (playerSection == null) {
            playerSection = this.getConfig().createSection(uuid.toString());
        }

        return playerSection;
    }

    @NotNull
    private ConfigurationSection getActivePathsSection(UUID uuid) {
        ConfigurationSection playerSection = this.getPlayerSection(uuid);
        ConfigurationSection activePathsSection = playerSection.getConfigurationSection("active paths");
        if (activePathsSection == null) {
            activePathsSection = playerSection.createSection("active paths");
        }

        return activePathsSection;
    }

    private ConfigurationSection getActivePathSection(UUID uuid, String pathName) {
        ConfigurationSection section = this.getActivePathsSection(uuid);
        return section.getConfigurationSection(pathName);
    }

    @NotNull
    private ConfigurationSection getCompletedPathsSection(UUID uuid) {
        ConfigurationSection playerSection = this.getPlayerSection(uuid);
        ConfigurationSection completedPathsSection = playerSection.getConfigurationSection("completed paths");
        if (completedPathsSection == null) {
            completedPathsSection = playerSection.createSection("completed paths");
        }

        return completedPathsSection;
    }

    private ConfigurationSection getCompletedPathSection(UUID uuid, String pathName) {
        ConfigurationSection section = this.getCompletedPathsSection(uuid);
        return section.getConfigurationSection(pathName);
    }

    @NotNull
    private ConfigurationSection getProgressOnPathsSection(UUID uuid) {
        ConfigurationSection playerSection = this.getPlayerSection(uuid);
        ConfigurationSection progressSection = playerSection.getConfigurationSection("progress on paths");
        if (progressSection == null) {
            progressSection = playerSection.createSection("progress on paths");
        }

        return progressSection;
    }

    private ConfigurationSection getProgressOnPathSection(UUID uuid, String pathName) {
        ConfigurationSection section = this.getProgressOnPathsSection(uuid);
        return section.getConfigurationSection(pathName);
    }

    @NotNull
    private ConfigurationSection getResultsNotPerformedSection(UUID uuid) {
        ConfigurationSection section = this.getPlayerSection(uuid);
        ConfigurationSection returnValue = section.getConfigurationSection("results not performed");
        if (returnValue == null) {
            returnValue = section.createSection("results not performed");
        }

        return returnValue;
    }

    @NotNull
    private ConfigurationSection getCompletedRequirementsMissingResultsSection(UUID uuid) {
        ConfigurationSection section = this.getResultsNotPerformedSection(uuid);
        ConfigurationSection returnValue = section.getConfigurationSection("completed requirements");
        if (returnValue == null) {
            returnValue = section.createSection("completed requirements");
        }

        return returnValue;
    }
}
